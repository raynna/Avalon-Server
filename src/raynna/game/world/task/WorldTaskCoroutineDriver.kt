package raynna.game.world.task

import java.util.concurrent.CancellationException
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine

internal class WorldTaskCoroutineDriver : AbstractCoroutineContextElement(WorldTaskCoroutineDriver) {
    companion object Key : CoroutineContext.Key<WorldTaskCoroutineDriver>

    private var suspended: SuspendedCoroutine<out Any>? = null

    val isIdle: Boolean get() = suspended == null

    fun suspendFor(
        ticks: Int,
        continuation: Continuation<Unit>,
    ) {
        suspended = SuspendedCoroutine(continuation, DelayCondition(ticks))
    }

    fun tick() {
        val current = suspended ?: return
        if (current.tryResume() && suspended === current) {
            suspended = null
        }
    }

    fun cancel(): Nothing {
        suspended = null
        throw CancellationException("WorldTask coroutine cancelled")
    }

    fun cleanup() {
        if (isIdle) return
        try {
            cancel()
        } catch (_: CancellationException) {
        }
        suspended = null
    }

    fun launch(block: suspend () -> Unit) {
        block.startCoroutine(
            object : Continuation<Unit> {
                override val context: CoroutineContext = this@WorldTaskCoroutineDriver

                override fun resumeWith(result: Result<Unit>) {
                    result
                        .exceptionOrNull()
                        ?.takeIf { it !is CancellationException }
                        ?.printStackTrace()
                }
            },
        )
    }
}
