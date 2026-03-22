package raynna.game.world.task

internal class DelayCondition(
    private var ticks: Int,
) : CoroutineWaitCondition<Unit> {
    override fun tick() = --ticks == 0

    override fun value() = Unit
}
