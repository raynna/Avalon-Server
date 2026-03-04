package com.rs.java.game.player.actions.skills.crafting

import com.rs.kotlin.rscm.Rscm

class ReqItem(
    private val idRef: Any,
    val amount: Int,
) {
    fun getId(): Int = idRef as? Int ?: Rscm.lookup(idRef as String)

    companion object {
        @JvmStatic
        fun item(
            id: Any,
            amount: Int,
        ): ReqItem = ReqItem(id, amount)

        @JvmStatic
        fun requiredItems(vararg items: ReqItem): Array<ReqItem> = arrayOf(*items)
    }
}
