package com.rs.kotlin.game.world.activity.pvpgame.lms

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.player.Player
import com.rs.kotlin.game.world.activity.pvpgame.PvPGameManager

class LastManStandingLobby {
    private val waitingPlayers = mutableListOf<Player>()
    private var countdown = 30
    private var running = false

    fun addPlayer(player: Player) {
        waitingPlayers.add(player)
        player.message("You joined Last Man Standing. Game starts in $countdown seconds.")
        if (!running) startCountdown()
    }

    private fun startCountdown() {
        running = true
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                if (waitingPlayers.size < 2) {
                    countdown = 30
                    running = false
                    stop()
                    return
                }
                if (--countdown <= 0) {
                    startGame()
                    stop()
                }
            }
        }, 0, 1)
    }

    private fun startGame() {
        val game = LastManStandingGame(waitingPlayers.toList())
        PvPGameManager.registerGame(game)
        game.start()
        waitingPlayers.clear()
        running = false
        countdown = 30
    }
}