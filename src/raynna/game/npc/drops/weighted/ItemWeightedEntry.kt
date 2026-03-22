package raynna.game.npc.drops.weighted

import raynna.game.player.Player
import raynna.game.npc.drops.Drop
import raynna.game.npc.drops.DropContext
import raynna.game.npc.drops.DropMetadata
import raynna.game.npc.drops.weighted.WeightedEntry

class ItemWeightedEntry(
    val itemId: Int,
    val amount: IntRange,
    override val weight: Int = 1,
    private val condition: ((DropContext) -> Boolean)? = null,
    private val customLogic: ((Player, Drop?) -> Unit)? = null,
    val metadata: DropMetadata = DropMetadata(),
) : WeightedEntry {
    override fun roll(context: DropContext): Drop? {
        if (condition != null && !condition.invoke(context)) {
            return null
        }

        val drop =
            Drop(
                itemId = itemId,
                amount = amount.random(),
                context = context,
                metadata = metadata,
            )

        customLogic?.invoke(context.player, drop)

        return drop
    }
}
