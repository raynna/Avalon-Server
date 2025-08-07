package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.CombatDefinitions
import com.rs.java.game.player.CombatDefinitions.*
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.CombatCalculations.MeleeCombat.getAttackBonusType
import com.rs.kotlin.game.player.combat.magic.Spell
import kotlin.math.floor
import kotlin.math.round

object CombatCalculations {
    private sealed class BonusType(val index: Int) {
        data object StabAttack : BonusType(0)
        data object SlashAttack : BonusType(1)
        data object CrushAttack : BonusType(2)
        data object MagicAttack : BonusType(3)
        data object RangeAttack : BonusType(4)
        data object StabDefence : BonusType(5)
        data object SlashDefence : BonusType(6)
        data object CrushDefence : BonusType(7)
        data object MagicDefence : BonusType(8)
        data object RangeDefence : BonusType(9)
        data object StrengthBonus : BonusType(10)
        data object RangedStrBonus : BonusType(11)
        data object MagicDamage : BonusType(12)
    }

    interface AccuracyCalculator {
        fun calculateAccuracy(player: Player, target: Entity, weaponId: Int, attackStyle: Int): Boolean
    }

    interface MaxHitCalculator {
        fun calculateMaxHit(player: Player, weaponId: Int, attackStyle: Int, specialMultiplier: Double = 1.0): Int
    }

