package raynna.game.npc.drops.weighted

import raynna.game.npc.drops.Drop
import raynna.game.npc.drops.DropContext

class WeightedTableEntry(
    val weightedTable: WeightedTable,
    override val weight: Int,
) : WeightedEntry {
    override fun roll(context: DropContext): Drop? = weightedTable.roll(context)
}
