package com.rs.kotlin.game.npc.drops

class FullDropTableEntry(
    val dropTable: DropTable,
    override val weight: Int,
) : WeightedEntry {
    override fun roll(context: DropContext): Drop? {
        val drops =
            dropTable.rollDrops(
                player = context.player,
                combatLevel = 0, // or pass properly if needed
            )

        return drops.firstOrNull()
    }
}
