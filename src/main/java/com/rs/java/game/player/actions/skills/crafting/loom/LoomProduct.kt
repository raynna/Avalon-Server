package com.rs.java.game.player.actions.skills.crafting.loom

import com.rs.java.game.player.actions.skills.crafting.ReqItem
import com.rs.kotlin.rscm.Rscm

class LoomProduct(
    private val idRef: Any,
    val level: Int,
    val xp: Double,
    vararg val requirements: ReqItem,
) {
    fun getId(): Int = idRef as? Int ?: Rscm.lookup(idRef as String)
}
