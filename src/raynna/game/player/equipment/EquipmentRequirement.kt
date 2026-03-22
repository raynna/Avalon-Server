package raynna.game.player.equipment

import raynna.game.player.Equipment
import raynna.game.player.equipment.EquipmentSets.containsAny

data class EquipmentRequirement(
    val required: List<Int> = emptyList(),
    val groups: List<List<Int>> = emptyList()
) {
    fun isSatisfied(equipment: Equipment): Boolean {
        if (!equipment.containsAll(*required.toIntArray())) return false

        return groups.all { group -> equipment.containsAny(*group.toIntArray()) }
    }
}

