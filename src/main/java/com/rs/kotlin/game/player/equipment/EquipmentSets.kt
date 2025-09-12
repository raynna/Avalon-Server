package com.rs.kotlin.game.player.equipment

import com.rs.java.game.item.Item
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.damage.CombatMultipliers

object EquipmentSets {
    private val VOID_MAGE_HELM = Item.getId("item.void_mage_helm")
    private val VOID_RANGE_HELM = Item.getId("item.void_ranger_helm")
    private val VOID_MELEE_HELM = Item.getId("item.void_melee_helm")
    private val VOID_HELMETS = listOf(VOID_MAGE_HELM, VOID_RANGE_HELM, VOID_MELEE_HELM)
    private val ELITE_VOID_TOPS = listOf(19785, 19787, 19789, 19803)
    private val ELITE_VOID_BOTTOMS = listOf(19786, 19788, 19790, 19804)
    private val VOID_TOP = Item.getId("item.void_knight_top")
    private val VOID_ROBE = Item.getId("item.void_knight_robe")
    private val VOID_GLOVES = Item.getId("item.void_knight_gloves")
    private val VOID_DEFLECTOR = Item.getId("item.void_knight_deflector_2")



    private val CRYSTAL_HELM = Item.getIds("item.crystal_helm")
    private val CRYSTAL_BODY = Item.getIds("item.crystal_body")
    private val CRYSTAL_LEGS = Item.getIds("item.crystal_legs")
    private val CRYSTAL_BOW = Item.getIds(
        "item.crystal_bow_full", "item.crystal_bow_9_10", "item.crystal_bow_8_10",
        "item.crystal_bow_7_10", "item.crystal_bow_6_10", "item.crystal_bow_5_10",
        "item.crystal_bow_4_10", "item.crystal_bow_3_10", "item.crystal_bow_2_10", "item.crystal_bow_1_10",
        "item.bow_of_faerdhinen", "item.bow_of_faerdhinen_red", "item.bow_of_faerdhinen_black", "item.bow_of_faerdhinen_purple",
         "item.bow_of_faerdhinen_green", "item.bow_of_faerdhinen_yellow", "item.bow_of_faerdhinen_blue")

    private val DHAROK_HELM = Item.getIds("item.dharok_s_helm", "item.dharok_s_helm_100", "item.dharok_s_helm_75","item.dharok_s_helm_50", "item.dharok_s_helm_25", "item.dharok_s_helm_0")
    private val DHAROK_TOP = Item.getIds("item.dharok_s_platebody", "item.dharok_s_platebody_100", "item.dharok_s_platebody_75", "item.dharok_s_platebody_50", "item.dharok_s_platebody_25","item.dharok_s_platebody_0" )
    private val DHAROK_LEGS = Item.getIds("item.dharok_s_platelegs", "item.dharok_s_platelegs_100", "item.dharok_s_platelegs_75", "item.dharok_s_platelegs_50", "item.dharok_s_platelegs_25", "item.dharok_s_platelegs_0")
    private val DHAROK_GREATAXE = Item.getIds("item.dharok_s_greataxe", "item.dharok_s_greataxe_100", "item.dharok_s_greataxe_75", "item.dharok_s_greataxe_50", "item.dharok_s_greataxe_25", "item.dharok_s_greataxe_0")


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

            isWearingCrystal(equipment) -> EquipmentSet.CRYSTAL

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



    fun getDamageMultiplier(player: Player, set: EquipmentSet, style: CombatMultipliers.Style): Double {
        val equipment = player.getEquipment()
        return when (set) {
            EquipmentSet.CRYSTAL -> {
                if (style == CombatMultipliers.Style.RANGE) {
                    getCrystalBonuses(equipment).second
                } else 1.0
            }
            EquipmentSet.VOID_MELEE -> if (style == CombatMultipliers.Style.MELEE) 1.10 else 1.0
            EquipmentSet.VOID_RANGE -> if (style == CombatMultipliers.Style.RANGE) 1.20 else 1.0
            EquipmentSet.VOID_MAGIC -> 1.0 // base void mage = accuracy only, no damage boost

            EquipmentSet.ELITE_VOID_MELEE -> if (style == CombatMultipliers.Style.MELEE) 1.10 else 1.0
            EquipmentSet.ELITE_VOID_RANGE -> if (style == CombatMultipliers.Style.RANGE) 1.225 else 1.0 // 12.5% dmg
            EquipmentSet.ELITE_VOID_MAGIC -> if (style == CombatMultipliers.Style.MAGIC) 1.05 else 1.0  // +5% dmg

            EquipmentSet.DHAROK -> 1.0
            EquipmentSet.NONE -> 1.0
        }
    }


