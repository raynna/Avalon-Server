package raynna.game.player.actions.skills.crafting.gem

import raynna.game.item.Item
import raynna.data.rscm.RscmResolver

enum class GemData(
    val product: GemProduct,
) {
    OPAL(GemProduct("item.uncut_opal", "item.opal", 15.0, 1, 886)),
    JADE(GemProduct("item.uncut_jade", "item.jade", 20.0, 13, 886)),
    RED_TOPAZ(GemProduct("item.uncut_red_topaz", "item.red_topaz", 25.0, 16, 887)),
    SAPPHIRE(GemProduct("item.uncut_sapphire", "item.sapphire", 50.0, 20, 888)),
    EMERALD(GemProduct("item.uncut_emerald", "item.emerald", 67.0, 27, 889)),
    RUBY(GemProduct("item.uncut_ruby", "item.ruby", 85.0, 34, 887)),
    DIAMOND(GemProduct("item.uncut_diamond", "item.diamond", 107.5, 43, 890)),
    DRAGONSTONE(GemProduct("item.uncut_dragonstone", "item.dragonstone", 137.5, 55, 885)),
    ONYX(GemProduct("item.uncut_onyx", "item.onyx", 167.5, 67, 2717)),
    ;

    companion object {
        private val lookup =
            RscmResolver.buildIdMap(entries) { listOf(it.product.uncut) }

        fun forUncut(id: Int): GemData? = lookup[id]

        fun getGem(
            a: Item,
            b: Item,
        ): GemData? = forUncut(a.id) ?: forUncut(b.id)
    }
}
