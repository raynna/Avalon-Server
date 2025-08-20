package com.rs.kotlin.game.npc.combatdata

data class CombatData(
    @JvmField val combatLevel: Int,
    @JvmField val attackLevel: Int,
    @JvmField val strengthLevel: Int,
    @JvmField val defenceLevel: Int,
    @JvmField val magicLevel: Int,
    @JvmField val rangedLevel: Int,
    @JvmField val constitutionLevel: Int,
    @JvmField val attackBonus: Int,
    @JvmField val strengthBonus: Int,
    @JvmField val magicBonus: Int,
    @JvmField val magicStrengthBonus: Int,
    @JvmField val rangedBonus: Int,
    @JvmField val rangedStrengthBonus: Int,
    @JvmField val meleeDefence: MeleeDefence,
    @JvmField val magicDefence: MagicDefence,
    @JvmField val rangedDefence: RangedDefence,
    @JvmField val immunities: Immunities,
    @JvmField val xpBonus: Double,
    @JvmField val maxHit: MaxHit,
    @JvmField val aggressive: Boolean,
    @JvmField val attackStyles: List<String>,
    @JvmField val attackSpeedTicks: Int,
    @JvmField val respawnTicks: Int,
    @JvmField val slayerXp: Int
)