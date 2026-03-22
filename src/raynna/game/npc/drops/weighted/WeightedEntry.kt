package raynna.game.npc.drops.weighted

import raynna.game.npc.drops.Drop
import raynna.game.npc.drops.DropContext

interface WeightedEntry {
    val weight: Int

    fun roll(context: DropContext): Drop?
}
