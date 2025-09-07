package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.damage.CombatMultipliers
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
        fun calculateMaxHit(player: Player, target: Entity, specialMultiplier: Double = 1.0): Hit
    }

    private fun computeHitChance(attack: Int, defence: Int): Double {
        return if (attack > defence) {
            1.0 - (defence + 2.0) / (2.0 * (attack + 1.0))
        } else {
            attack.toDouble() / (2.0 * (defence + 1.0))
        }
    }

    private fun floorToInt(value: Double): Int = floor(value).toInt()

    private object MeleeCombat : AccuracyCalculator, MaxHitCalculator {
        override fun getHitChance(player: Player, target: Entity, accuracyMultiplier: Double): Double {
            /*
             * Attack Calculation DONE
             */
            val baseAttack = getBaseAttackLevel(player)
            val bonusType = getAttackBonusIndex(player)
            val attackBonus = player.combatDefinitions.bonuses[bonusType].toDouble()
            val styleBonus = getAttackStyleBonus(player)
            val equipmentSet = EquipmentSets.getSet(player)
            val voidBonus = EquipmentSets.getAccuracyMultiplier(equipmentSet, CombatMultipliers.Style.MELEE)
            val specialBonus = CombatMultipliers.getMultiplier(
                player,
                target,
                CombatMultipliers.Style.MELEE
            )//TODO THINGS LIKE SLAYER HELMET, SALVE AMMY ETC

            var effectiveAttack = player.skills.getLevel(Skills.ATTACK) * player.prayer.attackMultiplier
            effectiveAttack += styleBonus + 8
            effectiveAttack *= voidBonus

            val attackRoll = effectiveAttack * (attackBonus + 64) * specialBonus * accuracyMultiplier


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

        override fun calculateMaxHit(player: Player, target: Entity, specialMultiplier: Double): Hit {
            val styleBonus = getStrengthStyleBonus(player)

            val equipmentSet = EquipmentSets.getSet(player)
            val dharokMultiplier = getDharokMultiplier(player)
            val voidBonus = EquipmentSets.getDamageMultiplier(equipmentSet, CombatMultipliers.Style.MELEE)
            val specialBonus = CombatMultipliers.getMultiplier(player, target, CombatMultipliers.Style.MELEE)

            val strengthBonus = player.combatDefinitions.bonuses[BonusType.StregthBonus.index].toDouble()

            val baseStrengthLevel = getBaseStrengthLevel(player)//correct
            val effectiveStrength = floor((baseStrengthLevel + styleBonus + 8 * voidBonus) * dharokMultiplier)
            val baseDamage = 0.5 + ((effectiveStrength * (strengthBonus + 640)) / 640) * specialBonus
            val hit = Hit(player, 0, 0, Hit.HitLook.MELEE_DAMAGE)
            val maxHit = (baseDamage * specialMultiplier).toInt()
            var damage = Utils.random(maxHit)
            if (target is NPC) {
                if (target.id == 4474) {
                    damage = maxHit
                }
            }
            hit.baseMaxHit = baseDamage.toInt()
            hit.maxHit = maxHit
            hit.damage = damage
            if (damage >= floor(baseDamage * 0.95)) {
                hit.setCriticalMark()
            }
            if (player.developerMode) {
                player.message(
                    "[Combat Damage] -> " +
                            "BaseDamage: ${baseDamage}, " +
                            "MaxHit: ${maxHit}, " +
                            "Rolled Damage: ${damage}, " +
                            "Critical?: ${damage >= floor(baseDamage * 0.95)}"
                )
            }
            return hit
        }
    }

    private object RangedCombat : AccuracyCalculator, MaxHitCalculator {
        override fun getHitChance(player: Player, target: Entity, accuracyMultiplier: Double): Double {
            /*
            * Range attack Calculation DONE
            */
            val bonusType = getAttackBonusIndex(player)
            val rangeBonus = player.combatDefinitions.bonuses[bonusType]
            val styleBonus = getAttackStyleBonus(player)
            val equipmentSet = EquipmentSets.getSet(player)
            val voidBonus = EquipmentSets.getAccuracyMultiplier(equipmentSet, CombatMultipliers.Style.RANGE)
            val specialBonus = CombatMultipliers.getMultiplier(player, target, CombatMultipliers.Style.RANGE)
            val (zaryteAccuracy, zaryteDamage, zaryteMaxHit) = getZaryteBowBoost(player, target)

            var effectiveAttack = player.skills.getLevel(Skills.RANGE) * player.prayer.rangedMultiplier
            effectiveAttack += styleBonus + 8
            effectiveAttack *= voidBonus * zaryteAccuracy
            val attackRoll = effectiveAttack * (rangeBonus + 64) * specialBonus * accuracyMultiplier
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

        override fun calculateMaxHit(player: Player, target: Entity, specialMultiplier: Double): Hit {
            val rangedLvl = player.skills.getLevel(Skills.RANGE).toDouble()
            val prayerBonus = player.prayer.rangedMultiplier
            val styleBonus = getStrengthStyleBonus(player)
            val equipmentSet = EquipmentSets.getSet(player)
            val voidBonus = EquipmentSets.getDamageMultiplier(equipmentSet, CombatMultipliers.Style.RANGE)
            val specialBonus = CombatMultipliers.getMultiplier(player, target, CombatMultipliers.Style.MELEE)
            val (zaryteAccuracy, zaryteDamage, zaryteMaxHit) = getZaryteBowBoost(player, target)

            val baseStrength = floor(rangedLvl * prayerBonus)
            val effectiveStrength = floor((baseStrength + styleBonus + 8) * voidBonus * zaryteDamage)
            val strengthBonus = player.combatDefinitions.bonuses[BonusType.RangedStrBonus.index].toDouble()
            val baseDamage = 0.5 + (effectiveStrength * (strengthBonus + 640) / 640) * specialBonus
            var maxHit = floor(baseDamage * specialMultiplier).toInt()
            var damage = Utils.random(maxHit)
            if (target is NPC) {
                if (target.id == 4474) {
                    damage = maxHit
                }
            }
            val hit = Hit(player, damage, maxHit, Hit.HitLook.RANGE_DAMAGE)
            if (zaryteMaxHit > 0) {
                if (hit.damage > zaryteMaxHit) {
                    hit.damage = zaryteMaxHit;
                }
                if (hit.damage >= floor(zaryteMaxHit * 0.95))
                    hit.setCriticalMark()
            }
            if (damage >= floor(baseDamage * 0.95)) {
                hit.setCriticalMark()
            }
            if (player.developerMode) {
                player.message(
                    "Ranged Damage Calculation -> " +
                            "BaseDamage: ${baseDamage}, " +
                            "StyleBonus: ${styleBonus}, " +
                            "MaxHit: ${maxHit}, " +
                            "Rolled Damage: ${damage}, " +
                            "Critical?: ${damage >= floor(baseDamage * 0.90)}"
                )
            }
            return hit
        }

        fun getZaryteBowBoost(player: Player, target: Entity): Triple<Double, Double, Int> {
            if (target !is NPC) return Triple(1.0, 1.0, 0)

            val weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON.toInt())
            val isZaryte = weapon.isAnyOf("item.zaryte_bow", "item.zaryte_bow_degraded")
            val isHexhunter = weapon.isAnyOf("item.hexhunter_bow", "item.hexhunter_bow_b")

            if (!isZaryte && !isHexhunter) return Triple(1.0, 1.0, 0)
            target.setBonuses()
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
            println("damageMultiplier from bow: $damageMultiplier, accuracy: $accuracyMultiplier")
            val damageCap = 810
            return Triple(accuracyMultiplier, damageMultiplier, damageCap)
        }
    }


    private object MagicCombat : AccuracyCalculator {

       override fun getHitChance(player: Player, target: Entity, accuracyMultiplier: Double): Double {
           val magicBonus = player.combatDefinitions.bonuses[BonusType.MagicAttack.index]
           val specialBonus = CombatMultipliers.getMultiplier(player, target, CombatMultipliers.Style.MAGIC)
           val equipmentSet = EquipmentSets.getSet(player)
           val voidAccuracy = EquipmentSets.getAccuracyMultiplier(equipmentSet, CombatMultipliers.Style.MAGIC)

           var effectiveMagic = player.skills.getLevel(Skills.MAGIC) * player.prayer.magicMultiplier
           effectiveMagic += 9//TODO STYLE ACCURACY FOR POLYPORE
           effectiveMagic *= voidAccuracy
           val attackRoll = effectiveMagic * (magicBonus + 64) * specialBonus * accuracyMultiplier

           val (defenceBonus, effectiveDefenceLevel) = getEffectiveMagicDefence(target)
           val defenceRoll = effectiveDefenceLevel * (defenceBonus + 64)
            return computeHitChance(attackRoll.toInt(), defenceRoll.toInt())
        }

        fun calculateMaxHit(player: Player, target: Entity, spellId: Int, specialMultiplier: Double = 1.0): Hit {
            val spell = Spellbook.getSpellById(player, spellId);
            val baseDamage = spell?.damage
                ?: if (spellId == 1000) {
                    (5 * player.skills.getLevel(Skills.MAGIC)) - 180
                } else {
                    10
                }
            val magicDamageBonus = player.combatDefinitions.bonuses[BonusType.MagicDamage.index].toDouble()
            val magicStrengthMultiplier = 1.0 + magicDamageBonus / 100.0
            val equipmentSet = EquipmentSets.getSet(player)
            val voidDamage = EquipmentSets.getDamageMultiplier(equipmentSet, CombatMultipliers.Style.MAGIC)

            var levelMultiplier = getMagicLevelMultiplier(player)
            if (target is NPC) {//just to make sure magic is a bit stronger for monsters so magic is a viable style in pvm
                levelMultiplier *= player.prayer.magicMultiplier
            }
            val maxHit = baseDamage * magicStrengthMultiplier * levelMultiplier * voidDamage * specialMultiplier
            var damage = Utils.random(maxHit.toInt())
            if (target is NPC) {
                if (target.id == 4474) {
                    damage = maxHit.toInt();
                }
            }
            val hit = Hit(player, damage, maxHit.toInt(), Hit.HitLook.MAGIC_DAMAGE)
            if (damage >= floor(maxHit * 0.95)) {
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
        combatStyle: CombatStyle,
        accuracyMultiplier: Double = 1.0
    ): Double {
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

    fun calculateMeleeMaxHit(player: Player, target: Entity, specialMultiplier: Double = 1.0): Hit =
        MeleeCombat.calculateMaxHit(player, target, specialMultiplier)

    fun calculateRangedMaxHit(player: Player, target: Entity, specialMultiplier: Double = 1.0): Hit =
        RangedCombat.calculateMaxHit(player, target, specialMultiplier)

    fun calculateMagicMaxHit(player: Player, target: Entity, spellId: Int, specialMultiplier: Double = 1.0): Hit =
        MagicCombat.calculateMaxHit(player, target, spellId, specialMultiplier)


    private fun calculateHitProbability(attack: Int, defence: Int): Boolean {
        val random = Math.random()

        val prob: Double = if (attack > defence) {
            1.0 - (defence + 2.0) / (2.0 * (attack + 1.0))
        } else {
            attack.toDouble() / (2.0 * (defence + 1.0))
        }
        val probPercent = prob * 100
        val randomPercent = random * 100

        return random < prob
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
        val range = floorToInt(player.skills.getLevel(Skills.RANGE) * player.prayer.rangedMultiplier)
        return range
    }

    private fun getEffectiveMagicLevel(player: Player): Int {
        val magic = floor(player.skills.getLevel(Skills.MAGIC) * player.prayer.magicMultiplier) + 8
        return magic.toInt()
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
}