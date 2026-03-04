package com.rs.java.game.player.actions.skills.crafting.gem

import com.rs.kotlin.rscm.Rscm

class GemProduct(
    val uncut: Any,
    private val cut: Any,
    val xp: Double,
    val level: Int,
    val animation: Int,
) {
    fun getUncut(): Int = uncut as? Int ?: Rscm.lookup(uncut as String)

    fun getCut(): Int = cut as? Int ?: Rscm.lookup(cut as String)
}
