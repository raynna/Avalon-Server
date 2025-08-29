package com.rs.kotlin.game.player.combat

import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import java.util.*
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

    fun distributeXp(player: Player, attackStyle: AttackStyle, damage: Int, hitpoints: Boolean = true) {
        val xpToAdd = mutableMapOf<Int, Double>()

        val baseXp = damage * 0.4
        val hpXp = damage * 0.133
        val type = attackStyle.combatType
        val isOneXpPerHit = player.toggles("ONEXPPERHIT", false)
        val isOneXHits = player.varsManager.getBitValue(1485) == 1
        if (isOneXpPerHit && damage > 0) {
            val xp = if (isOneXHits) ceil(damage / 10.0) else damage
            player.skills.addXpDelayed(Skills.HITPOINTS, xp.toDouble())
            return
        }
        if (hitpoints) {
            xpToAdd[Skills.HITPOINTS] = hpXp
        }

        when (this) {
            ATTACK -> xpToAdd[Skills.ATTACK] = (xpToAdd[Skills.ATTACK] ?: 0.0) + baseXp
            STRENGTH -> xpToAdd[Skills.STRENGTH] = (xpToAdd[Skills.STRENGTH] ?: 0.0) + baseXp
            DEFENCE -> xpToAdd[Skills.DEFENCE] = (xpToAdd[Skills.DEFENCE] ?: 0.0) + baseXp
            RANGED -> xpToAdd[Skills.RANGE] = (xpToAdd[Skills.RANGE] ?: 0.0) + baseXp
            SHARED -> {
                if (type == CombatType.RANGED) {
                    val split = baseXp / 2
                    xpToAdd[Skills.RANGE] = (xpToAdd[Skills.RANGE] ?: 0.0) + split
                    xpToAdd[Skills.DEFENCE] = (xpToAdd[Skills.DEFENCE] ?: 0.0) + split
                }
                val split = baseXp / 3
                xpToAdd[Skills.ATTACK] = (xpToAdd[Skills.ATTACK] ?: 0.0) + split
                xpToAdd[Skills.STRENGTH] = (xpToAdd[Skills.STRENGTH] ?: 0.0) + split
                xpToAdd[Skills.DEFENCE] = (xpToAdd[Skills.DEFENCE] ?: 0.0) + split
            }
        }
        for ((skill, xp) in xpToAdd) {
            player.skills.addXpDelayed(skill, xp)
        }
    }

}


enum class CombatType {
    MELEE, RANGED, MAGIC
}

