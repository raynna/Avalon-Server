@file:Suppress("ktlint:standard:filename")

package raynna.game.world.task

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun worldTaskCoroutine(
    delayTicks: Int = 0,
    block: suspend WorldTaskDsl.() -> Unit,
) {
    var driver: WorldTaskCoroutineDriver? = null

    WorldTasksHandler.scope.launch {
        WorldTasksHandler.schedule(startTick = delayTicks, repeatTicks = 0) {
            val currentDriver = driver

            if (currentDriver == null) {
                val newDriver = WorldTaskCoroutineDriver()
                driver = newDriver
                val dsl = WorldTaskDsl(this)
                try {
                    newDriver.launch {
                        withContext(newDriver) { dsl.block() }
                    }
                } catch (e: Exception) {
                    newDriver.cleanup()
                    stop()
                    e.printStackTrace()
                    return@schedule
                }
            } else {
                currentDriver.tick()
            }

            if (currentDriver?.isIdle == true) {
                println("task has stopped.")
                currentDriver.cleanup()
                stop()
            }
        }
    }
}
