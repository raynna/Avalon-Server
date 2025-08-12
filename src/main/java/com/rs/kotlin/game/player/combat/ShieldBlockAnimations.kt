package com.rs.kotlin.game.player.combat

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.kotlin.Rscm

object ShieldBlockAnimations {
    const val DEFAULT_BLOCK_ANIM = 424
    private const val SHIELD_BLOCK_ANIMATION = 1156
    private const val DEFENDER_BLOCK_ANIMATION = 4177

    private val shieldAnimationMap: Map<Int, Int>

    init {
        val groups = listOf(
            listOf(Rscm.lookup("item.toktz_ket_xil")) to SHIELD_BLOCK_ANIMATION,
        )
        shieldAnimationMap = groups.flatMap { (ids, anim) ->
            ids.map { id -> id to anim }
        }.toMap()
    }

    fun getBlockAnimationFor(shieldId: Int): Int? {
        shieldAnimationMap[shieldId]?.let { return it }

        val itemName = ItemDefinitions.getItemDefinitions(shieldId).getName() ?: return null
        if (itemName.contains("defender", ignoreCase = true)) {
            return DEFENDER_BLOCK_ANIMATION
        }
        if (itemName.contains("shield", ignoreCase = true)) {
            return SHIELD_BLOCK_ANIMATION
        }
        return null
    }
}