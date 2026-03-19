package com.rs.kotlin.game.world.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

object WorldTasksHandler {
    private val log = LoggerFactory.getLogger(WorldTasksHandler::class.java)
    private const val TASK_COUNT_WARNING_THRESHOLD = 100
    private const val SLOW_TASK_THRESHOLD_MS = 200L

    private val mutex = Mutex()
    private val pending = ArrayDeque<TaskEntry>()

    internal val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun schedule(task: WorldTask) =
        mutex.withLock {
            pending += TaskEntry(task)
        }

    suspend fun schedule(
        startTick: Int = 0,
        repeatTicks: Int = -1,
        action: suspend WorldTaskScope.() -> Unit,
    ) = schedule(WorldTask(action, startTick, repeatTicks))

    suspend fun taskCount(): Int = mutex.withLock { pending.size }

    suspend fun processTick() {
        val snapshot =
            mutex.withLock {
                if (pending.size > TASK_COUNT_WARNING_THRESHOLD) {
                    log.warn("High task count: {} tasks pending", pending.size)
                }
                pending.toList()
            }

        val toRemove = mutableListOf<TaskEntry>()

        for (entry in snapshot) {
            if (entry.ticksRemaining > 0) {
                entry.ticksRemaining--
                continue
            }

            val taskScope = WorldTaskScope()
            val elapsed: Duration =
                measureTime {
                    runCatching { entry.task.action(taskScope) }
                        .onFailure { err ->
                            log.error("Task crashed [${entry.task}]: ${err.message}", err)
                            toRemove += entry
                        }
                }

            if (elapsed > SLOW_TASK_THRESHOLD_MS.milliseconds) {
                log.warn("Slow task detected [${entry.task}]: took ${elapsed.inWholeMilliseconds} ms")
            }

            when {
                taskScope.stopRequested -> toRemove += entry
                entry.task.repeatTicks == -1 -> toRemove += entry
                else -> entry.ticksRemaining = entry.task.repeatTicks
            }
        }

        if (toRemove.isNotEmpty()) {
            mutex.withLock { pending.removeAll(toRemove.toSet()) }
        }
    }

    private class TaskEntry(
        val task: WorldTask,
    ) {
        var ticksRemaining: Int = task.startTick
    }
}
