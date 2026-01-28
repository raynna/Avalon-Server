package com.rs.kotlin.game.world.activity.pvpgame.tournament

import com.rs.core.thread.CoresManager
import com.rs.discord.DiscordAnnouncer
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
        announceWorldAndDiscord("A tournament will start in ${formatTime(safeDelay)}!")

        nextTask = CoresManager.getSlowExecutor().schedule({
            startNewTournament()
        }, safeDelay, TimeUnit.MILLISECONDS)
        scheduleWarning(safeDelay, 15 * 60 * 1000L) // 15 minutes before
        scheduleWarning(safeDelay, 5 * 60 * 1000L)  // 5 minutes before
    }

    fun startNewTournament(): TournamentLobby {
        cancelPending()
        val instance = TournamentInstance()
        instance.create()

        val lobby = instance.getLobby()
        currentInstance = instance
        currentLobby = lobby
        announceWorldAndDiscord(colour = Msg.GREEN, message = "News: A new Tournament has begun! Type ::tournament to join.")
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

    private fun scheduleWarning(totalDelay: Long, beforeMs: Long) {
        val triggerAt = totalDelay - beforeMs
        if (triggerAt <= 0) return // too soon, skip

        CoresManager.getSlowExecutor().schedule({
            announceWorldAndDiscord("A tournament will start in ${formatTime(beforeMs)}!")
        }, triggerAt, TimeUnit.MILLISECONDS)
    }

    private fun announceWorldAndDiscord(message: String, colour: String = Msg.ORANGE) {
        Msg.world(colour, icon = 22, "News: $message")
        DiscordAnnouncer.announce("Tournament", "News: $message")
    }
}
