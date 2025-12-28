package com.rs.kotlin.game.world.activity.pvpgame.tournament

import TournamentLobby
import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.core.thread.CoresManager
import com.rs.java.game.ForceTalk
import com.rs.java.game.World
import com.rs.kotlin.game.player.AccountCreation
import com.rs.java.game.player.Player
import com.rs.java.game.player.actions.combat.Magic
import com.rs.java.utils.Logger
import com.rs.java.utils.Utils
import com.rs.kotlin.game.world.activity.pvpgame.PvPGame
import com.rs.kotlin.game.world.activity.pvpgame.activePvPGame
import com.rs.kotlin.game.world.activity.pvpgame.openPvPOverlay
import com.rs.kotlin.game.world.activity.pvpgame.showResult
import java.util.*
import java.util.concurrent.TimeUnit

class TournamentGame(
    private val p1: Player,
    private val p2: Player,
    private val lobby: TournamentLobby
) : PvPGame() {

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
            setupLoadout(it)
            it.setCanPvp(false)
            it.packets.sendGlobalVar(270, countdownTicks)
            it.packets.sendGlobalVar(260, 0)
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
                    // One player missing -> auto win for the other
                    val winner = stillHere.firstOrNull()
                    val loser = if (winner == p1) p2 else p1

                    p1.setCanPvp(false)
                    p2.setCanPvp(false)

                    if (winner != null && loser != null) {
                        lobby.recordResult(winner, loser)
                    } else {
                        cleanup(null)
                    }
                    stop()
                }
            }
        }, 0, 1) // check every tick

    }



    private fun setupLoadout(player: Player) {
        player.inventory.reset()
        player.equipment.reset()
        // load some preset (this could be dynamic later)
        val template = World.getPlayerByDisplayName("halfeco")
            ?: AccountCreation.loadPlayer("halfeco")
        if (template != null) {
            player.presetManager.loadPreset("hybrid", template)
        }
        player.appearence.generateAppearenceData()
    }

    override fun onPlayerDeath(player: Player) {
        players.remove(player)
        val winner = players.firstOrNull()
        if (winner != null) {
            p1.setCanPvp(false)
            p2.setCanPvp(false)
            lobby.recordResult(winner, player)
        } else {
            cleanup(null)
        }
    }
}
