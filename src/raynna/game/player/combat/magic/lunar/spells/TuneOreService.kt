package raynna.game.player.combat.magic.lunar.spells

import raynna.game.player.Player

object TuneOreService {
    enum class Ore(
        val baseOreId: Int,
        val newOreId: Int,
        val requirements: IntArray,
    ) {
        DRAGONBANE_ORE(
            21778,
            21779,
            intArrayOf(536, 534, 243, 1753, 1751, 1749, 1747),
        ),

        WALLASALKIBANE_ORE(
            21778,
            21780,
            intArrayOf(6163, 6165, 6167),
        ),

        BASILISKBANE_ORE(
            21778,
            21781,
            intArrayOf(7977),
        ),

        ABYSSALBANE_ORE(
            21778,
            21782,
            intArrayOf(7979),
        ),
        ;

        companion object {
            fun forItem(itemId: Int): Ore? =
                Ore.entries.firstOrNull { ore ->
                    ore.requirements.contains(itemId)
                }
        }
    }

    fun cast(
        player: Player,
        itemId: Int,
    ): Boolean {
        val ore = Ore.forItem(itemId)
        if (ore == null) {
            player.message("You can't use this spell on that item.")
            return false
        }

        val amount = player.inventory.getNumberOf(ore.baseOreId)
        if (amount <= 0) {
            player.message("You need bane ore to tune.")
            return false
        }

        player.inventory.deleteItem(ore.baseOreId, amount)
        player.inventory.addItem(ore.newOreId, amount)
        return true
    }
}
