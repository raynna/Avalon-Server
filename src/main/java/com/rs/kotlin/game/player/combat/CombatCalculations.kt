package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.damage.CombatMultipliers
import com.rs.kotlin.game.player.combat.damage.MaxHit
import com.rs.kotlin.game.player.combat.magic.MagicStyle
import com.rs.kotlin.game.player.combat.magic.Spell
import com.rs.kotlin.game.player.combat.magic.Spellbook
import com.rs.kotlin.game.player.combat.melee.MeleeStyle
import com.rs.kotlin.game.player.combat.range.RangedStyle
import com.rs.kotlin.game.player.equipment.BonusType
import com.rs.kotlin.game.player.equipment.EquipmentSets
import com.rs.kotlin.game.player.equipment.EquipmentSets.getDharokMultiplier
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.floor

object CombatCalculations {

    interface AccuracyCalculator {
        fun getHitChance(player: Player, target: Entity, accuracyMultiplier: Double): Double
    }

    interface MaxHitCalculator {
        fun calculateMaxHit(player: Player, target: Entity, specialMultiplier: Double = 1.0): MaxHit
    }

    private fun computeHitChance(attack: Int, defence: Int): Double {
        return if (attack > defence) {
            1.0 - (defence + 2.0) / (2.0 * (attack + 1.0))
        } else {
            attack.toDouble() / (2.0 * (defence + 1.0))
        }
    }

    private object MeleeCombat : AccuracyCalculator, MaxHitCalculator {
        override fun getHitChance(player: Player, target: Entity, accuracyMultiplier: Double): Double {
            if (target is NPC && target.name.contains("dummy", ignoreCase = true))
                return 1.0
            /*
             * Attack Calculation DONE
             */
            val bonusType = getAttackBonusIndex(player)
            val attackBonus = player.combatDefinitions.bonuses[bonusType].toDouble()
            val styleBonus = getAttackStyleBonus(player)
            val equipmentSet = EquipmentSets.getSet(player)
            val voidBonus = EquipmentSets.getAccuracyMultiplier(player, equipmentSet, CombatMultipliers.Style.MELEE)
            val multipliers = CombatMultipliers.getMultipliers(player, target, CombatMultipliers.Style.MELEE)
            val baseAttack = getBaseAttackLevel(player)

            val effectiveAttack = floor((baseAttack + styleBonus + 8) * voidBonus)

            val attackRoll = effectiveAttack * (attackBonus + 64) * multipliers.accuracy * accuracyMultiplier
            /*
            * Defence Calculation
            */

            val defenceLevel = getDefenceLevel(target)
            val defenceBonus = getDefenceBonus(player, target, null)
            var targetStyleBonus = 0
            val defenceRoll = if (target is Player) {
                targetStyleBonus = getDefenceStyleBonus(target)
                val prayerBonus = target.prayer.defenceMultiplier

                var effectiveDefence = target.skills.getLevel(Skills.DEFENCE) * prayerBonus
                effectiveDefence += targetStyleBonus + 8

                val defenceRoll = effectiveDefence * (defenceBonus + 64)
                defenceRoll
            } else {
                (getDefenceLevel(target) + 9) * (defenceBonus + 64)
            }
            if (player.developerMode) {
                player.message(
                    "[Combat Accuracy][Attack] - " +
                            "attackLevel: ${baseAttack}, " +
                            "attackBonus: $attackBonus, " +
                            "styleBonus: $styleBonus, " +
                            "effectiveAttack: $effectiveAttack, " +
                            "attackRoll: $attackRoll, " +
                            ""
                )
                player.message(
                    "[Combat Accuracy][Defence] - Target: "
                            + (if (target is NPC) "${target.name}, " else if (target is Player) "${target.username}, " else "") +
                            "defenceLevel: $defenceLevel, " +
                            "defenceBonus: $defenceBonus, " +
                            "targetStyleBonus: $targetStyleBonus, " +
                            "defenceRoll: $defenceRoll, " +
                            ""
                )
            }
            return computeHitChance(attackRoll.toInt(), defenceRoll.toInt())
        }

