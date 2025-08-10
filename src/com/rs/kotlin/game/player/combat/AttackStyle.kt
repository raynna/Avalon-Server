package com.rs.kotlin.game.player.combat

import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.actions.combat.Combat

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
        val baseXp = (damage * 0.4)
        val hpXp = (damage * 0.133)
        val type = attackStyle.combatType
        if (hitpoints) {
            player.skills.addXp(Skills.HITPOINTS, hpXp)
        }
        when (this) {
            ATTACK -> player.skills.addXp(Skills.ATTACK, baseXp)
            STRENGTH -> player.skills.addXp(Skills.STRENGTH, baseXp)
            DEFENCE -> player.skills.addXp(Skills.DEFENCE, baseXp)
            RANGED -> player.skills.addXp(Skills.RANGE, baseXp)
            SHARED -> {
                if (type == CombatType.RANGED) {
                    val split = baseXp / 2
                    player.skills.addXp(Skills.RANGE, split)
                    player.skills.addXp(Skills.DEFENCE, split)
                }
                val split = baseXp / 3
                player.skills.addXp(Skills.ATTACK, split)
                player.skills.addXp(Skills.STRENGTH, split)
                player.skills.addXp(Skills.DEFENCE, split)
            }
        }
    }
}


enum class CombatType {
    MELEE, RANGED, MAGIC
}

