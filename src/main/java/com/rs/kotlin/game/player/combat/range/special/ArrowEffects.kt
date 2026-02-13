package com.rs.kotlin.game.player.combat.range.special

import com.rs.java.game.Graphics
import com.rs.java.game.Hit.HitLook
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.CombatType
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.special.SpecialEffect
import com.rs.kotlin.game.player.combat.special.addHit
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager

object ArrowEffects {

    val ZAMORAK_ARROW = SpecialEffect(
        chance = 10,
        execute = { context ->
            val chance = if (context.weaponId == 19149) 5 else 10
            context.ammo?.endGfx = null

            if (!Utils.roll(1, chance)) return@SpecialEffect false
            val projectileDelay = ProjectileManager.send(Projectile.ARROW,
                context.component5()?.projectileId ?: 27, context.attacker, context.defender)
            val range = context.addHit(CombatType.RANGED).delay(projectileDelay).roll()
            val magic = context.addHit(CombatType.RANGED).delay(projectileDelay + 1).look(HitLook.MAGIC_DAMAGE).maxHit((range.maxHit * 0.2).toInt()).roll()


            if (magic.damage == 0) {
                context.ammo?.endGfx = Graphics(85, 100)
            } else {
                context.ammo?.endGfx = Graphics(129)
                magic.setCriticalMark()
            }
            true
        }
    )

    val SARADOMIN_ARROW = SpecialEffect(
        chance = 10,
        execute = { context ->
            val chance = if (context.weaponId == 19143) 5 else 10
            context.ammo?.endGfx = null

            if (!Utils.roll(1, chance)) return@SpecialEffect false
            val projectileDelay = ProjectileManager.send(Projectile.ARROW,
                context.component5()?.projectileId ?: 27, context.attacker, context.defender)
            val range = context.addHit(CombatType.RANGED).delay(projectileDelay).roll()
            val magic = context.addHit(CombatType.RANGED).delay(projectileDelay + 1).look(HitLook.MAGIC_DAMAGE).maxHit((range.maxHit * 0.2).toInt()).roll()
            if (magic.damage == 0) {
                context.ammo?.endGfx = Graphics(85, 100)
            } else {
                context.ammo?.endGfx = Graphics(128)
                magic.setCriticalMark()
            }
            true
        }
    )

    val GUTHIX_ARROW = SpecialEffect(
        chance = 10,
        execute = { context ->
            val chance = if (context.weaponId == 19146) 5 else 10
            context.ammo?.endGfx = null

            if (!Utils.roll(1, chance)) return@SpecialEffect false
            val projectileDelay = ProjectileManager.send(Projectile.ARROW,
                context.component5()?.projectileId ?: 27, context.attacker, context.defender)
            val range = context.addHit(CombatType.RANGED).delay(projectileDelay).roll()
            val magic = context.addHit(CombatType.RANGED).delay(projectileDelay + 1).look(HitLook.MAGIC_DAMAGE).maxHit((range.maxHit * 0.2).toInt()).roll()
            if (magic.damage == 0) {
                context.ammo?.endGfx = Graphics(85, 100)
            } else {
                context.ammo?.endGfx = Graphics(127)
                magic.setCriticalMark()
            }
            true
        }
    )
}
