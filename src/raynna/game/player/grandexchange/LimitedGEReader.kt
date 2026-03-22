package raynna.game.player.grandexchange

import raynna.data.rscm.Rscm

/**
 * Registry of GE-limited items.
 *
 * Limited items always go through the order book — they never get the
 * instant-sell/buy path regardless of price or value.
 *
 * Add items here using RSCM references for autocomplete, or raw IDs if
 * the item has no RSCM entry yet.
 */
object LimitedGEReader {
    private val items: Set<Int> by lazy { buildSet() }

    private fun buildSet(): HashSet<Int> =
        hashSetOf(
            Rscm.item("item.overload_4"),
            Rscm.item("item.super_prayer_4"),
            Rscm.item("item.super_combat_potion_4"),
            Rscm.item("item.prayer_renewal_4"),
            Rscm.item("item.super_antifire_4"),
            Rscm.item("item.rocktail"),
            Rscm.item("item.purple_sweets"),
            Rscm.item("item.dragon_arrow"),
            Rscm.item("item.dragon_dart"),
            Rscm.item("item.dragonstone_bolts_e"),
            Rscm.item("item.morrigan_s_throwing_axe"),
            Rscm.item("item.morrigan_s_javelin"),
            Rscm.item("item.royal_d_hide_body"),
            Rscm.item("item.royal_d_hide_chaps"),
            Rscm.item("item.potion_flask"),
            Rscm.item("item.armadyl_rune"),
        )

    fun init() {
        println("[GE/Limited] Loaded ${items.size} limited item(s).")
    }

    fun reload() {
        println("[GE/Limited] Limited items are defined in code — restart to apply changes.")
    }

    fun itemIsLimited(itemId: Int): Boolean = itemId in items

    fun getLimitedItems(): Set<Int> = items
}
