package com.rs.java.game.player.actions.skills.crafting.spinningwheel

import com.rs.java.game.player.actions.skills.crafting.ReqItem
import com.rs.kotlin.rscm.Rscm

class SpinningProduct(
    private val idRef: Any,
    val level: Int,
    val xp: Double,
    vararg val requirements: ReqItem,
) {
    fun getId(): Int = idRef as? Int ?: Rscm.lookup(idRef as String)
}
