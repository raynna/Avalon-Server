package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.player.Player
import com.rs.java.utils.Utils

object SuperGlassMakeService {
    private const val BUCKET_OF_SAND = 1783
    private const val SEAWEED = 401
    private const val MOLTEN_GLASS = 1775

    private const val COOLDOWN = 1800L
    private const val ATTR_KEY = "LAST_SPELL"

    fun cast(player: Player): Boolean {
        val attrs = player.temporaryAttribute()

        val lastCast = attrs[ATTR_KEY] as? Long
        if (lastCast != null && lastCast + COOLDOWN > Utils.currentTimeMillis()) {
            return false
        }

        val inventory = player.inventory

        val sand = inventory.getNumberOf(BUCKET_OF_SAND)
        val seaweed = inventory.getNumberOf(SEAWEED)

        if (sand == 0 || seaweed == 0) {
            player.message(
                if (sand > 0) {
                    "You don't have any seaweed."
                } else {
                    "You don't have any bucket of sand."
                },
            )
            return false
        }

        val amount = minOf(sand, seaweed)

        inventory.deleteItem(SEAWEED, amount)
        inventory.deleteItem(BUCKET_OF_SAND, amount)

        inventory.addItem(MOLTEN_GLASS, amount)

        player.message("You create $amount molten glass with your spell.")

        attrs[ATTR_KEY] = Utils.currentTimeMillis()

        return true
    }
}
