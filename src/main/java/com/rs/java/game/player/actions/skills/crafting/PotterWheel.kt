package com.rs.java.game.player.actions.skills.crafting

import com.rs.java.game.Animation
import com.rs.java.game.WorldObject
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.actions.Action

class PotterWheel(
    slotId: Int,
    private val wheel: WorldObject,
    private var ticks: Int,
) : Action() {
    private val prod: Products? = Products.forId(slotId)

    override fun start(player: Player): Boolean {
        val product = prod ?: return false

        if (player.skills.getLevel(Skills.CRAFTING) < product.levelRequired) {
            player.packets.sendGameMessage(
                "You need a Crafting level of at least ${product.levelRequired} to create ${product.producedItem.definitions.name}",
            )
            return false
        }

        val req = product.itemsRequired

        if (!player.inventory.containsItem(req.id, req.amount)) {
            player.packets.sendGameMessage(
                "You need atleast ${req.amount} ${req.definitions.name} to create a ${product.producedItem.definitions.name}.",
            )
            return false
        }

        return true
    }

    override fun process(player: Player): Boolean {
        val product = prod ?: return false

        if (player.skills.getLevel(Skills.CRAFTING) < product.levelRequired) {
            player.packets.sendGameMessage(
                "You need a Crafting level of at least ${product.levelRequired} to create ${product.producedItem.definitions.name}",
            )
            return false
        }

        val req = product.itemsRequired

        if (!player.inventory.containsItem(req.id, req.amount)) {
            player.packets.sendGameMessage(
                "You need ${req.definitions.name} to create a ${product.producedItem.definitions.name}.",
            )
            return false
        }

        player.faceObject(wheel)
        return true
    }

    override fun processWithDelay(player: Player): Int {
        val product = prod ?: return -1

        ticks--

        player.animate(Animation(896))

        player.skills.addXp(Skills.CRAFTING, product.experience)

        val req = product.itemsRequired

        player.inventory.deleteItem(req.id, req.amount)
        player.inventory.addItem(product.producedItem)

        player.packets.sendGameMessage(
            "You make a ${product.producedItem.definitions.name.lowercase()}.",
            true,
        )

        return if (ticks > 0) 1 else -1
    }

    override fun stop(player: Player) {
        setActionDelay(player, 3)
    }

    enum class Products(
        val levelRequired: Int,
        val experience: Double,
        val itemsRequired: Item,
        val producedItem: Item,
        val buttonId: Int,
    ) {
        POT(1, 6.0, Item(1761), Item(1787), 0),
        PIE_DISH(7, 15.0, Item(1761), Item(1789), 1),
        BOWL(8, 18.0, Item(1761), Item(1791), 2),
        ;

        companion object {
            private val lookup = entries.associateBy { it.buttonId }

            fun forId(buttonId: Int): Products? = lookup[buttonId]
        }
    }
}
