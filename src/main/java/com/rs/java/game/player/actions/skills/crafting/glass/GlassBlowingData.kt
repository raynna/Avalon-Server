package com.rs.java.game.player.actions.skills.crafting.glass

import com.rs.kotlin.rscm.RscmResolver

enum class GlassBlowingData(
    val baseRef: Any,
    val products: Array<GlassProduct>,
) {
    MOLTEN_GLASS(
        "item.molten_glass",
        arrayOf(
            GlassProduct("item.beer_glass", 1, 17.5),
            GlassProduct("item.candle_lantern", 4, 19.0),
            GlassProduct("item.oil_lamp", 12, 25.0),
            GlassProduct("item.vial", 33, 35.0),
            GlassProduct("item.fishbowl", 42, 42.5),
            GlassProduct("item.unpowered_orb", 46, 52.5),
            GlassProduct("item.lantern_lens", 49, 55.0),
            GlassProduct("item.empty_light_orb", 87, 70.0),
        ),
    ),
    ;

    companion object {
        private const val GLASSBLOWING_PIPE = 1785

        private val baseLookup =
            RscmResolver.buildObjectIdMap(entries) { listOf(it.baseRef) }

        private val productLookup =
            RscmResolver.buildObjectIdMap(
                entries.flatMap { it.products.toList() },
            ) { listOf(it.idRef) }

        fun resolveBase(id: Int): GlassBlowingData? = baseLookup[id]

        fun resolveProduct(id: Int): GlassProduct? = productLookup[id]

        fun getGlassData(
            id1: Int,
            id2: Int,
        ): GlassBlowingData? {
            val data = resolveBase(id1) ?: resolveBase(id2) ?: return null

            val hasPipe = id1 == GLASSBLOWING_PIPE || id2 == GLASSBLOWING_PIPE

            return if (hasPipe) data else null
        }
    }
}
