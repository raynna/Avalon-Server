package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.player.Player

object StringJewelleryService {
    enum class Amulet(
        val baseId: Int,
        val newId: Int,
    ) {
        GOLD_AMULET(1673, 1692),
        SAPPHIRE_AMULET(1675, 1694),
        EMERALD_AMULET(1677, 1696),
        RUBY_AMULET(1679, 1698),
        DIAMOND_AMULET(1681, 1700),
        DRAGONSTONE_AMULET(1683, 1702),
        ONYX_AMULET(6579, 6581),
        LIVID_PLANT(20704, 20705),
    }

    fun cast(player: Player): Boolean {
        val inventory = player.inventory

        val amulet =
            Amulet.entries.firstOrNull {
                inventory.containsItem(it.baseId, 1)
            }

        if (amulet == null) {
            player.message("You don't have any amulets to string.")
            return false
        }

        player.lock(2)
        player.animate(Animation(4412))
        player.gfx(Graphics(742, 0, 96 shl 16))

        if (amulet == Amulet.LIVID_PLANT) {
            val amount = inventory.getNumberOf(20704)

            if (amount >= 10) {
                inventory.deleteItem(20704, 10)
                inventory.addItem(20705, 2)
            } else {
                inventory.deleteItem(20704, 5)
                inventory.addItem(20705, 1)
            }
        } else {
            inventory.deleteItem(amulet.baseId, 1)
            inventory.addItem(amulet.newId, 1)
        }

        player.message(
            "Your spell strings the ${
                ItemDefinitions.getItemDefinitions(amulet.baseId).name
            }.",
        )

        return true
    }
}
