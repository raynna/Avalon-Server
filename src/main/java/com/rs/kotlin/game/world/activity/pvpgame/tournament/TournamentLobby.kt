package com.rs.kotlin.game.world.activity.pvpgame.tournament

import com.rs.Settings
import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.core.thread.CoresManager
import com.rs.discord.DiscordAnnouncer
import com.rs.java.game.WorldTile
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.content.presets.Preset
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.player.command.CommandRegistry
import com.rs.kotlin.game.world.activity.pvpgame.PvPGameManager
import com.rs.kotlin.game.world.activity.pvpgame.activePvPGame
import com.rs.kotlin.game.world.activity.pvpgame.closePvPOverlay
import com.rs.kotlin.game.world.activity.pvpgame.showResult
import com.rs.kotlin.game.world.util.Msg

class TournamentLobby(private val instance: TournamentInstance) {

    private val tournamentPreset = TournamentPresetDefaults.random()

    fun getTournamentPreset(): Preset = tournamentPreset.preset
    fun getRules(): TournamentRules = tournamentPreset.rules

    private val participants = mutableSetOf<Player>()
    private val waitingPlayers = mutableListOf<Player>()

    private var joinPhase = true
    private var ticksRemaining = 30//500 original

    private var bestOfThree: Boolean = false
    private val finalScores = mutableMapOf<Player, Int>()

