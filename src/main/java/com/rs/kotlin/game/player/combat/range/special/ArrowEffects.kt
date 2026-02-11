package com.rs.kotlin.game.player.combat.range.special

import com.rs.java.game.Graphics
import com.rs.java.game.Hit.HitLook
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.CombatType
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.special.SpecialEffect

object ArrowEffects {

    val ZAMORAK_ARROW = SpecialEffect(
        chance = 10,
        execute = { context ->
            val chance = if (context.weaponId == 19149) 5 else 10
            context.ammo?.endGfx = null

            if (!Utils.roll(1, chance)) return@SpecialEffect false
            val rangedHit = context.combat.registerHit(attacker = context.attacker,
                defender = context.defender,
                attackStyle = context.attackStyle,
                weapon = context.weapon,
                combatType = CombatType.RANGED)
            val magicHit = context.combat.registerHit(
                attacker = context.attacker,
                defender = context.defender,
                attackStyle = context.attackStyle,
                weapon = context.weapon,
                combatType = CombatType.RANGED,
                hitLook = HitLook.MAGIC_DAMAGE,
                damageMultiplier = 0.2
            )

            if (magicHit.damage == 0) {
                context.ammo?.endGfx = Graphics(85, 100)
            } else {
                context.ammo?.endGfx = Graphics(129)
                magicHit.setCriticalMark()
            }
            context.combat.delayHits(
                PendingHit(rangedHit, context.defender, context.combat.getHitDelay()),
                PendingHit(magicHit, context.defender, context.combat.getHitDelay() + 1)
            )

            true
        }
    )

    val SARADOMIN_ARROW = SpecialEffect(
        chance = 10,
        execute = { context ->
            val chance = if (context.weaponId == 19143) 5 else 10
            context.ammo?.endGfx = null

            if (!Utils.roll(1, chance)) return@SpecialEffect false
            val rangedHit = context.combat.registerHit(attacker = context.attacker,
                defender = context.defender,
                attackStyle = context.attackStyle,
                weapon = context.weapon,
                combatType = CombatType.RANGED)
            val magicHit = context.combat.registerHit(
                attacker = context.attacker,
                defender = context.defender,
                attackStyle = context.attackStyle,
                weapon = context.weapon,
                combatType = CombatType.RANGED,
                hitLook = HitLook.MAGIC_DAMAGE,
                damageMultiplier = 0.2
            )

            if (magicHit.damage == 0) {
                context.ammo?.endGfx = Graphics(85, 100)
            } else {
                context.ammo?.endGfx = Graphics(128)
                magicHit.setCriticalMark()
            }
            context.combat.delayHits(
                PendingHit(rangedHit, context.defender, context.combat.getHitDelay()),
                PendingHit(magicHit, context.defender, context.combat.getHitDelay() + 1)
            )

            true
        }
    )

    val GUTHIX_ARROW = SpecialEffect(
        chance = 10,
        execute = { context ->
            val chance = if (context.weaponId == 19146) 5 else 10
            context.ammo?.endGfx = null

            if (!Utils.roll(1, chance)) return@SpecialEffect false
            val rangedHit = context.combat.registerHit(attacker = context.attacker,
                defender = context.defender,
                attackStyle = context.attackStyle,
                weapon = context.weapon,
                combatType = CombatType.RANGED)
            val magicHit = context.combat.registerHit(
                attacker = context.attacker,
                defender = context.defender,
                attackStyle = context.attackStyle,
                weapon = context.weapon,
                combatType = CombatType.RANGED,
                hitLook = HitLook.MAGIC_DAMAGE,
                damageMultiplier = 0.2
            )

            if (magicHit.damage == 0) {
                context.ammo?.endGfx = Graphics(85, 100)
            } else {
                context.ammo?.endGfx = Graphics(127)
                magicHit.setCriticalMark()
            }
            context.combat.delayHits(
                PendingHit(rangedHit, context.defender, context.combat.getHitDelay()),
                PendingHit(magicHit, context.defender, context.combat.getHitDelay() + 1)
            )

            true
        }
    )
}
