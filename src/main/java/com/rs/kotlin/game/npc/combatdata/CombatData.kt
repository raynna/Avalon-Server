package com.rs.kotlin.game.npc.combatdata

data class CombatData(
    @JvmField var combatLevel: Int,
    @JvmField var attackLevel: Int,
    @JvmField var strengthLevel: Int,
    @JvmField var defenceLevel: Int,
    @JvmField var magicLevel: Int,
    @JvmField var rangedLevel: Int,
    @JvmField var constitutionLevel: Int,
    @JvmField var attackBonus: Int,
    @JvmField var strengthBonus: Int,
    @JvmField var magicBonus: Int,
    @JvmField var magicStrengthBonus: Int,
    @JvmField var rangedBonus: Int,
    @JvmField var rangedStrengthBonus: Int,
    @JvmField var meleeDefence: MeleeDefence,
    @JvmField var magicDefence: MagicDefence,
    @JvmField var rangedDefence: RangedDefence,
    @JvmField var immunities: Immunities,
    @JvmField var maxHit: MaxHit,
    @JvmField var aggressive: Boolean,
    @JvmField var attackStyles: List<String>,
    @JvmField var attackSpeedTicks: Int,
    @JvmField var respawnTicks: Int,
    @JvmField var slayerXp: Int
) {

    private val baseStats = mapOf(
        "attack" to attackLevel,
        "strength" to strengthLevel,
        "defence" to defenceLevel,
        "magic" to magicLevel,
        "ranged" to rangedLevel,
        "constitution" to constitutionLevel
    )

    fun getBaseStat(stat: String): Int = baseStats[stat.lowercase()] ?: 1

    fun getCurrentStat(stat: String): Int {
        return when (stat.lowercase()) {
            "attack" -> attackLevel
            "strength" -> strengthLevel
            "defence" -> defenceLevel
            "magic" -> magicLevel
            "ranged" -> rangedLevel
            "constitution" -> constitutionLevel
            else -> 1
        }
    }

    /**
     * Drains a specific stat by [amount].
     * Will never drop below 1.
     */
    fun drain(stat: String, amount: Int) {
        when (stat.lowercase()) {
            "attack" -> attackLevel = (attackLevel - amount).coerceAtLeast(1)
            "strength" -> strengthLevel = (strengthLevel - amount).coerceAtLeast(1)
            "defence" -> defenceLevel = (defenceLevel - amount).coerceAtLeast(1)
            "magic" -> magicLevel = (magicLevel - amount).coerceAtLeast(1)
            "ranged" -> rangedLevel = (rangedLevel - amount).coerceAtLeast(1)
            "constitution" -> constitutionLevel = (constitutionLevel - amount).coerceAtLeast(1)
        }
    }

    /**
     * Regenerates drained stats gradually back to base.
     * [rate] = how many points to restore per call.
     */
    fun regenerate(rate: Int = 1) {
        baseStats.forEach { (stat, baseValue) ->
            when (stat) {
                "attack" -> if (attackLevel < baseValue) attackLevel = (attackLevel + rate).coerceAtMost(baseValue)
                "strength" -> if (strengthLevel < baseValue) strengthLevel =
                    (strengthLevel + rate).coerceAtMost(baseValue)

                "defence" -> if (defenceLevel < baseValue) defenceLevel = (defenceLevel + rate).coerceAtMost(baseValue)
                "magic" -> if (magicLevel < baseValue) magicLevel = (magicLevel + rate).coerceAtMost(baseValue)
                "ranged" -> if (rangedLevel < baseValue) rangedLevel = (rangedLevel + rate).coerceAtMost(baseValue)
                "constitution" -> if (constitutionLevel < baseValue) constitutionLevel =
                    (constitutionLevel + rate).coerceAtMost(baseValue)
            }
        }
    }
}