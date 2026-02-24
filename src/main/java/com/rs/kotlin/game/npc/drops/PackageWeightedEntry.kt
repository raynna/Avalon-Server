package com.rs.kotlin.game.npc.drops

class PackageWeightedEntry(
    override val weight: Int,
    val displayDrops: List<PackageDisplayDrop> = emptyList(),
    private val condition: ((DropContext) -> Boolean)? = null,
    private val build: (DropContext) -> List<Drop>,
) : WeightedEntry {
    override fun roll(context: DropContext): Drop? {
        if (weight <= 0) return null
        if (condition?.invoke(context) == false) return null

        val drops = build(context)
        if (drops.isEmpty()) return null

        val head = drops.first()
        var cur = head

        for (i in 1 until drops.size) {
            cur.extraDrop = drops[i]
            cur = drops[i]
        }

        return head
    }
}
