package raynna.game.player.grandexchange

import raynna.data.rscm.Rscm

/**
 * Registry of GE-unlimited items.
 *
 * Unlimited items bypass the order book entirely — sellers receive 95% of
 * guide price instantly from the server, buyers receive items at guide price.
 *
 * Add items here using RSCM references for autocomplete, or raw IDs if
 * the item has no RSCM entry yet.
 */
object UnlimitedGEReader {
    private val items: Set<Int> by lazy { buildSet() }

    private fun buildSet(): HashSet<Int> =
        hashSetOf(
            Rscm.item("item.air_rune"),
            Rscm.item("item.water_rune"),
            Rscm.item("item.earth_rune"),
            Rscm.item("item.fire_rune"),
            Rscm.item("item.mind_rune"),
            Rscm.item("item.body_rune"),
            Rscm.item("item.chaos_rune"),
            Rscm.item("item.death_rune"),
            Rscm.item("item.blood_rune"),
            Rscm.item("item.soul_rune"),
            Rscm.item("item.nature_rune"),
            Rscm.item("item.law_rune"),
            Rscm.item("item.cosmic_rune"),
            Rscm.item("item.astral_rune"),
            Rscm.item("item.bronze_arrow"),
            Rscm.item("item.iron_arrow"),
            Rscm.item("item.steel_arrow"),
            Rscm.item("item.mithril_arrow"),
            Rscm.item("item.adamant_arrow"),
            Rscm.item("item.rune_arrow"),
        )

    fun init() {
        println("[GE/Unlimited] Loaded ${items.size} unlimited item(s).")
    }

    fun reload() {
        println("[GE/Unlimited] Unlimited items are defined in code — restart to apply changes.")
    }

    fun itemIsUnlimited(itemId: Int): Boolean = itemId in items

    fun getUnlimitedItems(): Set<Int> = items
}
