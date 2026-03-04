package com.rs.java.game.player.actions.skills.crafting

import com.rs.java.game.Animation
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.actions.Action

class MilestoneCapes(
    slotId: Int,
    private var ticks: Int,
) : Action() {
    private val prod: MilestoneProducts? = MilestoneProducts.forId(slotId)

    override fun start(player: Player): Boolean {
        val product = prod ?: return false

        val levelReq = product.levelRequired

        for (i in 0 until 25) {
            if (player.skills.getLevelForXp(i) < levelReq) {
                player.packets.sendGameMessage(
                    "You need a level of at least $levelReq in all skills to create ${product.producedItem.definitions.name}",
                )
                return false
            }
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
        val levelReq = product.levelRequired

        for (i in 0 until 25) {
            if (player.skills.getLevelForXp(i) < levelReq) {
                player.packets.sendGameMessage(
                    "You need a level of at least $levelReq in all skills to create ${product.producedItem.definitions.name}",
                )
                return false
            }
        }

        val req = product.itemsRequired

        if (!player.inventory.containsItem(req.id, req.amount)) {
            player.packets.sendGameMessage(
                "You need ${req.definitions.name} to create a ${product.producedItem.definitions.name}.",
            )
            return false
        }

        return true
    }

    override fun processWithDelay(player: Player): Int {
        val product = prod ?: return -1

        ticks--

        player.animate(Animation(896))

        if (product.experience > 0) {
            player.skills.addXp(Skills.CRAFTING, product.experience)
        }

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

    enum class MilestoneProducts(
        val levelRequired: Int,
        val experience: Double,
        val itemsRequired: Item,
        val producedItem: Item,
        val buttonId: Int,
    ) {
        MILESTONE_CAPE_10(10, 10.0, Item(1759, 1), Item(20754), 0),
        MILESTONE_CAPE_20(20, 20.0, Item(1759, 2), Item(20755), 1),
        MILESTONE_CAPE_30(30, 30.0, Item(1759, 3), Item(20756), 2),
        MILESTONE_CAPE_40(40, 40.0, Item(1759, 4), Item(20757), 3),
        MILESTONE_CAPE_50(50, 50.0, Item(1759, 5), Item(20758), 4),
        MILESTONE_CAPE_60(60, 60.0, Item(1759, 6), Item(20759), 5),
        MILESTONE_CAPE_70(70, 70.0, Item(1759, 7), Item(20760), 6),
        MILESTONE_CAPE_80(80, 80.0, Item(1759, 8), Item(20761), 7),
        MILESTONE_CAPE_90(90, 90.0, Item(1759, 9), Item(20762), 8),
        ;

        companion object {
            private val lookup = entries.associateBy { it.buttonId }

            fun forId(buttonId: Int): MilestoneProducts? = lookup[buttonId]
        }
    }
}
