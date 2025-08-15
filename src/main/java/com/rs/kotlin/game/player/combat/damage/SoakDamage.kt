package com.rs.kotlin.game.player.combat.damage

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.player.CombatDefinitions
import com.rs.java.game.player.Player

class SoakDamage {

    companion object {
        fun handleAbsorb(attacker: Player, defender: Entity, incommingHit: Hit) {
            val MINIMUM_DAMAGE_THRESHOLD = 200
            val MINIMUM_HP_THRESHOLD = 200

            if (defender !is Player) {
                return
            }
            val hitType = incommingHit.look
            if (hitType != HitLook.MELEE_DAMAGE && hitType != HitLook.RANGE_DAMAGE && hitType != HitLook.MAGIC_DAMAGE) {
                return
            }
            val absorptionBonus = getAbsorptionBonus(defender, hitType)
            val reducibleDamage = incommingHit.damage - MINIMUM_DAMAGE_THRESHOLD
            val reducedDamage = (reducibleDamage * absorptionBonus) / 100
            if (absorptionBonus == 0 || reducibleDamage <= 0 || reducedDamage <= 0) {
                return
            }
            if (defender.getHitpoints() <= MINIMUM_HP_THRESHOLD) {
                return
            }
            incommingHit.damage = incommingHit.damage - reducedDamage
            incommingHit.soaking = Hit(attacker, reducedDamage, HitLook.ABSORB_DAMAGE)
        }

        private fun getAbsorptionBonus(player: Player, hitType: HitLook): Int {
            val combatDefs = player.getCombatDefinitions()

            return when (hitType) {
                HitLook.MELEE_DAMAGE -> combatDefs.bonuses[CombatDefinitions.ABSORVE_MELEE_BONUS]
                HitLook.RANGE_DAMAGE -> combatDefs.bonuses[CombatDefinitions.ABSORVE_RANGE_BONUS]
                HitLook.MAGIC_DAMAGE -> combatDefs.bonuses[CombatDefinitions.ABSORVE_MAGE_BONUS]
                else -> 0
            }
        }
    }
}