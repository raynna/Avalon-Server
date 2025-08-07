package com.rs.kotlin.game.player.combat

enum class AttackStyle {
    MELEE_STAB,
    MELEE_SLASH,
    MELEE_CRUSH,
    RANGED,
    MAGIC
}

enum class CombatBonusType(val index: Int) {
    STAB_ATTACK(0),
    SLASH_ATTACK(1),
    CRUSH_ATTACK(2),
    MAGIC_ATTACK(3),
    RANGED_ATTACK(4),
    STAB_DEFENCE(5),
    SLASH_DEFENCE(6),
    CRUSH_DEFENCE(7),
    MAGIC_DEFENCE(8),
    RANGED_DEFENCE(9),
    STRENGTH_BONUS(10),
    RANGED_STR_BONUS(11),
    MAGIC_DAMAGE(12)
}

data class CombatBonuses(val bonuses: IntArray) {
    operator fun get(type: CombatBonusType): Int = bonuses[type.index]
}