package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import kotlin.math.ceil

enum class AttackStyle(
    val attackSpeedModifier: Int = 0,
    val combatType: CombatType,
    val xpMode: XpMode
) {
    ACCURATE(combatType = CombatType.MELEE, xpMode = XpMode.ATTACK),
    AGGRESSIVE(combatType = CombatType.MELEE, xpMode = XpMode.STRENGTH),
    DEFENSIVE(combatType = CombatType.MELEE, xpMode = XpMode.DEFENCE),
    CONTROLLED(combatType = CombatType.MELEE, xpMode = XpMode.SHARED),

    ACCURATE_RANGE(combatType = CombatType.RANGED, xpMode = XpMode.RANGED),
    RAPID(attackSpeedModifier = -1, combatType = CombatType.RANGED, xpMode = XpMode.RANGED),
    LONGRANGE(attackSpeedModifier = 1, combatType = CombatType.RANGED, xpMode = XpMode.SHARED);
}


enum class XpMode {
    ATTACK, STRENGTH, DEFENCE, SHARED, RANGED;


    data class Fraction(val num: Int, val den: Int)

    companion object {

        @JvmStatic
        fun pvpXpMultiplier(opponentCombatLevel: Int): Fraction {
            val n = opponentCombatLevel / 20

            val candNum = 40 + n
            val candDen = 40

            return if (candNum * 8 > candDen * 9)
                Fraction(9, 8)
            else
                Fraction(candNum, candDen)
        }

        @JvmStatic
        fun applyMultiplierFloor(xp: Int, mult: Fraction): Int {
            return (xp * mult.num) / mult.den
        }
    }

    fun distributeXp(attacker: Player, defender: Entity, attackStyle: AttackStyle, damage: Int, hitpoints: Boolean = true) {

        var baseXp = (damage * 12) / 30   // 0.4
        var hpXp   = (damage * 4) / 30    // 0.1333

        val type = attackStyle.combatType

        val isOneXpPerHit = attacker.toggles("ONEXPPERHIT", false)
        val isOneXHits = attacker.varsManager.getBitValue(1485) == 1

        if (isOneXpPerHit && damage > 0) {
            val xp = if (isOneXHits) ceil(damage / 10.0).toInt() else damage
            attacker.skills.addXpDelayed(Skills.HITPOINTS, xp.toDouble())
            return
        }

        if (defender is Player) {
            val mult = pvpXpMultiplier(defender.skills.combatLevel)

            baseXp = applyMultiplierFloor(baseXp, mult)
            hpXp   = applyMultiplierFloor(hpXp, mult)
        }
        if (hitpoints) {
            attacker.skills.addXpDelayed(Skills.HITPOINTS, hpXp.toDouble())
        }

        when (this) {
            ATTACK   -> attacker.skills.addXpDelayed(Skills.ATTACK, baseXp.toDouble())
            STRENGTH -> attacker.skills.addXpDelayed(Skills.STRENGTH, baseXp.toDouble())
            DEFENCE  -> attacker.skills.addXpDelayed(Skills.DEFENCE, baseXp.toDouble())
            RANGED   -> attacker.skills.addXpDelayed(Skills.RANGE, baseXp.toDouble())

            SHARED -> {
                if (type == CombatType.RANGED) {
                    val split = baseXp / 2
                    attacker.skills.addXpDelayed(Skills.RANGE, split.toDouble())
                    attacker.skills.addXpDelayed(Skills.DEFENCE, split.toDouble())
                } else {
                    val split = baseXp / 3
                    attacker.skills.addXpDelayed(Skills.ATTACK, split.toDouble())
                    attacker.skills.addXpDelayed(Skills.STRENGTH, split.toDouble())
                    attacker.skills.addXpDelayed(Skills.DEFENCE, split.toDouble())
                }
            }
        }
    }
}


enum class CombatType {
    MELEE, RANGED, MAGIC
}

