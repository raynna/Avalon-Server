package com.rs.kotlin.game.player.combat

enum class AttackStyle(
    val attackSpeedModifier: Int = 0,
    val combatType: CombatType,
    val xpMode: XpMode
) {
    ACCURATE(combatType = CombatType.MELEE, xpMode = XpMode.ATTACK),
    AGGRESSIVE(combatType = CombatType.MELEE, xpMode = XpMode.STRENGTH),
    DEFENSIVE(combatType = CombatType.MELEE, xpMode = XpMode.DEFENCE),
    CONTROLLED(combatType = CombatType.MELEE, xpMode = XpMode.SHARED),

    RAPID(attackSpeedModifier = -1, combatType = CombatType.RANGED, xpMode = XpMode.RANGED),
    LONGRANGE(attackSpeedModifier = 1, combatType = CombatType.RANGED, xpMode = XpMode.DEFENCE);

    companion object {
        fun fromOrdinal(id: Int, weaponStyle: WeaponStyle): AttackStyle {
            return weaponStyle.attackStyles.getOrElse(id) { ACCURATE }
        }
    }
}





enum class XpMode {
    ATTACK, STRENGTH, DEFENCE, SHARED, RANGED
}

enum class CombatType {
    MELEE, RANGED, MAGIC
}

