package raynna.game.world.task

data class WorldTask(
    val action: suspend WorldTaskScope.() -> Unit,
    val startTick: Int = 0,
    val repeatTicks: Int = -1,
)
