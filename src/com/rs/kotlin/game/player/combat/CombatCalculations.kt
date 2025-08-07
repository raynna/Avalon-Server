package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.magic.Spell
import com.rs.kotlin.game.player.combat.melee.MeleeWeapon
import kotlin.math.floor
import kotlin.math.round

object CombatCalculations {

    enum class BonusType(val index: Int) {
        StabAttack(0),
        SlashAttack(1),
        CrushAttack(2),
        MagicAttack(3),
        RangeAttack(4),
        StabDefence(5),
        SlashDefence(6),
        CrushDefence(7),
        MagicDefence(8),
        RangeDefence(9),
        StrengthBonus(10),
        RangedStrBonus(11),
        MagicDamage(12),
    }

    interface AccuracyCalculator {
        fun calculateAccuracy(player: Player, target: Entity, weapon: Weapon?, attackStyle: AttackStyle): Boolean
    }

    interface MaxHitCalculator {
        fun calculateMaxHit(player: Player, attackStyle: AttackStyle, specialMultiplier: Double = 1.0): Hit
    }

    private object MeleeCombat : AccuracyCalculator, MaxHitCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, weapon: Weapon?, attackStyle: AttackStyle): Boolean {
            val bonusType = weapon?.weaponStyle?.getAttackBonusType(attackStyle)
            val attackBonusCorrect = player.combatDefinitions.bonuses[bonusType!!.index].toDouble()

            var attack = getBaseAttackLevel(player, attackStyle)
            attack *= (1.0 + attackBonusCorrect / 64.0)

            val (defenceBonus, defenceLevel) = getDefenceValues(player, target, attackStyle, isRanged = false)
            var defence = getBaseDefenceLevel(target, defenceLevel)
            defence *= (1.0 + defenceBonus / 64.0)
            player.message("[Combat Accuracy] - " +
                    "bonusType: ${bonusType.name}, " +
                    "AttackBonus: $attackBonusCorrect, " +
                    "DefenceBonus: $defenceBonus, " +
                    "Attack: $attack, " +
                    "Defence: $defence, " +
                    "")
            return calculateHitProbability(attack, defence)
        }

        override fun calculateMaxHit(player: Player, attackStyle: AttackStyle, specialMultiplier: Double): Hit {
            val strengthLvl = player.skills.getLevel(Skills.STRENGTH).toDouble()
            val effectiveStrength = 8.0 + floor(strengthLvl * player.prayer.strengthMultiplier)
            val strengthBonus = player.combatDefinitions.bonuses[BonusType.StrengthBonus.index].toDouble()

            val baseDamage = 5.0 + effectiveStrength * (1.0 + strengthBonus / 64.0)
            var maxHit = floor(baseDamage * specialMultiplier).toInt()

            when (attackStyle) {
                AttackStyle.AGGRESSIVE -> maxHit += 3
                AttackStyle.CONTROLLED -> maxHit += 1
                else -> {}
            }
            val damage = Utils.random(maxHit)
            val hit = Hit(player, damage, maxHit, Hit.HitLook.MELEE_DAMAGE)
            if (damage > baseDamage) {
                hit.setCriticalMark()
            }
            return hit
        }
    }

    private object RangedCombat : AccuracyCalculator, MaxHitCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, weapon: Weapon?, attackStyle: AttackStyle): Boolean {
            var range = getBaseRangedLevel(player, attackStyle)
            val rangeBonus = player.combatDefinitions.bonuses[BonusType.RangeAttack.index].toDouble()
            range *= (1.0 + rangeBonus / 64.0)

            val (defenceBonus, defenceLevel) = getDefenceValues(player, target, attackStyle, isRanged = true)
            var rangedefence = getBaseDefenceLevel(target, defenceLevel)
            rangedefence *= (1.0 + defenceBonus / 64.0)

            return calculateHitProbability(range, rangedefence)
        }

        override fun calculateMaxHit(player: Player, attackStyle: AttackStyle, specialMultiplier: Double): Hit {
            val rangedLvl = player.skills.getLevel(Skills.RANGE).toDouble()
            val styleBonus = getRangedStyleBonus(attackStyle)
            val effectiveStrength = round(rangedLvl * player.prayer.rangedMultiplier) + styleBonus
            val strengthBonus = player.combatDefinitions.bonuses[BonusType.RangedStrBonus.index].toDouble()
            player.message("Range str: ${strengthBonus}")
            val baseDamage = 5.0 + ((effectiveStrength + 8.0) * (strengthBonus + 64.0)) / 64.0
            var maxHit = floor(baseDamage * specialMultiplier).toInt()

            if (attackStyle == AttackStyle.ACCURATE) {
                maxHit += 3
            }
            val damage = Utils.random(maxHit)
            val hit = Hit(player, damage, maxHit, Hit.HitLook.RANGE_DAMAGE)
            if (damage > baseDamage) {
                hit.setCriticalMark()
            }
            return hit
        }

        private fun getRangedStyleBonus(attackStyle: AttackStyle): Double = when (attackStyle) {
            AttackStyle.ACCURATE -> 3.0
            AttackStyle.RAPID -> 0.0
            else -> 1.0
        }
    }

    private object MagicCombat : AccuracyCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, weapon: Weapon?, attackStyle: AttackStyle): Boolean {
            val attackBonus = player.combatDefinitions.bonuses[BonusType.MagicAttack.index].toDouble()
            var attack = getBaseMagicLevel(player)
            attack *= (1.0 + attackBonus / 64.0)

            val (defenceBonus, defenceLevel) = getMagicDefenceValues(target)
            val defence = defenceLevel * (1.0 + defenceBonus / 64.0)

            return calculateHitProbability(attack, defence)
        }

        fun calculateMaxHit(player: Player, spell: Spell): Hit {
            var maxHit = spell.damage.toDouble()
            val magicDamageBonus = player.combatDefinitions.bonuses[BonusType.MagicDamage.index].toDouble()

            maxHit *= (1.0 + magicDamageBonus / 100.0)
            maxHit *= getMagicLevelBoostMultiplier(player)
            val damage = Utils.random(maxHit.toInt())
            val hit = Hit(player, damage, maxHit.toInt(), Hit.HitLook.MAGIC_DAMAGE)
            if (damage > maxHit * 0.95) {
                hit.setCriticalMark()
            }
            return hit
        }

        private fun getMagicLevelBoostMultiplier(player: Player): Double {
            val currentLevel = player.skills.getLevel(Skills.MAGIC)
            val baseLevel = player.skills.getLevelForXp(Skills.MAGIC)
            val levelDiff = currentLevel - baseLevel
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
                    val defenceLevel = defenceBonus //TODO gotta make sure it matches with correct bonuses
                    Pair(defenceBonus, defenceLevel + 8.0)
                }
                else -> Pair(0.0, 0.0)
            }
        }
    }

    fun calculateMeleeAccuracy(player: Player, target: Entity, weapon: Weapon, attackStyle: AttackStyle): Boolean =
        MeleeCombat.calculateAccuracy(player, target, weapon, attackStyle)

    fun calculateRangedAccuracy(player: Player, target: Entity, weapon: Weapon, attackStyle: AttackStyle): Boolean =
        RangedCombat.calculateAccuracy(player, target, weapon, attackStyle)

    fun calculateMagicAccuracy(player: Player, target: Entity): Boolean =
        MagicCombat.calculateAccuracy(player, target, null, AttackStyle.AGGRESSIVE)

    fun calculateMeleeMaxHit(player: Player, attackStyle: AttackStyle, specialMultiplier: Double = 1.0): Hit =
        MeleeCombat.calculateMaxHit(player, attackStyle, specialMultiplier)

    fun calculateRangedMaxHit(player: Player, attackStyle: AttackStyle, specialMultiplier: Double = 1.0): Hit =
        RangedCombat.calculateMaxHit(player, attackStyle, specialMultiplier)

    fun calculateMagicMaxHit(player: Player, spell: Spell): Hit =
        MagicCombat.calculateMaxHit(player, spell)


    private fun calculateHitProbability(attack: Double, defence: Double): Boolean {
        val prob = if (attack < defence) {
            (attack - 1.0) / (defence * 2.0)
        } else {
            1.0 - (defence + 1.0) / (attack * 2.0)
        }.coerceIn(0.0, 1.0)
        return prob >= Utils.getRandomDouble(1.0)
    }

    private fun getBaseAttackLevel(player: Player, attackStyle: AttackStyle): Double {
        var attack = round(player.skills.getLevel(Skills.ATTACK) * player.prayer.attackMultiplier) + 8.0
        attack += when (attackStyle) {
            AttackStyle.ACCURATE -> 3.0
            AttackStyle.CONTROLLED -> 1.0
            else -> 0.0
        }
        return attack
    }

    private fun getBaseRangedLevel(player: Player, attackStyle: AttackStyle): Double {
        var range = round(player.skills.getLevel(Skills.RANGE) * player.prayer.rangedMultiplier) + 8.0
        range += when (attackStyle) {
            AttackStyle.ACCURATE -> 3.0
            AttackStyle.RAPID -> 0.0
            else -> 1.0
        }
        return range
    }

    private fun getBaseMagicLevel(player: Player): Double {
        return round(player.skills.getLevel(Skills.MAGIC) * player.prayer.magicMultiplier) + 8.0
    }

    private fun getBaseDefenceLevel(target: Entity, baseLevel: Double): Double {
        return baseLevel
    }

    private fun getDefenceValues(player: Player, target: Entity, attackStyle: AttackStyle, isRanged: Boolean): Pair<Double, Double> {
        val defenceBonusType = when {
            isRanged -> BonusType.RangeDefence
            else -> BonusType.StabDefence
        }
        val defenceBonus = when (target) {
            is Player -> target.combatDefinitions.bonuses[defenceBonusType.index].toDouble()
            is NPC -> target.bonuses?.get(defenceBonusType.index)?.toDouble() ?: target.combatLevel.toDouble()
            else -> 0.0
        }
        val defenceLevel = when (target) {
            is Player -> {
                val baseDef = target.skills.getLevel(Skills.DEFENCE) * target.prayer.defenceMultiplier
                baseDef + 8.0
            }
            is NPC -> target.combatLevel.toDouble() + 8.0
            else -> 0.0
        }
        return Pair(defenceBonus, defenceLevel)
    }
}
