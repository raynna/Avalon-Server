package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.CombatDefinitions
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.damage.DamageMultipliers
import com.rs.kotlin.game.player.combat.magic.Spell
import kotlin.math.floor

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
        fun calculateAccuracy(player: Player, target: Entity, accuracyMultiplier: Double): Boolean
    }

    interface MaxHitCalculator {
        fun calculateMaxHit(player: Player, target: Entity, specialMultiplier: Double = 1.0): Hit
    }

    private object MeleeCombat : AccuracyCalculator, MaxHitCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, accuracyMultiplier: Double): Boolean {
            /*
             * Attack Calculation DONE
             */
            val baseAttack = getBaseAttackLevel(player)
            val bonusType = getAttackBonusIndex(player)
            val attackBonus = player.combatDefinitions.bonuses[bonusType].toDouble()
            val styleBonus = getAttackStyleBonus(player)
            val voidBonus = 1.0//TODO void bonus
            val specialBonus = DamageMultipliers.getMultiplier(player,  target, DamageMultipliers.Style.MELEE)//TODO THINGS LIKE SLAYER HELMET, SALVE AMMY ETC

            val effectiveAttackLevel = floor((baseAttack + styleBonus + 8) * voidBonus)//DONE
            val attackRoll = floor((effectiveAttackLevel * (attackBonus + 64)) * specialBonus)//DONE

            /*
            * Defence Calculation
            */

            val defenceLevel = getDefenceLevel(target)
            val defenceBonus = getDefenceBonus(player, target, null)
            var targetStyleBonus: Int = 0
            var baseDefenceLevel: Int = 0
            var effectiveDefenceLevel: Int = 0
            val defenceRoll = if (target is Player) {
                targetStyleBonus = getDefenceStyleBonus(target)
                val prayerBonus = target.prayer.defenceMultiplier
                baseDefenceLevel = floor(defenceLevel * prayerBonus).toInt()
                effectiveDefenceLevel = floor((baseDefenceLevel + targetStyleBonus + 8).toDouble()).toInt()//DONE
                floor((effectiveDefenceLevel * (defenceBonus + 64)).toDouble())//DONE
            } else {
                floor(((getDefenceLevel(target) + 9) * (defenceBonus + 64)).toDouble())
            }
            player.message("[Combat Accuracy][Attack] - " +
                    "attackLevel: ${baseAttack}, " +
                    "attackBonus: $attackBonus, " +
                    "styleBonus: $styleBonus, " +
                    "effectiveAttack: $effectiveAttackLevel, " +
                    "attackRoll: $attackRoll, " +
                    "")
            player.message("[Combat Accuracy][Defence] - Target: "
                    + (if (target is NPC) "${target.name}, " else if (target is Player) "${target.username}, " else "") +
                    "defenceLevel: $defenceLevel, " +
                    "defenceBonus: $defenceBonus, " +
                    "targetStyleBonus: $targetStyleBonus, " +
                    "defenceRoll: $defenceRoll, " +
                    "")
            return calculateHitProbability(attackRoll, defenceRoll)
        }

        override fun calculateMaxHit(player: Player, target: Entity, specialMultiplier: Double): Hit {
            val styleBonus = getStrengthStyleBonus(player)
            val voidBonus = 1.0//TODO VOID BONUS getVoidBonus(player)
            val specialBonus = DamageMultipliers.getMultiplier(player, target, DamageMultipliers.Style.MELEE)
            val strengthBonus = player.combatDefinitions.bonuses[BonusType.StregthBonus.index].toDouble()

            val baseStrengthLevel = getBaseStrengthLevel(player)
            val effectiveStrength = floor((baseStrengthLevel + styleBonus + 8) * voidBonus)
            val baseDamage = 0.5 + ((effectiveStrength * (strengthBonus + 64)) / 64) * specialBonus
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
    }

    private object RangedCombat : AccuracyCalculator, MaxHitCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, accuracyMultiplier: Double): Boolean {
            /*
            * Range attack Calculation DONE
            */
            val baseRangeLevel = getBaseRangedLevel(player)
            val bonusType = getAttackBonusIndex(player)
            val rangeBonus = player.combatDefinitions.bonuses[bonusType]
            val styleBonus = getAttackStyleBonus(player)
            val voidBonus = 1.0//TODO void bonus
            val specialBonus = DamageMultipliers.getMultiplier(player, target, DamageMultipliers.Style.RANGE)

            val effectiveRangedAttack = floor((baseRangeLevel + styleBonus + 8) * voidBonus)//DONE
            val rangeRoll = floor((effectiveRangedAttack * (rangeBonus + 64)) * specialBonus)//DONE

            /*
            * Range Defence Calculation
            */

            val defenceLevel = getDefenceLevel(target)
            val defenceBonus = getDefenceBonus(player, target, null)

            val defenceRoll = if (target is Player) {
                val styleBonus = getDefenceStyleBonus(target)
                val prayerBonus = target.prayer.defenceMultiplier
                val effectiveDefenceLevel = floor(defenceLevel * prayerBonus) + styleBonus + 8//DONE
                floor(effectiveDefenceLevel * (defenceBonus + 64))//DONE
            } else {
                floor(((getDefenceLevel(target) + 9) * (defenceBonus + 64)).toDouble())//DONE
            }
            player.message("[Combat Accuracy] - " +
                    "bonusType: ${bonusType}, " +
                    "AttackBonus: $rangeBonus, " +
                    "DefenceBonus: $defenceBonus, " +
                    "Attack: $rangeRoll, " +
                    "Defence: $defenceRoll, " +
                    "")
            return calculateHitProbability(rangeRoll, defenceRoll)
        }

        override fun calculateMaxHit(player: Player, target: Entity, specialMultiplier: Double): Hit {
            val rangedLvl = player.skills.getLevel(Skills.RANGE).toDouble()
            val prayerBonus = player.prayer.rangedMultiplier
            val styleBonus = getStrengthStyleBonus(player)
            val voidBonus = 1.0//TODO getVoidModifier(player)
            val specialBonus = DamageMultipliers.getMultiplier(player, target, DamageMultipliers.Style.MELEE)

            val baseStrength = floor(rangedLvl * prayerBonus)
            val effectiveStrength = floor((baseStrength + styleBonus + 8) * voidBonus)
            val strengthBonus = player.combatDefinitions.bonuses[BonusType.RangedStrBonus.index].toDouble()

            val baseDamage = 0.5 + (((effectiveStrength) * (strengthBonus + 64.0)) / 64.0) * specialBonus

            val maxHit = floor(baseDamage * specialMultiplier).toInt()

            val damage = Utils.random(maxHit)
            val hit = Hit(player, damage, maxHit, Hit.HitLook.RANGE_DAMAGE)
            if (damage > baseDamage) {
                hit.setCriticalMark()
            }
            player.message("Ranged Damage Calculation -> " +
                    "BaseDamage: ${baseDamage}, " +
                    "StyleBonus: ${styleBonus}, " +
                    "MaxHit: ${maxHit}, " +
                    "Rolled Damage: ${damage}, " +
                    "Critical?: ${damage >= floor(baseDamage * 0.90)}")
            return hit
        }
    }

    private object MagicCombat : AccuracyCalculator {
        override fun calculateAccuracy(player: Player, target: Entity, accuracyMultiplier: Double): Boolean {
            val bonusType = getAttackBonusIndex(player)
            val magicBonus = player.combatDefinitions.bonuses[bonusType]
            val specialBonus = DamageMultipliers.getMultiplier(player, target, DamageMultipliers.Style.MAGIC)
            var effectiveMagicLevel = getBaseMagicLevel(player)
            val attack = effectiveMagicLevel * (magicBonus + 64) * specialBonus

            val (defenceBonus, effectiveDefenceLevel) = getMagicDefenceValues(target)
            val defence = effectiveDefenceLevel * (defenceBonus + 64).toDouble()

            return calculateHitProbability(attack, defence)
        }

        fun calculateMaxHit(player: Player, target: Entity, spell: Spell): Hit {
            val baseDamage = spell.damage
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

        private fun getMagicDefenceValues(target: Entity): Pair<Int, Int> {
            return when (target) {
                is Player -> {
                    val defenceBonus = target.combatDefinitions.bonuses[BonusType.MagicDefence.index]
                    val defence = floor(floor(
                        (target.skills.getLevel(Skills.MAGIC) * 0.7 +
                                target.skills.getLevel(Skills.DEFENCE) * 0.3) *
                                target.prayer.magicMultiplier
                    )).toInt()//this should be correct
                    Pair(defenceBonus, defence + 8)
                }
                is NPC -> {
                    val defenceBonus = target.bonuses?.get(NpcBonusType.MagicDefence.index) ?: (target.combatLevel / 3)
                    val defenceLevel = target.bonuses?.get(NpcBonusType.DefenceLevel.index) ?: target.combatLevel
                    Pair(defenceBonus, defenceLevel + 9)
                }
                else -> Pair(0, 0)
            }
        }
    }

    fun calculateMeleeAccuracy(player: Player, target: Entity, accuracyMultiplier: Double): Boolean =
        MeleeCombat.calculateAccuracy(player, target, accuracyMultiplier)

    fun calculateRangedAccuracy(player: Player, target: Entity, accuracyMultiplier: Double): Boolean =
        RangedCombat.calculateAccuracy(player, target, accuracyMultiplier)

    fun calculateMagicAccuracy(player: Player, target: Entity): Boolean =
        MagicCombat.calculateAccuracy(player, target, 1.0)

    fun calculateMeleeMaxHit(player: Player, target: Entity, specialMultiplier: Double = 1.0): Hit =
        MeleeCombat.calculateMaxHit(player, target, specialMultiplier)

    fun calculateRangedMaxHit(player: Player, target: Entity, specialMultiplier: Double = 1.0): Hit =
        RangedCombat.calculateMaxHit(player, target, specialMultiplier)

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


    private fun getBaseStrengthLevel(player: Player): Int {
        val strength = floor(player.skills.getLevel(Skills.STRENGTH) * player.prayer.strengthMultiplier)
        return strength.toInt()
    }

    private fun getBaseAttackLevel(player: Player): Int {
        val attack = floor(player.skills.getLevel(Skills.ATTACK) * player.prayer.attackMultiplier)
        return attack.toInt()
    }

    private fun getBaseRangedLevel(player: Player): Int {
        val range = floor(player.skills.getLevel(Skills.RANGE) * player.prayer.rangedMultiplier)
        return range.toInt()
    }

    private fun getBaseMagicLevel(player: Player): Int {
        val magic = floor(player.skills.getLevel(Skills.MAGIC) * player.prayer.magicMultiplier)
        return magic.toInt()
    }

    private fun getDefenceLevel(target: Entity): Int {
        val level = (when (target) {
            is NPC -> {
                var defenceLevel = target.bonuses[NpcBonusType.DefenceLevel.index]
                if (defenceLevel <= 0) {
                    defenceLevel = target.combatLevel
                }
                defenceLevel
            }
            is Player -> target.skills.getLevel(Skills.DEFENCE)
            else -> 0
        })
        return level
    }

    private fun getAttackStyleBonus(player: Player): Int {
        val weapon = Weapon.getWeapon(player.equipment.weaponId)
        val bonus = when (weapon.weaponStyle.styleSet.styleAt(player.combatDefinitions.attackStyle)) {
            AttackStyle.ACCURATE_RANGE -> 3
            AttackStyle.ACCURATE -> 3
            AttackStyle.CONTROLLED -> 1
            else -> 0
        }
        return bonus
    }

    private fun getStrengthStyleBonus(player: Player): Int {
        val weapon = Weapon.getWeapon(player.equipment.weaponId)
        val bonus = when (weapon.weaponStyle.styleSet.styleAt(player.combatDefinitions.attackStyle)) {
            AttackStyle.ACCURATE_RANGE -> 3
            AttackStyle.ACCURATE -> 3
            AttackStyle.AGGRESSIVE -> 3
            AttackStyle.CONTROLLED -> 1
            else -> 0
        }
        return bonus
    }


    private fun getDefenceStyleBonus(target: Player): Int {
        val targetWeapon = Weapon.getWeapon(target.equipment.weaponId)
        val bonus = when (targetWeapon.weaponStyle.styleSet.styleAt(target.combatDefinitions.attackStyle)) {
            AttackStyle.DEFENSIVE -> 3
            AttackStyle.LONGRANGE -> 3
            AttackStyle.CONTROLLED -> 1
            else -> 0
        }
        return bonus
    }

    private fun getAttackBonusIndex(player: Player): Int {
        val targetWeapon = Weapon.getWeapon(player.equipment.weaponId)
        val bonusStyle = targetWeapon.weaponStyle.styleSet.bonusAt(player.combatDefinitions.attackStyle)
        val bonus = when (bonusStyle) {
            AttackBonusType.STAB -> BonusType.StabAttack
            AttackBonusType.SLASH -> BonusType.SlashAttack
            AttackBonusType.CRUSH -> BonusType.CrushAttack
            AttackBonusType.RANGE -> BonusType.RangeAttack
            else -> BonusType.MagicAttack
        }
        return bonus.index
    }

    private fun getDefenceBonus(player: Player, target: Entity, spell: Spell?): Int {
            val targetWeapon = Weapon.getWeapon(player.equipment.weaponId)
            val bonusStyle = targetWeapon.weaponStyle.styleSet.bonusAt(player.combatDefinitions.attackStyle)
            val npcDefenceBonus = when (bonusStyle) {
                AttackBonusType.STAB -> NpcBonusType.StabDefence
                AttackBonusType.SLASH -> NpcBonusType.SlashDefence
                AttackBonusType.CRUSH -> NpcBonusType.CrushDefence
                else -> if (spell != null) NpcBonusType.MagicDefence else NpcBonusType.RangeDefence
            }
            val playerDefenceBonus = when (bonusStyle) {
                AttackBonusType.STAB -> BonusType.StabDefence
                AttackBonusType.SLASH -> BonusType.SlashDefence
                AttackBonusType.CRUSH -> BonusType.CrushDefence
                else -> if (spell != null) BonusType.MagicDefence else BonusType.RangeDefence
            }
            val defenceBonus = when (target) {
                is Player -> target.combatDefinitions.bonuses[playerDefenceBonus.index]
                is NPC -> {
                    var defenceBonus = target.bonuses[npcDefenceBonus.index]
                    if (defenceBonus <= 0) {
                        defenceBonus = target.combatLevel / 3
                    }
                    defenceBonus
                }
                else -> 0
            }
            return defenceBonus
    }
}