    fun getAccuracyMultiplier(player: Player, set: EquipmentSet, style: CombatMultipliers.Style): Double {
        val equipment = player.getEquipment()
        return when (set) {
            EquipmentSet.CRYSTAL -> {
                if (style == CombatMultipliers.Style.RANGE) {
                    getCrystalBonuses(equipment).first
                } else 1.0
            }
            EquipmentSet.VOID_MELEE -> if (style == CombatMultipliers.Style.MELEE) 1.10 else 1.0
            EquipmentSet.VOID_RANGE -> if (style == CombatMultipliers.Style.RANGE) 1.20 else 1.0
            EquipmentSet.VOID_MAGIC -> if (style == CombatMultipliers.Style.MAGIC) 1.45 else 1.0

            EquipmentSet.ELITE_VOID_MELEE -> if (style == CombatMultipliers.Style.MELEE) 1.10 else 1.0
            EquipmentSet.ELITE_VOID_RANGE -> if (style == CombatMultipliers.Style.RANGE) 1.20 else 1.0 // elite bonus is dmg only
            EquipmentSet.ELITE_VOID_MAGIC -> if (style == CombatMultipliers.Style.MAGIC) 1.45 else 1.0 // same as normal for accuracy

            EquipmentSet.DHAROK -> 1.0
            EquipmentSet.NONE -> 1.0
        }
    }


    private fun isWearingVoidMelee(equipment: Equipment): Boolean {
        return VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(VOID_MELEE_HELM)
    }

    private fun isWearingVoidRange(equipment: Equipment): Boolean {
        return VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(VOID_RANGE_HELM)
    }

    private fun isWearingVoidMagic(equipment: Equipment): Boolean {
        return VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(VOID_MAGE_HELM)
    }

    private fun isWearingEliteVoidMelee(equipment: Equipment): Boolean {
        return ELITE_VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(VOID_MELEE_HELM)
    }

    private fun isWearingEliteVoidRange(equipment: Equipment): Boolean {
        return ELITE_VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(VOID_RANGE_HELM)
    }

    private fun isWearingEliteVoidMagic(equipment: Equipment): Boolean {
        return ELITE_VOID_SET.isSatisfied(equipment) &&
                equipment.containsAny(VOID_MAGE_HELM)
    }

    private fun getCrystalBonuses(equipment: Equipment): Pair<Double, Double> {
        if (!hasCrystalBow(equipment)) return 1.0 to 1.0

        var accuracy = 1.0
        var damage = 1.0

        if (equipment.containsAny(*CRYSTAL_HELM.toIntArray())) {
            accuracy += 0.05
            damage += 0.025
        }
        if (equipment.containsAny(*CRYSTAL_BODY.toIntArray())) {
            accuracy += 0.15
            damage += 0.075
        }
        if (equipment.containsAny(*CRYSTAL_LEGS.toIntArray())) {
            accuracy += 0.10
            damage += 0.05
        }

        return accuracy to damage
    }

    private fun isWearingCrystal(equipment: Equipment): Boolean {
        return hasCrystalBow(equipment) && (
                equipment.containsAny(*CRYSTAL_HELM.toIntArray()) ||
                        equipment.containsAny(*CRYSTAL_BODY.toIntArray()) ||
                        equipment.containsAny(*CRYSTAL_LEGS.toIntArray())
                )
    }

    private fun hasCrystalBow(equipment: Equipment): Boolean {
        return equipment.containsAny(*CRYSTAL_BOW.toIntArray())
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
