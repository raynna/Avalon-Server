package com.rs.java.game.player.actions.skills.crafting.loom

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.WorldObject
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.actions.Action

class Loom(
    slotId: Int,
    private val loom: WorldObject,
    private var ticks: Int,
) : Action() {
    private val product: LoomProduct = LoomData.entries[slotId].product

    override fun start(player: Player): Boolean = check(player)

    private fun check(player: Player): Boolean {
        if (player.skills.getLevel(Skills.CRAFTING) < product.level) {
            player.message("You need a Crafting level of at least ${product.level}.")
            return false
        }

        val missing = StringBuilder()

        for (req in product.requirements) {
            val have = player.inventory.getAmountOf(req.getId())

            if (have < req.amount) {
                if (missing.isNotEmpty()) {
                    missing.append(", ")
                }

                missing
                    .append(req.amount)
                    .append(" ")
                    .append(ItemDefinitions.getItemDefinitions(req.getId()).name)
            }
        }

        if (missing.isNotEmpty()) {
            player.message("You need: $missing.")
            return false
        }

        return true
    }

    override fun process(player: Player): Boolean {
        if (ticks <= 0) {
            return false
        }

        if (player.skills.getLevel(Skills.CRAFTING) < product.level) {
            player.message("You need a Crafting level of at least ${product.level} to continue spinning.")
            return false
        }

        for (req in product.requirements) {
            if (!player.inventory.containsItem(req.getId(), req.amount)) {
                val name = ItemDefinitions.getItemDefinitions(req.getId()).name
                player.message("You have run out of $name.")
                return false
            }
        }

        player.faceObject(loom)
        return true
    }

    override fun processWithDelay(player: Player): Int {
        ticks--

        player.animate(896)

        for (req in product.requirements) {
            player.inventory.deleteItem(req.getId(), req.amount)
        }

        player.inventory.addItem(product.getId(), 1)

        if (product.xp > 0) {
            player.skills.addXp(Skills.CRAFTING, product.xp)
        }

        val name = ItemDefinitions.getItemDefinitions(product.getId()).name.lowercase()

        player.message("You make a $name.", true)

        return if (ticks > 0) 1 else -1
    }

    override fun stop(player: Player) {
        setActionDelay(player, 3)
    }
}