        override fun calculateMaxHit(player: Player, target: Entity, specialMultiplier: Double): MaxHit {

            val styleBonus = getStrengthStyleBonus(player)

            val equipmentSet = EquipmentSets.getSet(player)
            val dharokMultiplier = getDharokMultiplier(player)
            val void = EquipmentSets.getDamageMultiplier(player, equipmentSet, CombatMultipliers.Style.MELEE)
            val multipliers = CombatMultipliers.getMultipliers(player, target, CombatMultipliers.Style.MELEE)

            val strengthBonus = player.combatDefinitions.bonuses[BonusType.StregthBonus.index].toDouble()
            val baseStrengthLevel = getBaseStrengthLevel(player)

            val effectiveStrength =
                floor(((baseStrengthLevel + styleBonus + 8) * void) * dharokMultiplier)

            val baseDamage = 0.5 + ((effectiveStrength * (strengthBonus + 640)) / 640) * multipliers.damage
            val baseMaxHit = baseDamage.toInt()
            var maxHit = (baseMaxHit * specialMultiplier).toInt()

            var finalDamage = Utils.random(maxHit)
            finalDamage = ceilToNextTen(player, finalDamage)
            var finalMaxHit = (baseMaxHit * specialMultiplier).toInt()
            finalMaxHit = ceilToNextTen(player, finalMaxHit)
            if (target is NPC && target.id == 4474) {
                finalDamage = finalMaxHit
            }
            if (target is NPC) {
                if (target.capDamage != -1 && finalDamage > target.capDamage) {
                    maxHit = target.capDamage
                }
            }
            if (player.developerMode) {
                player.message(
                    "[Combat Damage] -> " +
                            "BaseMax: $baseMaxHit, " +
                            "MaxHit: $maxHit, "
                )
            }

            return MaxHit(
                base = baseMaxHit,
                max = maxHit
            )
        }
    }

    private object RangedCombat : AccuracyCalculator, MaxHitCalculator {
        override fun getHitChance(player: Player, target: Entity, accuracyMultiplier: Double): Double {
            if (target is NPC && target.name.contains("dummy", ignoreCase = true))
                return 1.0
            /*
            * Range attack Calculation DONE
            */
            val bonusType = getAttackBonusIndex(player)
            val rangeBonus = player.combatDefinitions.bonuses[bonusType]
            val styleBonus = getAttackStyleBonus(player)
            val equipmentSet = EquipmentSets.getSet(player)
            val void = EquipmentSets.getAccuracyMultiplier(player, equipmentSet, CombatMultipliers.Style.RANGE)
            val multipliers = CombatMultipliers.getMultipliers(player, target, CombatMultipliers.Style.RANGE)
            val (twistedBowAccuracy, _, _) = getTwistedBowBoost(player, target)

            var effectiveAttack = player.skills.getLevel(Skills.RANGE) * player.prayer.rangedMultiplier
            effectiveAttack += styleBonus + 8
            effectiveAttack *= void * twistedBowAccuracy
            val attackRoll = effectiveAttack * (rangeBonus + 64) * multipliers.accuracy * accuracyMultiplier
            /*
            * Range Defence Calculation
            */
            var defenceBonus = 0
            if (target is Player) {
                defenceBonus = target.combatDefinitions.bonuses[BonusType.RangeDefence.index]
            }
            if (target is NPC) {
                defenceBonus = target.combatData.rangedDefence.standard
            }

            val defenceRoll = if (target is Player) {
                val styleBonus = getDefenceStyleBonus(target)
                val prayerBonus = target.prayer.defenceMultiplier

                var effectiveDefence = target.skills.getLevel(Skills.DEFENCE) * prayerBonus
                effectiveDefence += styleBonus + 8

                val defenceRoll = effectiveDefence * (defenceBonus + 64)
                defenceRoll
            } else {
                ((getDefenceLevel(target) + 9) * (defenceBonus + 64))//DONE
            }
            if (player.developerMode) {
                player.message(
                    "[Combat Accuracy] - " +
                            "bonusType: ${bonusType}, " +
                            "AttackBonus: $rangeBonus, " +
                            "DefenceBonus: $defenceBonus, " +
                            "Attack: $attackRoll, " +
                            "Defence: $defenceRoll, " +
                            ""
                )
            }
            return computeHitChance(attackRoll.toInt(), defenceRoll.toInt())
        }