    init {
        Msg.world(
            Msg.GREEN,
            icon = 22,
            "News: Tournament Loadout: ${tournamentPreset.preset.name}"
        )

        DiscordAnnouncer.announce(
            "Tournament",
            "Loadout: ${tournamentPreset.preset.name}"
        )
        var lastAnnounced = -1
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                ticksRemaining--
                val secondsRemaining = (ticksRemaining * 600) / 1000
                if (secondsRemaining != lastAnnounced) {
                    when (secondsRemaining) {
                        180 -> {
                            Msg.world(Msg.GREEN, icon = 22, "News: A ${tournamentPreset.preset.name} tournament starts in 3 minutes!")
                            DiscordAnnouncer.announce("Tournament", "News: A ${tournamentPreset.preset.name} tournament starts in 3 minutes!")
                        }

                        120 -> {
                            Msg.world(Msg.GREEN, icon = 22, "News: A ${tournamentPreset.preset.name} tournament starts in 2 minutes!")
                            DiscordAnnouncer.announce("Tournament", "News: A ${tournamentPreset.preset.name} tournament starts in 2 minutes!")
                        }

                        60 -> {
                            Msg.world(Msg.GREEN, icon = 22, "News: A ${tournamentPreset.preset.name} tournament starts in 1 minutes!")
                            DiscordAnnouncer.announce("Tournament", "News: A ${tournamentPreset.preset.name} tournament starts in 1 minutes!")
                        }
                    }
                    lastAnnounced = secondsRemaining
                }
                if (ticksRemaining <= 0) {
                    joinPhase = false
                    if (waitingPlayers.size >= 2) {
                        scheduleNextMatch(true)
                        stop()
                    } else {
                        val allPlayers = participants.toList()
                        for (p in allPlayers) {
                            TournamentRecovery.restore(p)
                            p.message("The tournament didn't have enough players to start, you've been sent back home.")
                        }
                        instance.end(null)
                        stop()
                    }
                }
            }
        }, 0, 0)
    }

    fun addPlayer(player: Player) {
        if (!joinPhase) {
            player.message("The tournament has already started, you can’t join now.")
            return
        }
        if (participants.contains(player)) {
            player.message("You are already in the tournament.")
            return
        }
        if (player.familiar != null) {
            player.familiar.dissmissFamiliar(false)
        }
        participants.add(player)
        waitingPlayers.add(player)
        player.activeTournament = this.instance
        player.tempInventory = player.inventory.createSnapshot()
        player.tempEquipment = player.equipment.createSnapshot()
        player.tempXpSnapshot = DoubleArray(25)
        player.tempLevelSnapshot = ShortArray(25)
        player.tempWorldTile = WorldTile(player.x, player.y, player.plane)

        for (i in 0 until 25) {
            player.tempXpSnapshot[i] = player.skills.getXp(i)
            player.tempLevelSnapshot[i] = player.skills.getLevel(i).toShort()
        }
        player.inventory.reset()
        player.equipment.reset()

        preparePlayer(player)

        // convert ticks → seconds
        val secondsRemaining = (ticksRemaining * 600) / 1000
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60

        val timeStr = when {
            minutes > 0 && seconds > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} and $seconds second${if (seconds > 1) "s" else ""}"
            minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""}"
            else -> "$seconds second${if (seconds > 1) "s" else ""}"
        }

        player.message("You joined the ${tournamentPreset.preset.name} tournament lobby. The first match begins in $timeStr!")
        refreshInterface(ticksRemaining)
    }

    fun preparePlayer(player: Player) {
        val preset = getTournamentPreset()
        player.presetManager.applyPreset(preset)

        player.prayer.restorePrayer(player.skills.getLevelForXp(Skills.PRAYER) * 10)
        if (player.poison.isPoisoned) player.poison.reset()
        if (player.newPoison.isPoisoned()) player.newPoison.reset()
        player.setRunEnergy(100)
        player.heal(player.maxHitpoints)
        player.skills.restoreSkills()
        player.getAppearence().generateAppearenceData()
        player.skills[Skills.SUMMONING] = player.skills.getLevelForXp(Skills.SUMMONING)
        player.skills.refresh(Skills.SUMMONING)
        player.getCombatDefinitions().resetSpecialAttack()
    }

    fun onLeave(player: Player, restore: Boolean = true) {
        val game = player.activePvPGame
        if (game is TournamentGame) {
            forfeit(player)
            return
        }
        participants.remove(player)
        waitingPlayers.remove(player)

        if (restore) {
            TournamentRecovery.restore(player)
        }

        player.message("You have left the tournament and returned to your original position.")
    }

    fun forfeit(player: Player) {
        val game = player.activePvPGame
        if (game is TournamentGame) {
            game.handleForfeit(player)  // you'll add this
            return
        }
        waitingPlayers.remove(player)
        participants.remove(player)
        TournamentRecovery.restore(player)
        player.message("You have forfeited tournament.")
    }

    private fun refreshInterface(nextFight: Int) {
        val allPlayers = participants.toList()
        for (p in allPlayers) {
            if (p.hasFinished() || !p.hasStarted()) continue

            if (!p.interfaceManager.containsInterface(265)) {
                p.interfaceManager.sendOverlay(265, p.interfaceManager.hasRezizableScreen())
            }
            p.packets.sendGlobalVar(271, 2)//set text to participants on left side
            p.packets.sendGlobalVar(262, waitingPlayers.size)
            p.packets.sendGlobalVar(261, participants.size)
            p.packets.sendGlobalVar(260, 0)
            if (nextFight > 0) {
                p.packets.sendGlobalVar(270, nextFight) // ticks
            } else {
                p.packets.sendTextOnComponent(265, 16, "Fight active")
            }
        }
    }


    fun scheduleNextMatch(instant: Boolean) {
        WorldTasksManager.schedule(object : WorldTask() {
            var ticks = if (instant) 0 else 25//15 seconds
            override fun run() {
                refreshInterface(ticks)
                if (waitingPlayers.size < 2) {
                    stop(); return
                }
                if (--ticks <= 0) {
                    startMatch()
                    stop()
                }
            }
        }, 0, 0)
    }

    private fun startMatch() {

        println("[TOURNAMENT] startMatch called. waiting=${waitingPlayers.map { it.username }}")

        if (waitingPlayers.size < 2) return

        val (p1, p2) = waitingPlayers.shuffled().take(2)
        if (waitingPlayers.size == 2 && !bestOfThree) {

            println("[TOURNAMENT] FINAL MATCH ACTIVATED: ${p1.username} vs ${p2.username}")

            bestOfThree = true
            finalScores[p1] = 0
            finalScores[p2] = 0

            Msg.warn(p1,"Final match is best of 3!")
            Msg.warn(p2,"Final match is best of 3!")
        }

        waitingPlayers.removeAll(listOf(p1, p2))

        val game = TournamentGame(p1, p2, this)
        PvPGameManager.registerGame(game)
        game.start()
    }


    fun recordResult(winner: Player, loser: Player) {
        if (!participants.contains(winner)) return

        if (bestOfThree) {

            finalScores[winner] = (finalScores[winner] ?: 0) + 1

            val scoreW = finalScores[winner] ?: 0
            val scoreL = finalScores[loser] ?: 0
            if (scoreW >= 2) {
                winner.showResult(winner)
                loser.showResult(null)
                finishTournament(winner)
                return
            }
            winner.message("You won this round! Next fight starting soon")
            winner.message("You have $scoreW wins in the final (best of 3).")

            loser.message("You have lost this round! Next fight starting soon.")
            loser.message("You have $scoreL wins in the final (best of 3).")

            preparePlayer(winner)
            preparePlayer(loser)

            winner.nextWorldTile = getLobby1Tile()
            loser.nextWorldTile = getLobby1Tile()

            waitingPlayers.add(winner)
            waitingPlayers.add(loser)
            loser.interfaceManager.sendOverlay(265, false)//just to refresh interface
            winner.interfaceManager.sendOverlay(265, false)

            scheduleNextMatch(false)
            return
        }

        preparePlayer(winner)

        loser.nextWorldTile = getLobby2Tile()
        loser.message("You have been eliminated from this tournament.")
        loser.showResult(null)

        winner.nextWorldTile = getLobby1Tile()
        winner.message("You won this round! Returning to lobby...")

        waitingPlayers.add(winner)

        if (waitingPlayers.size >= 2) {
            scheduleNextMatch(false)
        }
    }


    fun finishTournament(champion: Player) {
        champion.message("Congratulations! You are the Tournament Champion!")

        val allPlayers = participants.toList()
        CoresManager.getSlowExecutor().execute {
            try {
                for (p in allPlayers) {
                    TournamentRecovery.restore(p)
                    p.message("The tournament has ended. You’ve been sent back home.")
                    if (champion == p) {
                        p.message("You've been rewarded a magic chest & 10m coins for winning the tournament")
                        p.moneyPouch.addMoney(10_000_000, false)
                        if (p.inventory.hasFreeSlots()) {
                            p.inventory.addItem("item.magic_chest", 1)
                        } else {
                            p.bank.addItem(Rscm.item("item.magic_chest"), 1, true)
                            p.message("You didn't have space in inventory for your reward, reward sent to bank.")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        instance.end(champion)

        participants.clear()
        waitingPlayers.clear()
        bestOfThree = false
        finalScores.clear()
    }

    fun onLogin(player: Player) {
        if (!joinPhase && waitingPlayers.isEmpty() && participants.isEmpty()) {
            TournamentRecovery.restore(player)
            return
        }

        if (waitingPlayers.contains(player) || participants.contains(player)) {
            player.activeTournament = null
        } else {
            TournamentRecovery.restore(player)
        }
    }

    fun onLogout(player: Player) {
        waitingPlayers.remove(player)
        participants.remove(player)
        TournamentRecovery.restore(player)
    }



    fun getLobby1Tile() = instance.getLobby1()
    fun getLobby2Tile() = instance.getLobby2()
    fun getFirstSpawn() = instance.getFirstSpawn()
    fun getSecondSpawn() = instance.getSecondSpawn()
}
