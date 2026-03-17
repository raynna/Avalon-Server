package com.rs.kotlin.game.npc.combatdata

import com.rs.kotlin.game.player.combat.magic.ElementType

data class Weaknesses(
    val elemental: Map<String, Int> = emptyMap(),
) {
    fun getMultiplier(element: ElementType): Double {
        val key = element.key()
        val percent = elemental[key] ?: return 1.0
        return 1.0 + (percent / 100.0)
    }

    fun isWeakTo(element: ElementType): Boolean = elemental.containsKey(element.key())
}