        override fun calculateMaxHit(player: Player, target: Entity, specialMultiplier: Double): MaxHit {

            val rangedLvl = player.skills.getLevel(Skills.RANGE).toDouble()
            val prayerBonus = player.prayer.rangedMultiplier
            val styleBonus = getStrengthStyleBonus(player)

            val equipmentSet = EquipmentSets.getSet(player)
            val void = EquipmentSets.getDamageMultiplier(player, equipmentSet, CombatMultipliers.Style.RANGE)
            val multipliers = CombatMultipliers.getMultipliers(player, target, CombatMultipliers.Style.RANGE)

            val (_, twistedBowMultiplier, twistedBowMax) = getTwistedBowBoost(player, target)

            val baseStrength = floor(rangedLvl * prayerBonus)
            val effectiveStrength = floor((baseStrength + styleBonus + 8) * void * twistedBowMultiplier)

            val strengthBonus = player.combatDefinitions.bonuses[BonusType.RangedStrBonus.index].toDouble()

            val baseDamage = 0.5 + (effectiveStrength * (strengthBonus + 640) / 640) * multipliers.damage

            val baseMaxHit = baseDamage.toInt()

            var maxHit = (baseMaxHit * specialMultiplier).toInt()
            maxHit = ceilToNextTen(player, maxHit)
            if (twistedBowMax in 1..<maxHit) {
                maxHit = twistedBowMax
            }
            if (target is NPC) {
                if (target.capDamage != -1 && maxHit > target.capDamage) {
                    maxHit = target.capDamage
                }
            }

            if (player.developerMode) {
                player.message(
                    "Ranged Damage Calculation -> " +
                            "BaseMax: $baseMaxHit, " +
                            "MaxHit: $maxHit, "
                )
            }

            return MaxHit(
                baseMaxHit,
                maxHit,
            )
        }


        fun getTwistedBowBoost(player: Player, target: Entity): Triple<Double, Double, Int> {
            if (target !is NPC) return Triple(1.0, 1.0, 0)

            val weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON.toInt())
            val isHexhunter = weapon.isAnyOf("item.hexhunter_bow", "item.hexhunter_bow_b")
            val isTwistedBow = weapon.isAnyOf("item.twisted_bow")

            if (!isHexhunter && !isTwistedBow) return Triple(1.0, 1.0, 0)
            val magicLevel = target.combatData.magicLevel
            val magicAttack = target.combatData.magicBonus

            val effectiveMagic = (magicLevel + magicAttack).toDouble() / 2

            val maxMagic = 255.0
            var damageMultiplier = (1.0 + (effectiveMagic / maxMagic) * (2.5 - 1.0)).coerceIn(1.0, 2.5)

