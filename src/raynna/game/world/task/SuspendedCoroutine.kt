package raynna.game.world.task

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

internal class SuspendedCoroutine<T : Any>(
    private val continuation: Continuation<T>,
    private val condition: CoroutineWaitCondition<T>,
) {
    fun tryResume(): Boolean {
        if (!condition.tick()) return false
        continuation.resume(condition.value())
        return true
    }
}
