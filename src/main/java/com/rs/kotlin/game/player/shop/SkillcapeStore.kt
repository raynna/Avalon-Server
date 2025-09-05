package com.rs.kotlin.game.player.shop

import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills

/**
 * Kotlin rewrite of SkillcapeStore.
 * No static mutable state, just generates capes based on player stats.
 */
object SkillcapeStore {

    const val CURRENCY_SPRITE: Int = 1371
    const val TITLE: String = "Max' Skillcape Store"

    fun generateCapes(player: Player): List<Item> {
        val s = player.skills
        val capes = mutableListOf<Item>()

        fun addIfLevel(skill: Int, level: Int, itemId: Int) {
            if (s.getLevelForXp(skill) >= level) capes.add(Item(itemId, 1))
        }

        addIfLevel(Skills.ATTACK, 99, 9748)
        addIfLevel(Skills.STRENGTH, 99, 9751)
        addIfLevel(Skills.DEFENCE, 99, 9754)
        addIfLevel(Skills.RANGE, 99, 9757)
        addIfLevel(Skills.PRAYER, 99, 9760)
        addIfLevel(Skills.MAGIC, 99, 9763)
        addIfLevel(Skills.RUNECRAFTING, 99, 9766)
        addIfLevel(Skills.HITPOINTS, 99, 9769)
        addIfLevel(Skills.AGILITY, 99, 9772)
        addIfLevel(Skills.HERBLORE, 99, 9775)
        addIfLevel(Skills.THIEVING, 99, 9778)
        addIfLevel(Skills.CRAFTING, 99, 9781)
        addIfLevel(Skills.FLETCHING, 99, 9784)
        addIfLevel(Skills.SLAYER, 99, 9787)
        addIfLevel(Skills.CONSTRUCTION, 99, 9790)
        addIfLevel(Skills.MINING, 99, 9793)
        addIfLevel(Skills.SMITHING, 99, 9796)
        addIfLevel(Skills.FISHING, 99, 9799)
        addIfLevel(Skills.COOKING, 99, 9802)
        addIfLevel(Skills.FIREMAKING, 99, 9805)
        addIfLevel(Skills.WOODCUTTING, 99, 9808)
        addIfLevel(Skills.FARMING, 99, 9811)
        addIfLevel(Skills.HUNTER, 99, 9949)
        addIfLevel(Skills.SUMMONING, 99, 12170)

        // Dungeoneering 99 + 120 capes
        addIfLevel(Skills.DUNGEONEERING, 99, 18509)
        if (s.getLevelForXp(Skills.DUNGEONEERING) == 120)
            capes.add(Item(19709, 1))

        // Max cape
        if (player.hasMaxCapeRequirements())
            capes.add(Item(20767, 1))

        return capes
    }
}