            var accuracyMultiplier = (1.0 + (effectiveMagic / maxMagic) * (1.4 - 1.0)).coerceIn(1.0, 1.4)
            if (isHexhunter) {
                damageMultiplier *= 2
                accuracyMultiplier *= 2
            }
            val damageCap = 810
            return Triple(accuracyMultiplier, damageMultiplier, damageCap)
        }
    }


    private object MagicCombat : AccuracyCalculator {

       override fun getHitChance(player: Player, target: Entity, accuracyMultiplier: Double): Double {
           if (target is NPC && target.name.contains("dummy", ignoreCase = true))
               return 1.0
           val magicBonus = player.combatDefinitions.bonuses[BonusType.MagicAttack.index]
           val equipmentSet = EquipmentSets.getSet(player)
           val void = EquipmentSets.getAccuracyMultiplier(player, equipmentSet, CombatMultipliers.Style.MAGIC)
           val multipliers = CombatMultipliers.getMultipliers(player, target, CombatMultipliers.Style.MAGIC)

           var effectiveMagic = player.skills.getLevel(Skills.MAGIC) * player.prayer.magicMultiplier
           effectiveMagic += 9//TODO STYLE ACCURACY FOR POLYPORE
           effectiveMagic *= void
           val attackRoll = effectiveMagic * (magicBonus + 64) * multipliers.accuracy * accuracyMultiplier

           val (defenceBonus, effectiveDefenceLevel) = getEffectiveMagicDefence(target)
           val defenceRoll = effectiveDefenceLevel * (defenceBonus + 64)
           return computeHitChance(attackRoll.toInt(), defenceRoll)
        }

        fun calculateMaxHit(
            player: Player,
            target: Entity,
            baseDamage: Int = -1,
            spellId: Int = -1,
            specialMultiplier: Double = 1.0
        ): MaxHit {

            val spell = Spellbook.getSpellById(player, spellId)

            val min = when {
                spellId == 99 -> 160
                else -> 0
            }

            val base = when {
                spell != null && spell.chargeBoost -> {
                    val b = spell.damage
                    if (player.tickManager.isActive(TickManager.TickKeys.CHARGE_SPELL)) b + 100 else b
                }
                spellId == 1000 -> (5 * player.skills.getLevel(Skills.MAGIC)) - 180
                spellId == 56 -> {
                    val magicLevel = player.skills.getLevelForXp(Skills.MAGIC)
                    (floor(magicLevel * 0.10).toInt() + 10) * 10
                }
                spellId == 99 -> {
                    val magicLevel = player.skills.getLevelForXp(Skills.MAGIC)
                    val baseVal = 160 + (magicLevel - 77) * 4
                    val boost = (magicLevel - 77) * 4
                    baseVal + boost
                }
                baseDamage > 0 -> baseDamage
                spell != null && spell.damage > 0 -> spell.damage
                else -> 0
            }

            val magicDamageBonus = player.combatDefinitions.bonuses[BonusType.MagicDamage.index].toDouble()
            val magicStrengthMultiplier = 1.0 + magicDamageBonus / 100.0

            val equipmentSet = EquipmentSets.getSet(player)
            val voidDamage = EquipmentSets.getDamageMultiplier(player, equipmentSet, CombatMultipliers.Style.MAGIC)
            val multipliers = CombatMultipliers.getMultipliers(player, target, CombatMultipliers.Style.MAGIC)
            var levelMultiplier = getMagicLevelMultiplier(player)
            if (target is NPC) {
                levelMultiplier *= player.prayer.magicMultiplier
            }

            val baseMaxHit = floor(base * magicStrengthMultiplier * levelMultiplier * voidDamage * multipliers.damage).toInt()

            var maxHit = (baseMaxHit * specialMultiplier).toInt()
            maxHit = ceilToNextTen(player, maxHit)
            if (target is NPC) {
                if (target.capDamage != -1 && maxHit > target.capDamage) {
                    maxHit = target.capDamage
                }
            }

            return MaxHit(
                baseMaxHit,
                maxHit,
                min
            )
        }


        private fun getMagicLevelMultiplier(player: Player): Double {
            val currentLevel = player.skills.getLevel(Skills.MAGIC).toDouble()
            val baseLevel = player.skills.getLevelForXp(Skills.MAGIC).toDouble()

            // Base scaling: 0.20% per level → +9.9% at level 99
            val normalScaling = 1.0 + (baseLevel * 0.0020)

            // Boost scaling: 0.25% per level above base, capped at +5%
            val boostedLevels = (currentLevel - baseLevel).coerceAtLeast(0.0)
            val boostedScaling = 1.0 + (boostedLevels * 0.0025).coerceAtMost(0.05)

            return normalScaling * boostedScaling
        }

        private fun getEffectiveMagicDefence(target: Entity): Pair<Int, Int> {
            return when (target) {
                is Player -> {
                    val defenceBonus = target.combatDefinitions.bonuses[BonusType.MagicDefence.index]
                    val defencePrayerMultiplier = target.prayer.defenceMultiplier
                    val magicPrayerMultiplier = target.prayer.magicMultiplier
                    var effectiveDefence = target.skills.getLevel(Skills.DEFENCE) * defencePrayerMultiplier
                    val effectiveMagic = target.skills.getLevel(Skills.MAGIC) * magicPrayerMultiplier
                    effectiveDefence = (effectiveMagic * 7 / 10) + (effectiveDefence * 3 / 10)
                    effectiveDefence += 9

                    Pair(defenceBonus, effectiveDefence.toInt())
                }

                is NPC -> {
                    val magicLevel = target.combatData.magicLevel ?: (target.combatLevel / 3)
                    val defenceBonus = target.combatData.magicDefence.magic ?: target.combatLevel
                    val effectiveDefence = magicLevel + 9
                    Pair(defenceBonus, effectiveDefence)
                }

                else -> Pair(0, 0)
            }
        }
    }

    fun getHitChance(
        player: Player,
        target: Entity,
        type: CombatType,
        accuracyMultiplier: Double = 1.0
    ): Double {
        if (target is NPC && target.name.contains("dummy", ignoreCase = true))
            return 1.0

        return when (type) {
            CombatType.MELEE ->
                MeleeCombat.getHitChance(player, target, accuracyMultiplier)

            CombatType.RANGED ->
                RangedCombat.getHitChance(player, target, accuracyMultiplier)

            CombatType.MAGIC ->
                MagicCombat.getHitChance(player, target, accuracyMultiplier)
        }
    }


    fun getHitChance(
        player: Player,
        target: Entity,
        combatStyle: CombatStyle,
        accuracyMultiplier: Double = 1.0
    ): Double {
        if (target is NPC && target.name.contains("dummy", ignoreCase = true))
            return 1.0
        return when (combatStyle) {
            is MeleeStyle -> MeleeCombat.getHitChance(player, target, accuracyMultiplier)
            is RangedStyle -> RangedCombat.getHitChance(player, target, accuracyMultiplier)
            is MagicStyle -> MagicCombat.getHitChance(player, target, accuracyMultiplier)
            else -> {0.0}
        }
    }

    fun calculateMeleeAccuracy(player: Player, target: Entity, accuracyMultiplier: Double): Boolean =
        ThreadLocalRandom.current().nextDouble() < MeleeCombat.getHitChance(player, target, accuracyMultiplier)

    fun calculateRangedAccuracy(player: Player, target: Entity, accuracyMultiplier: Double): Boolean =
        ThreadLocalRandom.current().nextDouble() < RangedCombat.getHitChance(player, target, accuracyMultiplier)

    fun calculateMagicAccuracy(player: Player, target: Entity, accuracyMultiplier: Double): Boolean =
        ThreadLocalRandom.current().nextDouble() < MagicCombat.getHitChance(player, target, accuracyMultiplier)

    fun getMeleeMaxHit(player: Player, target: Entity, specialMultiplier: Double = 1.0): MaxHit =
        MeleeCombat.calculateMaxHit(player, target, specialMultiplier)

    fun getRangedMaxHit(player: Player, target: Entity, specialMultiplier: Double = 1.0): MaxHit =
        RangedCombat.calculateMaxHit(player, target, specialMultiplier)

    fun getMagicMaxHit(player: Player, target: Entity, baseDamage: Int = -1, spellId: Int = -1, specialMultiplier: Double = 1.0): MaxHit =
        MagicCombat.calculateMaxHit(player, target, baseDamage = baseDamage, spellId = spellId, specialMultiplier)

    private fun getBaseStrengthLevel(player: Player): Int {
        val strength = floor(player.skills.getLevel(Skills.STRENGTH) * player.prayer.strengthMultiplier)
        return strength.toInt()
    }

    private fun getBaseAttackLevel(player: Player): Int {
        val attack = floor(player.skills.getLevel(Skills.ATTACK) * player.prayer.attackMultiplier)
        return attack.toInt()
    }

    private fun getDefenceLevel(target: Entity): Int {
        val level = (when (target) {
            is NPC -> {
                var defenceLevel = target.combatData.defenceLevel
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
        val attackStyle = player.combatDefinitions.attackStyle // This should return your AttackStyle enum
        val targetWeapon = Weapon.getWeapon(player.equipment.weaponId)
        val bonusStyle = targetWeapon.weaponStyle.styleSet.bonusAt(attackStyle)// should be your AttackStyle enum

        return when (target) {
            is Player -> {
                val playerBonusType = when (bonusStyle) {
                    AttackBonusType.STAB -> BonusType.StabDefence
                    AttackBonusType.SLASH -> BonusType.SlashDefence
                    AttackBonusType.CRUSH -> BonusType.CrushDefence
                    AttackBonusType.RANGE -> BonusType.RangeDefence
                    else -> BonusType.MagicDefence
                }
                target.combatDefinitions.bonuses[playerBonusType.index]
            }

            is NPC -> {
                val npcData = target.combatData
                val defence = when (bonusStyle) {
                    AttackBonusType.STAB -> npcData.meleeDefence.stab
                    AttackBonusType.SLASH -> npcData.meleeDefence.slash
                    AttackBonusType.CRUSH -> npcData.meleeDefence.crush
                    AttackBonusType.RANGE -> npcData.rangedDefence.standard
                    else -> npcData.magicDefence.magic
                }
                if (defence > 0) defence else target.combatLevel / 3
            }

            else -> 0
        }
    }

    fun ceilToNextTen(player: Player?, damage: Int): Int {
        if (player == null) return damage
        if (player.varsManager.getBitValue(1485) == 0) return damage
        if (damage <= 0) return damage

        return ((damage + 9) / 10) * 10
    }
}