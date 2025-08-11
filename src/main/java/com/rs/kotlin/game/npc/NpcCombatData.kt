package com.rs.kotlin.game.npc

data class NpcCombatData(
    val id: Int,
    val name: String,
    val releaseDate: String? = null,
    val aliases: List<String> = emptyList(),
    val members: Boolean = false,
    val combatLevel: Int,
    val size: String? = null,
    val examine: String? = null,

    // Combat attributes
    val attributes: List<String> = emptyList(),
    val xpBonus: Double = 0.0,
    val maxHit: MaxHitData,
    val aggressive: Boolean = false,
    val poisonous: PoisonData? = null,
    val attackStyles: List<String> = emptyList(),
    val attackSpeedTicks: Int = 4,
    val respawnTicks: Int = 16,

    // Slayer info
    val slayerLevel: Int? = null,
    val slayerXp: Double? = null,
    val categories: List<String> = emptyList(),
    val assignedBy: List<String> = emptyList(),

    // Combat stats
    val attackLevel: Int = 1,
    val strengthLevel: Int = 1,
    val defenceLevel: Int = 1,
    val magicLevel: Int = 1,
    val rangedLevel: Int = 1,
    val constitutionLevel: Int = 1,

    // Aggressive stats
    val attackBonus: Int = 0,
    val strengthBonus: Int = 0,
    val magicBonus: Int = 0,
    val magicStrengthBonus: Int = 0,
    val rangedBonus: Int = 0,
    val rangedStrengthBonus: Int = 0,

    // Defences
    val meleeDefence: MeleeDefence,
    val magicDefence: MagicDefence,
    val rangedDefence: RangedDefence,

    // Immunities
    val immunities: Immunities = Immunities()
)

data class MaxHitData(
    val melee: Int? = null,
    val dragonfire: Int? = null
)

data class PoisonData(
    val damage: Int,
    val type: String = "poison" // could be "poison" or "venom"
)

data class MeleeDefence(
    val stab: Int = 0,
    val slash: Int = 0,
    val crush: Int = 0
)

data class MagicDefence(
    val base: Int = 0,
    val elementalWeakness: String? = null,
    val weaknessPercentage: Double? = null
)

data class RangedDefence(
    val light: Int = 0,
    val standard: Int = 0,
    val heavy: Int = 0
)

data class Immunities(
    val poison: Boolean = false,
    val venom: Boolean = false,
    val cannons: Boolean = false,
    val thralls: Boolean = false,
    val burn: Boolean = false
)
