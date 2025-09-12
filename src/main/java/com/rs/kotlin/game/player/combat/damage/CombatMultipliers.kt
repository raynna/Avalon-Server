package com.rs.kotlin.game.player.combat.damage

import com.rs.java.game.Entity
import com.rs.java.game.item.Item
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player

class CombatMultipliers {

    enum class Style {
        MELEE, RANGE, MAGIC, HYBRID
    }

    enum class Type {
        SLAYER, UNDEAD, DEMON, DRAGON, REGULAR
    }

    enum class BoostEquipment(
        val itemId: Int,
        val style: Style,
        val type: Type,
        val accuracyBoost: Double = 0.0,
        val damageBoost: Double = 0.0,
        val stack: Boolean = false
    ) {
        FULL_SLAYER_HELMET(Item.getId("item.full_slayer_helmet"), Style.HYBRID, Type.SLAYER, 0.15, 0.15),
        BLACK_SLAYER_HELMET(Item.getId("item.black_slayer_helmet"), Style.HYBRID, Type.SLAYER, 0.15, 0.15),
        TZTOK_SLAYER_HELM(Item.getId("item.tztok_slayer_helmet"), Style.HYBRID, Type.SLAYER, 0.15, 0.15),
        TZKAL_SLAYER_HELM(Item.getId("item.tzkal_slayer_helmet"), Style.HYBRID, Type.SLAYER, 0.15, 0.15),
        SLAYER_HELMET(Item.getId("item.slayer_helmet"), Style.MELEE, Type.SLAYER, 0.15, 0.15),
        SALVE_AMULET_E(Item.getId("item.salve_amulet_e"), Style.HYBRID, Type.UNDEAD, 0.15, 0.20),
        BLACK_MASK(Item.getId("item.black_mask"), Style.MELEE, Type.SLAYER, 0.15, 0.15),
        HEXCREST(Item.getId("item.hexcrest"), Style.MAGIC, Type.SLAYER, 0.15, 0.15),
        FOCUSSIGHT(Item.getId("item.focus_sight"), Style.RANGE, Type.SLAYER, 0.15, 0.15),
        SALVE_AMULET(Item.getId("item.salve_amulet"), Style.MELEE, Type.UNDEAD, 0.15, 0.15),
        DARKLIGHT(Item.getId("item.darklight"), Style.MELEE, Type.DEMON, 0.60, 0.60),
        SILVERLIGHT(Item.getId("item.silverlight"), Style.MELEE, Type.DEMON, 0.60, 0.60),
        DRAGONHUNTER_CROSSBOW(
            Item.getId("item.dragon_hunter_crossbow"),
            Style.RANGE, Type.DRAGON,
            0.25, 0.25, stack = true
        ),
        DRAGONHUNTER_LANCE(
            Item.getId("item.dragon_hunter_lance"),
            Style.MELEE, Type.DRAGON,
            0.25, 0.25, stack = true
        );

        companion object {
            fun fromItemId(id: Int) = entries.firstOrNull { it.itemId == id }
        }
    }

    data class Multipliers(
        val accuracy: Double = 1.0,
        val damage: Double = 1.0
    )

    companion object {
        private val UNDEAD_NPCS = arrayOf(
            "ghost", "zombie", "revenant", "skeleton", "abberant spectre", "banshee",
            "ghoul", "vampire", "skeletal"
        )
        private val DEMON_NPCS = arrayOf("demon")
        private val DRAGON_NPCS = arrayOf("dragon", "elvarg")

        fun getMultipliers(player: Player, target: Entity, style: Style): Multipliers {
            var accuracy = 1.0
            var damage = 1.0

            if (target is NPC) {
                val maxHitDummy = target.id == 4474
                val isUndead = UNDEAD_NPCS.any { target.definitions.name.contains(it, ignoreCase = true) }
                val isDemon = DEMON_NPCS.any { target.definitions.name.contains(it, ignoreCase = true) }
                val isDragon = DRAGON_NPCS.any { target.definitions.name.contains(it, ignoreCase = true) }

                val equippedBoosts = player.equipment.items.itemsCopy
                    .mapNotNull { it?.id }
                    .mapNotNull { BoostEquipment.fromItemId(it) }
                    .filter { style == it.style || it.style == Style.HYBRID }

                equippedBoosts.forEach { boost ->
                    val applies = when (boost.type) {
                        Type.SLAYER -> maxHitDummy || (player.slayerTask != null &&
                                player.slayerManager.isValidTask(target.name))
                        Type.UNDEAD -> maxHitDummy || isUndead
                        Type.DEMON -> maxHitDummy || isDemon
                        Type.DRAGON -> maxHitDummy || isDragon
                        Type.REGULAR -> maxHitDummy
                    }
                    if (applies) {
                        if (boost.stack) {
                            accuracy += boost.accuracyBoost
                            damage += boost.damageBoost
                        } else {
                            accuracy = maxOf(accuracy, 1.0 + boost.accuracyBoost)
                            damage = maxOf(damage, 1.0 + boost.damageBoost)
                        }
                    }
                }
            }
            return Multipliers(accuracy, damage)
        }
    }
}
