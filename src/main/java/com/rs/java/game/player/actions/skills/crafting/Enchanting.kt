package com.rs.java.game.player.actions.skills.crafting

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills

object Enchanting {
    enum class Types(
        val enchantLevel: Int,
        val level: Int,
        val ring: Int,
        val necklace: Int,
        val amulet: Int,
        val bracelet: Int,
    ) {
        SAPPHIRE(1, 7, 2550, 3853, 1727, 11074),
        EMERALD(2, 27, 2552, 5521, 1729, 11079),
        RUBY(3, 49, 2568, 11194, 1725, 11088),
        DIAMOND(4, 57, 2570, 11090, 1731, 11095),
        DRAGONSTONE(5, 86, 2572, 11105, 1712, 11188),
        ONYX(6, 87, 6583, 11128, 6585, 11133),
        ;

        companion object {
            private val lookup = entries.associateBy { it.enchantLevel }

            fun forLevel(level: Int) = lookup[level]
        }
    }

    enum class Base(
        val level: Int,
        val baseRing: Int,
        val baseNecklace: Int,
        val baseAmulet: Int,
        val baseBracelet: Int,
    ) {
        SAPPHIRE(1, 1637, 1656, 1694, 11072),
        EMERALD(2, 1639, 1658, 1696, 11076),
        RUBY(3, 1641, 1660, 1698, 11085),
        DIAMOND(4, 1643, 1662, 1700, 11092),
        DRAGONSTONE(5, 1645, -1, 1702, -1),
        ONYX(6, 6575, 6577, 6581, 11130),
        ;

        companion object {
            private val lookup = entries.associateBy { it.level }

            fun forLevel(level: Int) = lookup[level]
        }
    }

    fun startEnchant(
        player: Player,
        itemId: Int,
        slotId: Int,
        enchantLevel: Int,
        xp: Double,
    ): Boolean {
        val base = Base.forLevel(enchantLevel) ?: return false
        val type = Types.forLevel(enchantLevel) ?: return false

        if (!canBeEnchanted(base, itemId)) {
            player.packets.sendGameMessage("This item can't be enchanted.")
            return false
        }

        if (player.skills.getLevel(Skills.MAGIC) < type.level) {
            player.packets.sendGameMessage(
                "You need atleast ${type.level} to enchant ${type.name.lowercase()}",
            )
            return false
        }

        return when (itemId) {
            base.baseRing -> enchant(player, base.baseRing, type.ring, enchantLevel, slotId, xp)
            base.baseNecklace -> enchant(player, base.baseNecklace, type.necklace, enchantLevel, slotId, xp)
            base.baseAmulet -> enchant(player, base.baseAmulet, type.amulet, enchantLevel, slotId, xp)
            base.baseBracelet -> enchant(player, base.baseBracelet, type.bracelet, enchantLevel, slotId, xp)
            else -> false
        }
    }

    private fun enchant(
        player: Player,
        toEnchant: Int,
        toMake: Int,
        enchantLevel: Int,
        slotId: Int,
        xp: Double,
    ): Boolean {
        val name = ItemDefinitions.getItemDefinitions(toEnchant).name
        val makeName = ItemDefinitions.getItemDefinitions(toMake).name

        when (enchantLevel) {
            1, 2 -> {
                player.animate(Animation(719))
                player.gfx(Graphics(114, 0, 100))
            }

            3, 4 -> {
                player.animate(Animation(720))
                player.gfx(Graphics(115, 0, 100))
            }

            5 -> {
                player.animate(Animation(721))
                player.gfx(Graphics(116, 0, 100))
            }

            6 -> {
                player.animate(Animation(721))
                player.gfx(Graphics(452, 0, 100))
            }
        }

        player.inventory.deleteItem(slotId, Item(toEnchant))
        player.inventory.addItem(toMake, 1)

        player.skills.addXp(Skills.MAGIC, enchantLevel * 10.0)

        player.packets.sendGameMessage(
            "You enchant the $name into a $makeName!",
        )

        return true
    }

    private fun canBeEnchanted(
        base: Base,
        itemId: Int,
    ): Boolean =
        itemId == base.baseRing ||
            itemId == base.baseNecklace ||
            itemId == base.baseAmulet ||
            itemId == base.baseBracelet
}
