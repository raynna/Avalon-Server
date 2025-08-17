package com.rs.kotlin.game.player.equipment

import com.rs.java.game.player.Equipment
import com.rs.kotlin.game.player.equipment.EquipmentSets.containsAny

data class EquipmentRequirement(
    val required: List<Int> = emptyList(),
    val groups: List<List<Int>> = emptyList()
) {
    fun isSatisfied(equipment: Equipment): Boolean {
        if (!equipment.containsAll(*required.toIntArray())) return false

        return groups.all { group -> equipment.containsAny(*group.toIntArray()) }
    }
}

