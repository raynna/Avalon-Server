package com.rs.kotlin.game.player.combat.damage

import com.rs.Settings
import com.rs.java.game.Entity
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player

class CombatMultipliers() {

    enum class Style {
        MELEE, RANGE, MAGIC, HYBRID
    }

    enum class Type {
        SLAYER, UNDEAD, REGULAR
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
        SALVE_AMULET(4081, Style.MELEE, Type.UNDEAD, 0.15);

        companion object {
            fun fromItemId(id: Int) = entries.firstOrNull { it.itemId == id }
        }
    }

    companion object {
        fun getMultiplier(player: Player, target: Entity, style: Style): Double {
            var multiplier = 1.0
            if (target is NPC) {
                val maxHitDummy = target.id == 4474
                val isUndead = Settings.UNDEAD_NPCS.any { undeadName ->
                    target.definitions.name.contains(undeadName, ignoreCase = true)
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
