package raynna.game.world.task

class WorldTaskBuilder private constructor() {
    private val steps = mutableListOf<BuilderStep>()

    private sealed interface BuilderStep

    private data class DelayStep(
        val ticks: Int,
    ) : BuilderStep

    private data class RunStep(
        val action: Runnable,
    ) : BuilderStep

    private data class RepeatStep(
        val times: Int,
        val ticksBetween: Int,
        val action: Runnable,
    ) : BuilderStep

    fun delay(ticks: Int): WorldTaskBuilder {
        steps += DelayStep(ticks)
        return this
    }

    fun run(action: Runnable): WorldTaskBuilder {
        steps += RunStep(action)
        return this
    }

    @JvmOverloads
    fun repeat(
        times: Int,
        ticksBetween: Int = 1,
        action: Runnable,
    ): WorldTaskBuilder {
        steps += RepeatStep(times, ticksBetween, action)
        return this
    }

    fun start(delayTicks: Int = 0) {
        val capturedSteps = steps.toList()
        worldTaskCoroutine(delayTicks) {
            for (step in capturedSteps) {
                when (step) {
                    is DelayStep -> {
                        delay(step.ticks)
                    }

                    is RunStep -> {
                        step.action.run()
                    }

                    is RepeatStep -> {
                        repeat(step.times) { i ->
                            step.action.run()
                            if (i < step.times - 1) delay(step.ticksBetween)
                        }
                    }
                }
            }
            stop()
        }
    }

    companion object {
        @JvmStatic
        fun create(): WorldTaskBuilder = WorldTaskBuilder()
    }
}
