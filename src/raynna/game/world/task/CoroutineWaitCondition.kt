package raynna.game.world.task

internal interface CoroutineWaitCondition<T> {
    fun tick(): Boolean

    fun value(): T
}
