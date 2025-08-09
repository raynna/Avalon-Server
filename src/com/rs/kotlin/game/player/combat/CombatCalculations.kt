package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.CombatDefinitions
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.magic.Spell
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
        SummoningDefence(10),
        AbsorbMelee(11),
        AbsorbMage(12),
        AbsorbRange(13),
        StregthBonus(14),
        RangedStrBonus(15),
        PrayerBonus(16),
        MagicDamage(17),
    }

    enum class NpcBonusType(val index: Int) {
        AttackLevel(0),
        StrengthLevel(1),
        DefenceLevel(2),
        MagicLevel(3),
        RangeLevel(4),

        StabAttack(5),
        SlashAttack(6),
        CrushAttack(7),
        MagicAttack(8),
        RangeAttack(9),

        StabDefence(10),
        SlashDefence(11),
        CrushDefence(12),
        MagicDefence(13),
        RangeDefence(14),

        StrengthBonus(15),
        AttackBonus(16);
    }


    interface AccuracyCalculator {
        fun calculateAccuracy(player: Player, target: Entity, weapon: Weapon?, attackStyle: AttackStyle, accuracyMultiplier: Double): Boolean
    }

    interface MaxHitCalculator {
        fun calculateMaxHit(player: Player, attackStyle: AttackStyle, specialMultiplier: Double = 1.0): Hit
    }

    private object MeleeCombat : AccuracyCalculator, MaxHitCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, weapon: Weapon?, attackStyle: AttackStyle, accuracyMultiplier: Double): Boolean {
            /*
             * Attack Calculation DONE
             */
            val attackLevel = getBaseAttackLevel(player, attackStyle)
            val bonusType = weapon?.weaponStyle?.getAttackBonusType(attackStyle)
            val attackBonus = player.combatDefinitions.bonuses[bonusType!!.index].toDouble()
            val styleBonus = getAttackStyleBonus(attackStyle)
            val prayerBonus = player.prayer.attackMultiplier
            val voidBonus = 1.0//TODO void bonus
            val specialBonus = 1.0//TODO THINGS LIKE SLAYER HELMET, SALVE AMMY ETC

            val baseAttack = floor(attackLevel * prayerBonus)//DONE
            val effectiveAttackLevel = floor((baseAttack + styleBonus + 8) * voidBonus)//DONE
            val attackRoll = floor((effectiveAttackLevel * (attackBonus + 64)) * specialBonus)//DONE

            /*
            * Defence Calculation
            */

            val defenceLevel = getBaseDefenceLevel(target)
            val defenceBonus = getDefenceBonus(target, weapon, attackStyle, null)

            val defenceRoll = if (target is Player) {
                val styleBonus = getDefenceStyleBonus(target)
                val prayerBonus = target.prayer.defenceMultiplier
                val effectiveDefenceLevel = floor(defenceLevel * prayerBonus) + styleBonus + 8//DONE
                floor(effectiveDefenceLevel * (defenceBonus + 64))//DONE
            } else {
                if (target is NPC) {
                    player.message("[${target.name}] Defence Level: ${defenceLevel}, Defence Bonus: ${defenceBonus}")
                }
                floor((getBaseDefenceLevel(target) + 9) * (defenceBonus + 64))//DONE
            }
            player.message("[Combat Accuracy] - " +
                    "bonusType: ${bonusType.name}, " +
                    "AttackBonus: $attackBonus, " +
                    "DefenceBonus: $defenceBonus, " +
                    "Attack: $attackRoll, " +
                    "Defence: $defenceRoll, " +
                    "")
            return calculateHitProbability(attackRoll, defenceRoll)
        }

        override fun calculateMaxHit(player: Player, attackStyle: AttackStyle, specialMultiplier: Double): Hit {
            val strengthLevel = floor(player.skills.getLevel(Skills.STRENGTH).toDouble())
            val styleBonus = getStrengthStyleBonus(attackStyle)
            val voidBonus = 1.0//TODO VOID BONUS getVoidBonus(player)
            val strengthBonus = player.combatDefinitions.bonuses[BonusType.StregthBonus.index].toDouble()
            val prayerMultiplier = player.prayer.strengthMultiplier

            val baseStrength = floor(strengthLevel * prayerMultiplier)
            val effectiveStrength = floor((baseStrength + styleBonus + 8) * voidBonus)
            val baseDamage = 0.5 + (effectiveStrength * (strengthBonus + 64)) / 64
            val maxHit = floor(baseDamage * specialMultiplier).toInt()

            val damage = Utils.random(maxHit)
            val hit = Hit(player, damage, maxHit, Hit.HitLook.MELEE_DAMAGE)
            if (damage >= floor(baseDamage * 0.90)) {
                hit.setCriticalMark()
            }
            player.message("[Combat Damage] -> " +
                    "BaseDamage: ${baseDamage}, " +
                    "MaxHit: ${maxHit}, " +
                    "Rolled Damage: ${damage}, " +
                    "Critical?: ${damage >= floor(baseDamage * 0.90)}")
            return hit
        }
        private fun getAttackStyleBonus(attackStyle: AttackStyle): Int = when (attackStyle) {
            AttackStyle.ACCURATE -> 3
            AttackStyle.CONTROLLED -> 1
            else -> 0
        }

        private fun getStrengthStyleBonus(attackStyle: AttackStyle): Int = when (attackStyle) {
            AttackStyle.AGGRESSIVE -> 3
            AttackStyle.CONTROLLED -> 1
            else -> 0
        }

        private fun getDefenceStyleBonus(target: Player): Int = when (target.combatDefinitions.attackStyle) {
            CombatDefinitions.DEFENSIVE -> 3
            CombatDefinitions.CONTROLLED -> 1
            else -> 0
        }
    }

    private object RangedCombat : AccuracyCalculator, MaxHitCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, weapon: Weapon?, attackStyle: AttackStyle, accuracyMultiplier: Double): Boolean {
            /*
            * Range attack Calculation DONE
            */
            val attackLevel = getBaseAttackLevel(player, attackStyle)
            val bonusType = weapon?.weaponStyle?.getAttackBonusType(attackStyle)
            val rangeBonus = player.combatDefinitions.bonuses[bonusType!!.index].toDouble()
            val styleBonus = getAttackStyleBonus(attackStyle)
            val prayerBonus = player.prayer.rangedMultiplier
            val voidBonus = 1.0//TODO void bonus
            val specialBonus = 1.0//TODO THINGS LIKE SLAYER HELMET, SALVE AMMY ETC

            val baseRange = floor(attackLevel * prayerBonus)//DONE
            val effectiveRangedAttack = floor((baseRange + styleBonus + 8) * voidBonus)//DONE
            val rangeRoll = floor((effectiveRangedAttack * (rangeBonus + 64)) * specialBonus)//DONE

            /*
            * Range Defence Calculation
            */

            val defenceLevel = getBaseDefenceLevel(target)
            val defenceBonus = getDefenceBonus(target, weapon, attackStyle, null)

            val defenceRoll = if (target is Player) {
                val styleBonus = getDefenceStyleBonus(target)
                val prayerBonus = target.prayer.defenceMultiplier
                val effectiveDefenceLevel = floor(defenceLevel * prayerBonus) + styleBonus + 8//DONE
                floor(effectiveDefenceLevel * (defenceBonus + 64))//DONE
            } else {
                floor((getBaseDefenceLevel(target) + 9) * (defenceBonus + 64))//DONE
            }
            player.message("[Combat Accuracy] - " +
                    "bonusType: ${bonusType.name}, " +
                    "AttackBonus: $rangeBonus, " +
                    "DefenceBonus: $defenceBonus, " +
                    "Attack: $rangeRoll, " +
                    "Defence: $defenceRoll, " +
                    "")
            return calculateHitProbability(rangeRoll, defenceRoll)
        }

        override fun calculateMaxHit(player: Player, attackStyle: AttackStyle, specialMultiplier: Double): Hit {
            val rangedLvl = player.skills.getLevel(Skills.RANGE).toDouble()
            val prayerBonus = player.prayer.rangedMultiplier
            val styleBonus = getStrengthStyleBonus(attackStyle)
            val voidBonus = 1.0//TODO getVoidModifier(player)

            val baseStrength = floor(rangedLvl * prayerBonus)
            val effectiveStrength = floor((baseStrength + styleBonus + 8) * voidBonus)
            val strengthBonus = player.combatDefinitions.bonuses[BonusType.RangedStrBonus.index].toDouble()

            val baseDamage = 0.5 + ((effectiveStrength) * (strengthBonus + 64.0)) / 64.0

            val maxHit = floor(baseDamage * specialMultiplier).toInt()

            val damage = Utils.random(maxHit)
            val hit = Hit(player, damage, maxHit, Hit.HitLook.RANGE_DAMAGE)
            if (damage > baseDamage) {
                hit.setCriticalMark()
            }
            player.message("Ranged Damage Calculation -> " +
                    "BaseDamage: ${baseDamage}, " +
                    "AttackStyle: ${attackStyle.name}, " +
                    "StyleBonus: ${styleBonus}, " +
                    "MaxHit: ${maxHit}, " +
                    "Rolled Damage: ${damage}, " +
                    "Critical?: ${damage >= floor(baseDamage * 0.90)}")
            return hit
        }

        private fun getAttackStyleBonus(attackStyle: AttackStyle): Int = when (attackStyle) {
            AttackStyle.ACCURATE -> 3
            else -> 0
        }

        private fun getStrengthStyleBonus(attackStyle: AttackStyle): Int = when (attackStyle) {
            AttackStyle.ACCURATE -> 3
            else -> 0
        }

        private fun getDefenceStyleBonus(target: Player): Int = when (target.combatDefinitions.attackStyle) {
            CombatDefinitions.DEFENSIVE -> 3
            CombatDefinitions.CONTROLLED -> 1
            else -> 0
        }
    }

    private object MagicCombat : AccuracyCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, weapon: Weapon?, attackStyle: AttackStyle, accuracyMultiplier: Double): Boolean {
            val attackBonus = player.combatDefinitions.bonuses[BonusType.MagicAttack.index].toDouble()
            var attack = getBaseMagicLevel(player)

            //TODO SPECIAL ATTACK ACCURACY UNUSED FOR MAGIC ATM, no weapon has special attack pre-eoc

            attack *= (1.0 + attackBonus / 64.0)

            val (defenceBonus, defenceLevel) = getMagicDefenceValues(target)
            val defence = defenceLevel * (1.0 + defenceBonus / 64.0)

            return calculateHitProbability(attack, defence)
        }

        fun calculateMaxHit(player: Player, target: Entity, spell: Spell): Hit {
            var baseDamage = spell.damage.toDouble()

            val magicDamageBonus = player.combatDefinitions.bonuses[BonusType.MagicDamage.index].toDouble()
            val magicStrengthMultiplier = 1.0 + magicDamageBonus / 100.0

            var levelMultiplier = getMagicLevelMultiplier(player)
            if (target is NPC) {//just to make sure magic is a bit stronger for monsters so magic is a viable style in pvm
                levelMultiplier *= player.prayer.magicMultiplier
            }
            val maxHit = baseDamage * magicStrengthMultiplier * levelMultiplier
            val damage = Utils.random(maxHit.toInt())
            val hit = Hit(player, damage, maxHit.toInt(), Hit.HitLook.MAGIC_DAMAGE)

            if (damage > maxHit * 0.90) {
                hit.setCriticalMark()
            }
            return hit
        }

        private fun getMagicLevelMultiplier(player: Player): Double {
            val currentLevel = player.skills.getLevel(Skills.MAGIC).toDouble()
            val baseLevel = player.skills.getLevelForXp(Skills.MAGIC).toDouble()
            //custom scaling, to make magic more relyant on magic level for all levels, max hit is still same 490 as previous
            val normalScaling = 1.0 + ((baseLevel - 1) * 0.00205)
            val boostedScaling = 1.0 + ((currentLevel - baseLevel).coerceAtLeast(0.0) * 0.0015)
            return normalScaling * boostedScaling
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
                    val defenceBonus = target.bonuses?.get(NpcBonusType.MagicDefence.index)?.toDouble() ?: target.combatLevel.toDouble()
                    val defenceLevel = defenceBonus //TODO gotta make sure it matches with correct bonuses
                    Pair(defenceBonus, defenceLevel + 8.0)
                }
                else -> Pair(0.0, 0.0)
            }
        }
    }

    fun calculateMeleeAccuracy(player: Player, target: Entity, weapon: Weapon, attackStyle: AttackStyle = AttackStyle.ACCURATE, accuracyMultiplier: Double): Boolean =
        MeleeCombat.calculateAccuracy(player, target, weapon, attackStyle, accuracyMultiplier)

    fun calculateRangedAccuracy(player: Player, target: Entity, weapon: Weapon, attackStyle: AttackStyle = AttackStyle.ACCURATE, accuracyMultiplier: Double): Boolean =
        RangedCombat.calculateAccuracy(player, target, weapon, attackStyle, accuracyMultiplier)

    fun calculateMagicAccuracy(player: Player, target: Entity): Boolean =
        MagicCombat.calculateAccuracy(player, target, null, AttackStyle.ACCURATE, 1.0)

    fun calculateMeleeMaxHit(player: Player, attackStyle: AttackStyle, specialMultiplier: Double = 1.0): Hit =
        MeleeCombat.calculateMaxHit(player, attackStyle, specialMultiplier)

    fun calculateRangedMaxHit(player: Player, attackStyle: AttackStyle, specialMultiplier: Double = 1.0): Hit =
        RangedCombat.calculateMaxHit(player, attackStyle, specialMultiplier)

    fun calculateMagicMaxHit(player: Player, target: Entity, spell: Spell): Hit =
        MagicCombat.calculateMaxHit(player, target, spell)


    private fun calculateHitProbability(attack: Double, defence: Double): Boolean {
        val random = Utils.getRandomDouble(100.0)

        val prob: Double = if (attack > defence) {
            1 - (defence + 2) / (2 * (attack + 1))
        } else {
            attack / ( 2 * (defence + 1))
        }
        return prob > random / 100;
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
        return round(player.skills.getLevel(Skills.MAGIC) * player.prayer.magicMultiplier) + 8
    }

    private fun getBaseDefenceLevel(target: Entity): Int {
        val level = (when (target) {
            is NPC -> target.bonuses[CombatDefinitions.NPC_DEFENCE_LEVEL]
            is Player -> target.skills.getLevel(Skills.DEFENCE)
            else -> 0
        })
        return level
    }

    private fun getDefenceBonus(target: Entity, weapon: Weapon, attackStyle: AttackStyle, spell: Spell?): Double {
        val bonusStyle = weapon.weaponStyle.getAttackBonusType(attackStyle)
        val npcDefenceBonus = when (bonusStyle){
            AttackBonusType.STAB -> NpcBonusType.StabDefence
            AttackBonusType.SLASH -> NpcBonusType.SlashDefence
            AttackBonusType.CRUSH -> NpcBonusType.CrushDefence
            else -> if (spell != null) NpcBonusType.MagicDefence else NpcBonusType.RangeDefence
        }
        val playerDefenceBonus = when (bonusStyle){
            AttackBonusType.STAB -> BonusType.StabDefence
            AttackBonusType.SLASH -> BonusType.SlashDefence
            AttackBonusType.CRUSH -> BonusType.CrushDefence
            else -> if (spell != null) BonusType.MagicDefence else BonusType.RangeDefence
        }
        val defenceBonus = when (target) {
            is Player -> target.combatDefinitions.bonuses[playerDefenceBonus.index].toDouble()
            is NPC -> target.bonuses?.get(npcDefenceBonus.index)?.toDouble() ?: target.combatLevel.toDouble()
            else -> 0.0
        }
        return defenceBonus
    }
}
