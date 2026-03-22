package raynna.game.player.combat.magic.lunar.spells

import raynna.game.Animation
import raynna.game.Graphics
import raynna.game.player.Player
import raynna.util.Utils

object HumidifyService {
    private const val COOLDOWN = 4800L
    private const val ATTR_KEY = "LAST_HUMIDIFY"

    private val emptyItems =
        intArrayOf(
            229,
            1831,
            1829,
            1827,
            1825,
            1925,
            1923,
            1935,
            5331,
            5332,
            5333,
            5334,
            5335,
            5337,
            5338,
            5339,
            6667,
            7688,
            731,
            1980,
            434,
        )

    private val filledItems =
        intArrayOf(
            227,
            1823,
            1823,
            1823,
            1823,
            1929,
            1921,
            1937,
            5340,
            5340,
            5340,
            5340,
            5340,
            5340,
            5340,
            5340,
            6668,
            7690,
            732,
            4458,
            1761,
        )

    fun cast(player: Player): Boolean {
        val attrs = player.temporaryAttribute()
        val lastCast = attrs[ATTR_KEY] as? Long

        if (lastCast != null && lastCast + COOLDOWN > Utils.currentTimeMillis()) {
            return false
        }

        var converted = false

        for (i in emptyItems.indices) {
            val empty = emptyItems[i]
            val filled = filledItems[i]

            val amount = player.inventory.getAmountOf(empty)

            if (amount > 0) {
                player.inventory.deleteItem(empty, amount)
                player.inventory.addItem(filled, amount)
                converted = true
            }
        }

        if (!converted) {
            player.message("You do not have any empty vessels to fill.")
            return false
        }

        player.animate(Animation(6294))
        player.gfx(Graphics(1061))

        attrs[ATTR_KEY] = Utils.currentTimeMillis()

        return true
    }
}
