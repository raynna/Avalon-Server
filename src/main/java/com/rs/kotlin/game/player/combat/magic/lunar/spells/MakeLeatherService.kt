package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player

object MakeLeatherService {
    private data class Leather(
        val hide: Int,
        val leather: Int,
    )

    private val LEATHERS =
        listOf(
            Leather(1739, 1741), // Cowhide -> Leather
            Leather(1739, 1743), // Cowhide -> Hard leather (RS3 option but optional)
            Leather(1753, 1745), // Green dragonhide
            Leather(1751, 2505), // Blue dragonhide
            Leather(1749, 2507), // Red dragonhide
            Leather(1747, 2509), // Black dragonhide
            Leather(6287, 6289), // Snake hide
            Leather(7801, 6289), // Swamp snake hide
        )

    fun cast(player: Player): Boolean {
        val leatherType =
            LEATHERS.firstOrNull {
                player.inventory.containsItem(it.hide, 1)
            }

        if (leatherType == null) {
            player.message("You don't have any hides to tan.")
            return false
        }

        val amount = player.inventory.getNumberOf(leatherType.hide)

        if (amount <= 0) {
            return false
        }

        player.lock(2)

        player.animate(Animation(6296))
        player.gfx(Graphics(1068))

        player.inventory.deleteItem(leatherType.hide, amount)
        player.inventory.addItem(leatherType.leather, amount)

        return true
    }
}
