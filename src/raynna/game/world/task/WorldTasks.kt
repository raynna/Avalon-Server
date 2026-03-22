package raynna.game.world.task

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.function.Consumer

object WorldTasks {
    @JvmStatic
    fun submit(action: Runnable) =
        WorldTasksHandler.scope.launch {
            WorldTasksHandler.schedule(startTick = 0, repeatTicks = -1) { action.run() }
        }

    @JvmStatic
    fun submit(
        delayTicks: Int,
        action: Runnable,
    ) = WorldTasksHandler.scope.launch {
        WorldTasksHandler.schedule(startTick = delayTicks, repeatTicks = -1) { action.run() }
    }

    @JvmStatic
    fun repeat(
        intervalTicks: Int,
        action: StopHandle.() -> Unit,
    ) = repeat(0, intervalTicks, action)

    @JvmStatic
    fun repeat(
        startTick: Int,
        intervalTicks: Int,
        action: StopHandle.() -> Unit,
    ) = WorldTasksHandler.scope.launch {
        WorldTasksHandler.schedule(startTick = startTick, repeatTicks = (intervalTicks - 1).coerceAtLeast(0)) {
            StopHandle(this).action()
        }
    }

    @JvmStatic
    fun coroutine(
        delayTicks: Int = 0,
        block: Consumer<WorldTaskDsl>,
    ) {
        worldTaskCoroutine(delayTicks) { block.accept(this) }
    }

    @JvmStatic
    fun scheduleBlocking(
        delayTicks: Int = 0,
        repeatTicks: Int = -1,
        action: Runnable,
    ) = runBlocking {
        WorldTasksHandler.schedule(startTick = delayTicks, repeatTicks = repeatTicks) { action.run() }
    }

    @JvmStatic
    fun taskCount(): Int = runBlocking { WorldTasksHandler.taskCount() }
}

class StopHandle internal constructor(
    private val scope: WorldTaskScope,
) {
    fun stop() = scope.stop()
}
