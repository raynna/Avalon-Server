package com.rs.kotlin.game.npc.combatdata

import com.google.gson.annotations.SerializedName

data class NpcCombatDefinition(
    val ids: List<Int> = emptyList(),
    val names: List<String> = emptyList(),

    val attackAnim: Int = -1,
    val attackSound: Int = -1,
    val defenceAnim: Int = -1,
    val defendSound: Int = -1,
    val deathAnim: Int = -1,
    val deathDelay: Int = 0,
    val deathSound: Int = -1,
    val respawnDelay: Int = 0,
    val hitpoints: Int = 1,
    val maxHit: Int = 1,

    val attackStyle: AttackStyle = AttackStyle.MELEE,
    val attackMethod: AttackMethod = AttackMethod.MELEE,

    val combatLevels: Map<String, Int> = emptyMap(),

    val attackRange: Int = -1,
    val attackGfx: Int = -1,
    val attackProjectile: Int = -1,

    @SerializedName("agressivenessType")
    val aggressivenessType: AggressivenessType = AggressivenessType.PASSIVE,
    val aggroDistance: Int = -1,
    val deAggroDistance: Int = -1,
    val maxDistFromSpawn: Int = 12
) {
    override fun toString(): String {
        return buildString {
            appendLine("NpcCombatDefinition(")
            if (ids.isNotEmpty()) appendLine("  ids=$ids")
            if (names.isNotEmpty()) appendLine("  names=$names")
            appendLine("  hp=$hitpoints, maxHit=$maxHit, respawn=$respawnDelay")
            appendLine("  attackStyle=$attackStyle, method=$attackMethod")
            appendLine("  attackAnim=$attackAnim, defenceAnim=$defenceAnim, deathAnim=$deathAnim")
            appendLine("  gfx=$attackGfx, proj=$attackProjectile")
            appendLine("  aggressiveness=$aggressivenessType, aggroDist=$aggroDistance")
            appendLine(")")
        }
    }
}

enum class AttackStyle { MELEE, RANGE, MAGIC }
enum class AttackMethod { MELEE, RANGE, MAGE, SPECIAL, SPECIAL2 }
enum class AggressivenessType { PASSIVE, AGGRESSIVE }
