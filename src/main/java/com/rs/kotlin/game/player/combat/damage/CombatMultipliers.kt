package com.rs.kotlin.game.player.combat.damage

import com.rs.java.game.Entity
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player

class CombatMultipliers() {

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
        val boost: Double
    ) {
        FULL_SLAYER_HELMET(15492, Style.HYBRID, Type.SLAYER, 0.20),
        SLAYER_HELMET(13263, Style.MELEE, Type.SLAYER, 0.20),
        SALVE_AMULET_E(10588, Style.HYBRID, Type.UNDEAD, 0.20),
        BLACK_MASK(8921, Style.MELEE, Type.SLAYER, 0.15),
        HEXCREST(15488, Style.MAGIC, Type.SLAYER, 0.15),
        FOCUSSIGHT(15490, Style.RANGE, Type.SLAYER, 0.15),
        SALVE_AMULET(4081, Style.MELEE, Type.UNDEAD, 0.15),
        DARKLIGHT(6746, Style.MELEE, Type.DEMON, 0.60),
        SILVERLIGHT(2402, Style.MELEE, Type.DEMON, 0.60),
        ;

        companion object {
            fun fromItemId(id: Int) = entries.firstOrNull { it.itemId == id }
        }
    }

    companion object {
        var UNDEAD_NPCS: Array<String> = arrayOf(
            "ghost", "zombie", "revenant", "skeleton", "abberant spectre", "banshee",
            "ghoul", "vampire", "skeletal"
        )
        var DEMON_NPCS: Array<String> = arrayOf(
            "demon"
        )
        var DRAGON_NPCS: Array<String> = arrayOf(
            "dragon", "elvarg"
        )
        fun getMultiplier(player: Player, target: Entity, style: Style): Double {
            var multiplier = 1.0
            if (target is NPC) {
                val maxHitDummy = target.id == 4474
                val isUndead = UNDEAD_NPCS.any { undeadName ->
                    target.definitions.name.contains(undeadName, ignoreCase = true)
                }
                val isDemon = DEMON_NPCS.any { demonName ->
                    target.definitions.name.contains(demonName, ignoreCase = true)
                }
                val isDragon = DRAGON_NPCS.any { dragonName ->
                    target.definitions.name.contains(dragonName, ignoreCase = true)
                }

                val equippedItem = player.equipment.items.itemsCopy
                    .mapNotNull { it?.id }.firstNotNullOfOrNull { BoostEquipment.fromItemId(it) }

                if (equippedItem != null &&
                    (style == equippedItem.style || equippedItem.style == Style.HYBRID)
                ) {
                    when (equippedItem.type) {
                        Type.SLAYER -> {
                            if (maxHitDummy || (player.slayerTask != null &&
                                        player.slayerManager.isValidTask(target.name))
                            ) {
                                multiplier += equippedItem.boost
                            }
                        }
                        Type.DEMON -> {
                            if (isDemon) {
                                multiplier += equippedItem.boost;
                            }
                        }
                        Type.DRAGON -> {
                            if (isDragon) {
                                multiplier += equippedItem.boost;
                            }
                        }

                        Type.UNDEAD -> {
                            if (maxHitDummy || isUndead) {
                                multiplier += equippedItem.boost
                            }
                        }

                        Type.REGULAR -> {
                            if (maxHitDummy) {
                                multiplier += equippedItem.boost
                            }
                        }
                    }
                }
                return multiplier
            }
            return multiplier
        }
    }
}