    private object MeleeCombat : AccuracyCalculator, MaxHitCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, weaponId: Int, attackStyle: Int): Boolean {
            val attackBonus = player.combatDefinitions.bonuses[getAttackBonusType(player, weaponId, attackStyle).index].toDouble()
            var attack = getBaseAttackLevel(player, weaponId, attackStyle)
            attack *= (1.0 + attackBonus / 64.0)

            val (defenceBonus, defenceLevel) = getDefenceValues(player, target, weaponId, attackStyle, isRanged = false)
            var defence = getBaseDefenceLevel(target, defenceLevel)
            defence *= (1.0 + defenceBonus / 64.0)

            return calculateHitProbability(attack, defence)
        }

        override fun calculateMaxHit(player: Player, weaponId: Int, attackStyle: Int, specialMultiplier: Double): Int {
            val strengthLvl = player.skills.getLevel(Skills.STRENGTH).toDouble()
            val effectiveStrength = 8.0 + floor(strengthLvl * player.prayer.strengthMultiplier)
            val strengthBonus = player.combatDefinitions.bonuses[BonusType.StrengthBonus.index].toDouble()

            val baseDamage = 5.0 + effectiveStrength * (1.0 + (strengthBonus / 64.0))
            var maxHit = floor(baseDamage * specialMultiplier).toInt()

            when (player.combatDefinitions.getStyle(weaponId, attackStyle)) {
                AGGRESSIVE -> maxHit += 3
                CONTROLLED -> maxHit += 1
                else -> {}
            }

            return maxHit
        }

        fun getAttackBonusType(player: Player, weaponId: Int, attackStyle: Int): BonusType {
            return when (getAttackStyle(player, weaponId, attackStyle)) {
                else -> BonusType.CrushAttack
            }
        }
    }

    private object RangedCombat : AccuracyCalculator, MaxHitCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, weaponId: Int, attackStyle: Int): Boolean {
            var range = getBaseRangedLevel(player, weaponId, attackStyle)
            val rangeBonus = player.combatDefinitions.bonuses[BonusType.RangeAttack.index].toDouble()
            range *= (1.0 + rangeBonus / 64.0)

            val (defenceBonus, defenceLevel) = getDefenceValues(player, target, weaponId, attackStyle, isRanged = true)
            var rangedefence = getBaseDefenceLevel(target, defenceLevel)
            rangedefence *= (1.0 + defenceBonus / 64.0)

            return calculateHitProbability(range, rangedefence)
        }

        override fun calculateMaxHit(player: Player, weaponId: Int, attackStyle: Int, specialMultiplier: Double): Int {
            val rangedLvl = player.skills.getLevel(Skills.RANGE).toDouble()
            val styleBonus = getRangedStyleBonus(attackStyle)
            val effectiveStrength = round(rangedLvl * player.prayer.rangedMultiplier) + styleBonus
            val strengthBonus = player.combatDefinitions.bonuses[BonusType.RangedStrBonus.index].toDouble()

            val baseDamage = 5.0 + ((effectiveStrength + 8.0) * (strengthBonus + 64.0)) / 64.0
            var maxHit = floor(baseDamage * specialMultiplier).toInt()

            if (player.combatDefinitions.getStyle(weaponId, attackStyle) == ACCURATE) {
                maxHit += 3
            }

            return maxHit
        }

        private fun getRangedStyleBonus(attackStyle: Int): Double = when (attackStyle) {
            0 -> 3.0 //accurate
            1 -> 0.0 //rapid
            else -> 1.0 //longrange
        }
    }

    private object MagicCombat : AccuracyCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, weaponId: Int, attackStyle: Int): Boolean {
            val attackBonus = player.combatDefinitions.bonuses[BonusType.MagicAttack.index].toDouble()
            var attack = getBaseMagicLevel(player)
            attack *= (1.0 + attackBonus / 64.0)

            val (defenceBonus, defenceLevel) = getMagicDefenceValues(target)
            val defence = defenceLevel * (1.0 + defenceBonus / 64.0)

            return calculateHitProbability(attack, defence)
        }

        fun calculateMaxHit(player: Player, spell: Spell): Int {
            var maxHit = spell.damage.toDouble()
            val magicDamageBonus = player.combatDefinitions.bonuses[BonusType.MagicDamage.index].toDouble()

            maxHit *= (1.0 + (magicDamageBonus / 100.0))
            maxHit *= getMagicLevelBoostMultiplier(player)

            return maxHit.coerceAtLeast(0.0).toInt()
        }

        private fun getMagicLevelBoostMultiplier(player: Player): Double {
            val levelDiff = player.skills.getLevel(Skills.MAGIC) - player.skills.getLevelForXp(Skills.MAGIC)
            return 1.0 + (levelDiff * 0.03).coerceAtLeast(0.0)
        }

        private fun getMagicDefenceValues(target: Entity): Pair<Double, Double> {
            return when (target) {
                is Player -> {
                    val defenceBonus = target.combatDefinitions.bonuses[BonusType.MagicDefence.index].toDouble()
                    val defence = round(
                        (target.skills.getLevel(Skills.MAGIC) * 0.7 +
                                target.skills.getLevel(Skills.DEFENCE) * 0.3) *
                                target.prayer.magicMultiplier
                    ) + 8.0
                    Pair(defenceBonus, defence)
                }
                is NPC -> {
                    val defenceBonus = target.bonuses?.get(BonusType.MagicDefence.index)?.toDouble() ?: target.combatLevel.toDouble()
                    val defence = round(
                        (defenceBonus * 0.7) + (defenceBonus * 0.3)
                    ) + 8.0
                    Pair(defenceBonus, defence)
                }
                else -> Pair(0.0, 0.0)
            }
        }
    }

    // Public API functions
    fun calculateMeleeAccuracy(player: Player, target: Entity, weaponId: Int, attackStyle: Int): Boolean {
        return MeleeCombat.calculateAccuracy(player, target, weaponId, attackStyle)
    }

    fun calculateRangedAccuracy(player: Player, target: Entity, weaponId: Int, attackStyle: Int): Boolean {
        return RangedCombat.calculateAccuracy(player, target, weaponId, attackStyle)
    }

    fun calculateMagicAccuracy(player: Player, target: Entity): Boolean {
        return MagicCombat.calculateAccuracy(player, target, 0, 0) // weaponId and attackStyle not used for magic
    }

    fun calculateMeleeMaxHit(player: Player, weaponId: Int, attackStyle: Int, specialMultiplier: Double = 1.0): Int {
        return MeleeCombat.calculateMaxHit(player, weaponId, attackStyle, specialMultiplier)
    }

    fun calculateRangedMaxHit(player: Player, weaponId: Int, attackStyle: Int, specialMultiplier: Double = 1.0): Int {
        return RangedCombat.calculateMaxHit(player, weaponId, attackStyle, specialMultiplier)
    }

    fun calculateMagicMaxHit(player: Player, spell: Spell): Int {
        return MagicCombat.calculateMaxHit(player, spell)
    }

    // Helper functions
    private fun calculateHitProbability(attack: Double, defence: Double): Boolean {
        val prob = when {
            attack < defence -> (attack - 1.0) / (defence * 2.0)
            attack >= defence -> 1.0 - (defence + 1.0) / (attack * 2.0)
            else -> attack / defence
        }
        return prob >= Utils.getRandomDouble(100.0) / 100.0
    }

    private fun getBaseAttackLevel(player: Player, weaponId: Int, attackStyle: Int): Double {
        var attack = round(player.skills.getLevel(Skills.ATTACK) * player.prayer.getAttackMultiplier()) + 8.0
        when (player.combatDefinitions.getStyle(weaponId, attackStyle)) {
            ACCURATE -> attack += 3.0
            CONTROLLED -> attack += 1.0
            else -> {}
        }
        return attack
    }

    private fun getBaseRangedLevel(player: Player, weaponId: Int, attackStyle: Int): Double {
        var range = round(player.skills.getLevel(Skills.RANGE) * player.prayer.getRangedMultiplier()) + 8.0
        when (player.combatDefinitions.getStyle(weaponId, attackStyle)) {
            ACCURATE -> range += 3.0
            CombatDefinitions.RAPID -> range += 1.0
            else -> {}
        }
        return range
    }

    private fun getBaseMagicLevel(player: Player): Double {
        return round(player.skills.getLevel(Skills.MAGIC) * player.prayer.magicMultiplier) + 8.0
    }

    private fun getBaseDefenceLevel(target: Entity, defenceLevel: Double): Double {
        return round(defenceLevel * (if (target is Player) target.prayer.getDefenceMultiplier() else 1.0)) + 8.0
    }

    private fun getDefenceValues(
        player: Player,
        target: Entity,
        weaponId: Int,
        attackStyle: Int,
        isRanged: Boolean
    ): Pair<Double, Double> {
        return when (target) {
            is Player -> getPlayerDefenceValues(target, weaponId, attackStyle, isRanged)
            is NPC -> getNpcDefenceValues(player, target, weaponId, attackStyle, isRanged)
            else -> Pair(0.0, 0.0)
        }
    }

    private fun getPlayerDefenceValues(
        player: Player,
        weaponId: Int,
        attackStyle: Int,
        isRanged: Boolean
    ): Pair<Double, Double> {
        val defenceBonus = if (isRanged) {
            player.combatDefinitions.bonuses[BonusType.RangeDefence.index].toDouble()
        } else {
            player.combatDefinitions.bonuses[getMeleeDefenceBonusType(player, weaponId, attackStyle).index].toDouble()
        }

        val defenceLevel = player.skills.getLevel(Skills.DEFENCE).toDouble()
        val styleBonus = when (player.combatDefinitions.getStyle(weaponId, attackStyle)) {
            DEFENSIVE -> 3.0
            CONTROLLED -> 1.0
            else -> 0.0
        }

        return Pair(defenceBonus, defenceLevel + styleBonus)
    }

    private fun getNpcDefenceValues(
        player: Player,
        npc: NPC,
        weaponId: Int,
        attackStyle: Int,
        isRanged: Boolean
    ): Pair<Double, Double> {
        return if (isRanged) {
            val defenceBonus = npc.bonuses?.get(BonusType.RangeDefence.index)?.toDouble() ?: npc.combatLevel.toDouble()
            val defenceLevel = npc.bonuses?.get(NPC_DEFENCE_LEVEL)?.toDouble() ?: npc.combatLevel.toDouble()
            Pair(defenceBonus, defenceLevel)
        } else {
            val attackMethod = getAttackBonusType(player, weaponId, attackStyle)
            val defenceBonus = when (attackMethod) {
                BonusType.StabAttack -> npc.bonuses?.get(NPC_STAB_DEFENCE)?.toDouble() ?: 0.0
                BonusType.SlashAttack -> npc.bonuses?.get(NPC_SLASH_DEFENCE)?.toDouble() ?: 0.0
                else -> npc.bonuses?.get(NPC_CRUSH_DEFENCE)?.toDouble() ?: npc.combatLevel.toDouble()
            }
            val defenceLevel = npc.bonuses?.get(NPC_DEFENCE_LEVEL)?.toDouble() ?: npc.combatLevel.toDouble()
            Pair(defenceBonus, defenceLevel)
        }
    }

    private fun getMeleeDefenceBonusType(player: Player, weaponId: Int, attackStyle: Int): BonusType {
        return when (getAttackBonusType(player, weaponId, attackStyle)) {
            BonusType.StabAttack -> BonusType.StabDefence
            BonusType.SlashAttack -> BonusType.SlashDefence
            else -> BonusType.CrushDefence
        }
    }

    private fun getAttackStyle(player: Player, weaponId: Int, attackStyle: Int): Int {
        // Should be implemented based on weapon styles
        return player.combatDefinitions.getStyle(weaponId, attackStyle)
    }
}