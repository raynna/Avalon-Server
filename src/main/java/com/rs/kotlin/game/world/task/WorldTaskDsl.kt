package com.rs.kotlin.game.world.task

import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

class WorldTaskDsl internal constructor(
    private val taskScope: WorldTaskScope,
) {
    suspend fun delay(ticks: Int) {
        require(ticks >= 0) { "delay ticks must be non-negative, got $ticks" }
        if (ticks == 0) return
        suspendCoroutineUninterceptedOrReturn { cont ->
            cont.context.driver.suspendFor(ticks, cont)
            COROUTINE_SUSPENDED
        }
    }

    suspend fun stop(): Nothing {
        taskScope.stop()
        currentCoroutineContext().driver.cancel()
    }

    private companion object {
        val CoroutineContext.driver: WorldTaskCoroutineDriver
            get() =
                get(WorldTaskCoroutineDriver)
                    ?: error(
                        "WorldTaskCoroutineDriver missing from context — " +
                            "only call delay/stop inside worldTaskCoroutine { }",
                    )
    }
}
