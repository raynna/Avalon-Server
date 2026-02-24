package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player

class PackageWeightedEntry(
    override val weight: Int,
    val displayDrops: List<PackageDisplayDrop> = emptyList(), // <-- for UI/export/scan
    private val condition: ((Player) -> Boolean)? = null,
    private val build: (Player, DropSource) -> List<Drop>,
) : WeightedEntry {
    override fun roll(
        player: Player,
        source: DropSource,
    ): Drop? {
        if (weight <= 0) return null
        if (condition?.invoke(player) == false) return null

        val list = build(player, source)
        if (list.isEmpty()) return null

        // Chain them: head -> extraDrop -> extraDrop ...
        val head = list.first()
        var cur = head
        for (i in 1 until list.size) {
            cur.extraDrop = list[i]
            cur = list[i]
        }
        return head
    }
}
