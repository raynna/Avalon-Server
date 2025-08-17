package com.rs.kotlin.game.player.equipment

import com.rs.java.game.item.Item
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.damage.CombatMultipliers

object EquipmentSets {
    private val VOID_MAGE_HELM = 11663
    private val VOID_RANGE_HELM = 11664
    private val VOID_MELEE_HELM = 11665
    private val VOID_HELMETS = listOf(VOID_MAGE_HELM, VOID_RANGE_HELM, VOID_MELEE_HELM)
    private val ELITE_VOID_TOPS = listOf(19785, 19787, 19789, 19803)// melee/range/mage helms
    private val ELITE_VOID_BOTTOMS = listOf(19786, 19788, 19790, 19804)// melee/range/mage helms
    private val VOID_TOP = 8839
    private val VOID_ROBE = 8840
    private val VOID_GLOVES = 8842
    private val VOID_DEFLECTOR = 19712

    private val DHAROK_HELM = listOf(4716, 4726, 4736, 4746, 4756)
    private val DHAROK_TOP = listOf(4718, 4728, 4738, 4748, 4758)
    private val DHAROK_LEGS = listOf(4720, 4730, 4740, 4750, 4760)
    private val DHAROK_GREATAXE = listOf(4722, 4732, 4742, 4752, 4762)


    val VOID_SET = EquipmentRequirement(
        required = emptyList(),
        groups = listOf(
            VOID_HELMETS,                                // one void helm
            listOf(VOID_TOP, VOID_DEFLECTOR) + ELITE_VOID_TOPS,  // top or elite top or deflector
            listOf(VOID_ROBE, VOID_DEFLECTOR) + ELITE_VOID_BOTTOMS, // robe or elite bottom or deflector
            listOf(VOID_GLOVES, VOID_DEFLECTOR)          // gloves or deflector
        )
    )

    val ELITE_VOID_SET = EquipmentRequirement(
        required = emptyList(),
        groups = listOf(
            VOID_HELMETS,                                // one void helm
            ELITE_VOID_TOPS + VOID_DEFLECTOR,            // elite top or deflector
            ELITE_VOID_BOTTOMS + VOID_DEFLECTOR,         // elite bottom or deflector
            listOf(VOID_GLOVES, VOID_DEFLECTOR)          // gloves or deflector
        )
    )


    fun getSet(player: Player): EquipmentSet {
        val equipment = player.equipment

        return when {
            isWearingEliteVoidMelee(equipment) -> EquipmentSet.ELITE_VOID_MELEE
            isWearingEliteVoidRange(equipment) -> EquipmentSet.ELITE_VOID_RANGE
            isWearingEliteVoidMagic(equipment) -> EquipmentSet.ELITE_VOID_MAGIC

            isWearingVoidMelee(equipment) -> EquipmentSet.VOID_MELEE
            isWearingVoidRange(equipment) -> EquipmentSet.VOID_RANGE
            isWearingVoidMagic(equipment) -> EquipmentSet.VOID_MAGIC

            isWearingDharok(equipment) -> EquipmentSet.DHAROK
            else -> EquipmentSet.NONE
        }
    }

    fun getDharokMultiplier(player: Player): Double {
        val equipment = player.equipment
        if (!isWearingDharok(equipment)) return 1.0

        val maxHP = player.maxHitpoints.toDouble() / 10
        val currentHP = player.hitpoints.toDouble() / 10

        return 1.0 + (maxHP - currentHP) / 100.0
    }



    fun getDamageMultiplier(set: EquipmentSet, style: CombatMultipliers.Style): Double {
        return when (set) {
            EquipmentSet.VOID_MELEE -> if (style == CombatMultipliers.Style.MELEE) 1.10 else 1.0
            EquipmentSet.VOID_RANGE -> if (style == CombatMultipliers.Style.RANGE) 1.10 else 1.0
            EquipmentSet.VOID_MAGIC -> 1.0 // base void mage = accuracy only, no damage boost

            EquipmentSet.ELITE_VOID_MELEE -> if (style == CombatMultipliers.Style.MELEE) 1.10 else 1.0
            EquipmentSet.ELITE_VOID_RANGE -> if (style == CombatMultipliers.Style.RANGE) 1.125 else 1.0 // 12.5% dmg
            EquipmentSet.ELITE_VOID_MAGIC -> if (style == CombatMultipliers.Style.MAGIC) 1.05 else 1.0  // +5% dmg

            EquipmentSet.DHAROK -> 1.0
            EquipmentSet.NONE -> 1.0
        }
    }


    fun getAccuracyMultiplier(set: EquipmentSet, style: CombatMultipliers.Style): Double {
        return when (set) {
            EquipmentSet.VOID_MELEE -> if (style == CombatMultipliers.Style.MELEE) 1.10 else 1.0
            EquipmentSet.VOID_RANGE -> if (style == CombatMultipliers.Style.RANGE) 1.10 else 1.0
            EquipmentSet.VOID_MAGIC -> if (style == CombatMultipliers.Style.MAGIC) 1.45 else 1.0

            EquipmentSet.ELITE_VOID_MELEE -> if (style == CombatMultipliers.Style.MELEE) 1.10 else 1.0
            EquipmentSet.ELITE_VOID_RANGE -> if (style == CombatMultipliers.Style.RANGE) 1.10 else 1.0 // elite bonus is dmg only
            EquipmentSet.ELITE_VOID_MAGIC -> if (style == CombatMultipliers.Style.MAGIC) 1.45 else 1.0 // same as normal for accuracy

            EquipmentSet.DHAROK -> 1.0
            EquipmentSet.NONE -> 1.0
        }
    }


    private fun isWearingVoidMelee(equipment: Equipment): Boolean {
        return VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(11665) // melee helm
    }

    private fun isWearingVoidRange(equipment: Equipment): Boolean {
        return VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(11664) // range helm
    }

    private fun isWearingVoidMagic(equipment: Equipment): Boolean {
        return VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(11663) // magic helm
    }

    private fun isWearingEliteVoidMelee(equipment: Equipment): Boolean {
        return ELITE_VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(11665)
    }

    private fun isWearingEliteVoidRange(equipment: Equipment): Boolean {
        return ELITE_VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(11664)
    }

    private fun isWearingEliteVoidMagic(equipment: Equipment): Boolean {
        return ELITE_VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(11663)
    }


    private fun isWearingDharok(equipment: Equipment): Boolean {
        return equipment.containsAny(*DHAROK_HELM.toIntArray()) &&
                equipment.containsAny(*DHAROK_TOP.toIntArray()) &&
                equipment.containsAny(*DHAROK_LEGS.toIntArray()) &&
                equipment.containsAny(*DHAROK_GREATAXE.toIntArray())
    }

    fun Equipment.containsItem(itemId: Int): Boolean =
        items.containsOne(Item(itemId, 1))

    fun Equipment.containsAny(vararg itemIds: Int): Boolean =
        itemIds.any { containsItem(it) }

    fun Equipment.containsAll(vararg itemIds: Int): Boolean =
        itemIds.all { containsItem(it) }

}
