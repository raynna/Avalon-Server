package com.rs.kotlin.game.world.activity.pvpgame.tournament

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.ForceTalk
import com.rs.java.game.player.Player
import com.rs.java.game.player.actions.combat.Magic
import com.rs.kotlin.game.player.command.CommandRegistry.execute
import com.rs.kotlin.game.world.activity.pvpgame.PvPGame
import com.rs.kotlin.game.world.activity.pvpgame.PvPGameManager
import com.rs.kotlin.game.world.activity.pvpgame.activePvPGame
import com.rs.kotlin.game.world.activity.pvpgame.openPvPOverlay

class TournamentGame(
    private val p1: Player,
    private val p2: Player,
    val lobby: TournamentLobby
) : PvPGame() {

    override fun shouldDestroyArea(): Boolean = false

    override fun start() {
        players.addAll(listOf(p1, p2))
        val countdownTicks = 33
        val secondsRemaining = (countdownTicks * 600) / 1000
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        Magic.teleport(p1, lobby.getFirstSpawn())
        Magic.teleport(p2, lobby.getSecondSpawn())
        p1.openPvPOverlay(p1, p2)
        p2.openPvPOverlay(p2, p1)
        players.forEach {
            it.activePvPGame = this
            it.setCanPvp(false)
            it.packets.sendGlobalVar(270, countdownTicks)
            it.packets.sendGlobalVar(260, 0)
            it.message("Loadout: ${lobby.getTournamentPreset().name}")
            it.message("Tournament match will begin in $secondsRemaining seconds. Get ready!")
        }

        WorldTasksManager.schedule(object : WorldTask() {
            var ticksLeft = countdownTicks
            override fun run() {
                ticksLeft--

                if (ticksLeft in 1..3) {
                    players.forEach { it.nextForceTalk = ForceTalk("$ticksLeft!") }
                }

                if (ticksLeft <= 0) {
                    players.forEach { p ->
                        p.setCanPvp(true)
                        p.packets.sendPlayerOption("Attack", 1, false)
                        p.packets.sendGlobalVar(260, 1)
                        p.nextForceTalk = ForceTalk("Fight!")
                    }
                    stop()
                }
            }
        }, 0, 0)
        //Safety: check if players are still present every tick
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {

                val stillHere = players.filter { it.isActive && !it.hasFinished() }

                if (stillHere.size < 2) {

                    println("[TOURNAMENT] SAFETY TASK FIRED. stillHere=${stillHere.map { it.username }} players=${players.map { it.username }}")


                    val winner = stillHere.firstOrNull()
                    val loser = if (winner == p1) p2 else p1

                    players.clear()

                    p1.setCanPvp(false)
                    p2.setCanPvp(false)

                    println("[TOURNAMENT] Safety unregistering PvPGame")
                    PvPGameManager.unregisterGame(this@TournamentGame)

                    if (winner != null) {
                        println("[TOURNAMENT] Safety winner=${winner.username} loser=${loser.username}")
                        lobby.recordResult(winner, loser)
                    }
                    stop()
                }
            }
        }, 0, 1)


    }

    override fun onPlayerDeath(player: Player) {

        println("[TOURNAMENT] onPlayerDeath fired. Dead=${player.username}")

        players.remove(player)

        val winner = players.firstOrNull()

        println("[TOURNAMENT] Remaining players after death: ${players.map { it.username }}")

        players.clear()

        p1.setCanPvp(false)
        p2.setCanPvp(false)

        println("[TOURNAMENT] Unregistering PvPGame")
        PvPGameManager.unregisterGame(this)

        if (winner != null) {
            println("[TOURNAMENT] Winner by death = ${winner.username}")
            lobby.recordResult(winner, player)
        } else {
            println("[TOURNAMENT] No winner found, cleanup")
            cleanup(null)
        }
    }


}
