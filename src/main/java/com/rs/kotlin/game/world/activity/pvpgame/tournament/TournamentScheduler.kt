package com.rs.kotlin.game.world.activity.pvpgame.tournament

import TournamentLobby
import com.rs.core.thread.CoresManager
import com.rs.java.game.World
import com.rs.java.utils.Utils
import com.rs.kotlin.game.world.util.Msg
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.max

object TournamentScheduler {

    private const val MIN_DELAY_MS = 25 * 60 * 1000L
    private const val MAX_DELAY_MS = 35 * 60 * 1000L

    @Volatile private var nextTask: ScheduledFuture<*>? = null
    @Volatile private var currentInstance: TournamentInstance? = null
    @Volatile private var currentLobby: TournamentLobby? = null

    fun start() {
        if (nextTask == null && currentInstance == null) {
            scheduleNext(randomDelay())
        }
    }

    private fun randomDelay(): Long {
        val span = MAX_DELAY_MS - MIN_DELAY_MS
        return MIN_DELAY_MS + Utils.random(span.toInt())
    }

    private fun scheduleNext(delayMs: Long) {
        cancelPending()
        val safeDelay = max(1000L, delayMs)
        Msg.world(Msg.ORANGE, icon = 15,"News: A tournament will start in ${formatTime(safeDelay)}!")

        nextTask = CoresManager.getSlowExecutor().schedule({
            startNewTournament()
        }, safeDelay, TimeUnit.MILLISECONDS)
    }

    fun startNewTournament(): TournamentLobby {
        val instance = TournamentInstance()
        instance.create()

        val lobby = instance.getLobby()
        currentInstance = instance
        currentLobby = lobby
        Msg.world(Msg.GREEN, icon = 13,"News: A new Tournament has begun! Type ::tournament to join.")

        return lobby
    }

    fun endTournament() {
        currentLobby = null
        currentInstance = null
        scheduleNext(randomDelay())
    }

    fun getLobby(): TournamentLobby? = currentLobby
    fun getInstance(): TournamentInstance? = currentInstance

    private fun cancelPending() {
        nextTask?.cancel(false)
        nextTask = null
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return buildString {
            if (minutes > 0) append("$minutes minute${if (minutes > 1) "s" else ""}")
            if (minutes > 0 && seconds > 0) append(" and ")
            if (seconds > 0) append("$seconds second${if (seconds > 1) "s" else ""}")
        }
    }
}
