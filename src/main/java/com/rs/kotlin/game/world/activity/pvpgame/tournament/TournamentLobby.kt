import com.rs.Settings
import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.core.thread.CoresManager
import com.rs.discord.DiscordAnnouncer
import com.rs.java.game.WorldTile
import com.rs.java.game.player.Player
import com.rs.kotlin.game.world.activity.pvpgame.PvPGameManager
import com.rs.kotlin.game.world.activity.pvpgame.showResult
import com.rs.kotlin.game.world.activity.pvpgame.tournament.TournamentGame
import com.rs.kotlin.game.world.activity.pvpgame.tournament.TournamentInstance
import com.rs.kotlin.game.world.util.Msg

class TournamentLobby(private val instance: TournamentInstance) {
    private val waitingPlayers = mutableListOf<Player>()
    private val winners = mutableListOf<Player>()
    private val losers = mutableListOf<Player>()

    private var joinPhase = true
    private var ticksRemaining = 500

    private var bestOfThree: Boolean = false
    private val finalScores = mutableMapOf<Player, Int>()

    init {
        var lastAnnounced = -1
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                ticksRemaining--
                val secondsRemaining = (ticksRemaining * 600) / 1000
                if (secondsRemaining != lastAnnounced) {
                    when (secondsRemaining) {
                        180 -> {
                            Msg.world(Msg.GREEN, icon = 22, "News: Tournament starts in 3 minutes!")
                            DiscordAnnouncer.announce("Tournament", "News: Tournament starts in 3 minutes!")
                        }

                        120 -> {
                            Msg.world(Msg.GREEN, icon = 22, "News: Tournament starts in 2 minutes!")
                            DiscordAnnouncer.announce("Tournament", "News: Tournament starts in 2 minutes!")
                        }

                        60 -> {
                            Msg.world(Msg.GREEN, icon = 22, "News: Tournament starts in 1 minutes!")
                            DiscordAnnouncer.announce("Tournament", "News: Tournament starts in 1 minutes!")
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
                        val allPlayers = waitingPlayers + winners + losers
                        for (p in allPlayers) {
                            p.interfaceManager.closeOverlay(false)
                            p.activeTournament = null
                            p.nextWorldTile = WorldTile(Settings.HOME_PLAYER_LOCATION)
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
        waitingPlayers.add(player)
        player.activeTournament = this.instance
        player.tempInventory = player.inventory.createSnapshot()
        player.tempEquipment = player.equipment.createSnapshot()
        player.inventory.reset()
        player.equipment.reset()
        player.appearence.generateAppearenceData()
        // convert ticks → seconds
        val secondsRemaining = (ticksRemaining * 600) / 1000
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60

        val timeStr = when {
            minutes > 0 && seconds > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} and $seconds second${if (seconds > 1) "s" else ""}"
            minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""}"
            else -> "$seconds second${if (seconds > 1) "s" else ""}"
        }

        player.message("You joined the Tournament lobby. The first match begins in $timeStr!")
        refreshInterface(ticksRemaining)
    }

    fun onLeave(player: Player, restore: Boolean = true) {
        waitingPlayers.remove(player)
        winners.remove(player)
        losers.remove(player)

        if (restore) {
            restoreItems(player)
        }

        player.interfaceManager.closeOverlay(false)
        player.activeTournament = null

        player.nextWorldTile = WorldTile(Settings.HOME_PLAYER_LOCATION)
        player.message("You have left the tournament and been returned home.")
    }


    fun restoreItems(player: Player) {
        player.inventory.restoreSnapshot(player.tempInventory)
        player.equipment.restoreSnapshot(player.tempEquipment)
        player.appearence.generateAppearenceData()
    }


    private fun refreshInterface(nextFight: Int) {
        val allPlayers = waitingPlayers + winners + losers
        for (p in allPlayers) {
            if (p.hasFinished() || !p.hasStarted()) continue
            if (!p.interfaceManager.containsInterface(265))
            p.interfaceManager.sendOverlay(265, false)

            p.packets.sendTextOnComponent(265, 1, "Waiting:")
            p.packets.sendTextOnComponent(265, 2, "Waiting:")
            p.packets.sendTextOnComponent(265, 3, "Waiting:")
            p.packets.sendTextOnComponent(265, 9, "Winners:")
            p.packets.sendGlobalVar(
                261, waitingPlayers.size
            )
            p.packets.sendGlobalVar(
                262,
                winners.size
            )
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
        if (waitingPlayers.size < 2) return
        val (p1, p2) = waitingPlayers.shuffled().take(2)
        if (waitingPlayers.size == 2 && !bestOfThree) {
            bestOfThree = true
            finalScores[p1] = 0
            finalScores[p2] = 0
            Msg.warn(p1,"Final match is best of 3!")
            Msg.warn(p2, "Final match is best of 3!")
        }
        waitingPlayers.removeAll(listOf(p1, p2))

        val game = TournamentGame(p1, p2, this)
        PvPGameManager.registerGame(game)
        game.start()
    }

    fun recordResult(winner: Player, loser: Player) {
        if (bestOfThree) {
            finalScores[winner] = (finalScores[winner] ?: 0) + 1

            val scoreW = finalScores[winner] ?: 0
            val scoreL = finalScores[loser] ?: 0
            winner.message("You won this round! Next fught starting soon")
            winner.message("You have $scoreW wins in the final (best of 3).")
            loser.message("You have lost this round! Next fight starting soon.")
            loser.message("You have $scoreL wins in the final (best of 3).")

            if (scoreW >= 2) {
                finishTournament(winner)
                winner.showResult(winner)
            } else {
                waitingPlayers.addAll(listOf(winner, loser))
                scheduleNextMatch(false)
            }
            return
        }
        winners.add(winner)
        losers.add(loser)
        loser.nextWorldTile = getLobby2Tile()
        loser.message("You have been eliminated from this tournament.")
        loser.showResult(null)

        winner.nextWorldTile = getLobby1Tile()
        winner.message("You won this round! Returning to lobby...")

        if (waitingPlayers.size >= 2) {
            scheduleNextMatch(false)
        } else if (waitingPlayers.isEmpty()) {
            if (winners.size == 1) {
                finishTournament(winners.first())
            } else {
                waitingPlayers.addAll(winners)
                winners.clear()
                scheduleNextMatch(false)
            }
        }
    }

    fun finishTournament(champion: Player) {
        champion.message("Congratulations! You are the Tournament Champion!")

        val allPlayers = waitingPlayers + winners + losers
        CoresManager.getSlowExecutor().execute {
            try {
                for (p in allPlayers) {
                    p.interfaceManager.closeOverlay(false)
                    p.activeTournament = null
                    p.nextWorldTile = WorldTile(Settings.HOME_PLAYER_LOCATION)
                    p.message("The tournament has ended. You’ve been sent back home.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        instance.end(champion)

        waitingPlayers.clear()
        winners.clear()
        losers.clear()
    }

    fun onLogin(player: Player) {
        if (!joinPhase && waitingPlayers.isEmpty() && winners.isEmpty() && losers.isEmpty()) {
            player.activeTournament = null
            player.nextWorldTile = WorldTile(Settings.HOME_PLAYER_LOCATION)
            restoreItems(player)
            return
        }

        if (waitingPlayers.contains(player) || winners.contains(player) || losers.contains(player)) {
            player.activeTournament = null
        } else {
            player.activeTournament = null
            player.nextWorldTile = WorldTile(Settings.HOME_PLAYER_LOCATION)
            restoreItems(player)
        }
    }

    fun onLogout(player: Player) {
        waitingPlayers.remove(player)
        winners.remove(player)
        losers.remove(player)

        player.activeTournament = null
        player.location = Settings.HOME_PLAYER_LOCATION // force-save safe coords
    }



    fun getLobby1Tile() = instance.getLobby1()
    fun getLobby2Tile() = instance.getLobby2()
    fun getFirstSpawn() = instance.getFirstSpawn()
    fun getSecondSpawn() = instance.getSecondSpawn()
}
