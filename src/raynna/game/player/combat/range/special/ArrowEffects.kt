package raynna.game.player.combat.range.special

import raynna.game.Graphics
import raynna.game.Hit.HitLook
import raynna.util.Utils
import raynna.game.player.combat.CombatType
import raynna.game.player.combat.damage.PendingHit
import raynna.game.player.combat.special.EffectResult
import raynna.game.player.combat.special.SpecialEffect
import raynna.game.player.combat.special.addHit
import raynna.game.world.projectile.Projectile
import raynna.game.world.projectile.ProjectileManager

object ArrowEffects {
    val ZAMORAK_ARROW =
        SpecialEffect(
            chance = 10,
            execute = { context ->
                val chance = if (context.weaponId == 19149) 5 else 10
                context.ammo?.endGfx = null

                if (!Utils.roll(1, chance)) return@SpecialEffect EffectResult.CONTINUE
                val projectileDelay =
                    ProjectileManager.send(
                        Projectile.ARROW,
                        context.component5()?.projectileId ?: 27,
                        context.attacker,
                        context.defender,
                    )
                val range = context.addHit(CombatType.RANGED).delay(projectileDelay).roll()
                val magic =
                    context
                        .addHit(
                            CombatType.RANGED,
                        ).delay(projectileDelay + 1)
                        .look(HitLook.MAGIC_DAMAGE)
                        .maxHit((range.maxHit * 0.2).toInt())
                        .roll()

                if (magic.damage == 0) {
                    context.ammo?.endGfx = Graphics(85, 100)
                } else {
                    context.ammo?.endGfx = Graphics(129)
                    magic.setCriticalMark()
                }
                EffectResult.COMPLETE
            },
        )

    val SARADOMIN_ARROW =
        SpecialEffect(
            chance = 10,
            execute = { context ->
                val chance = if (context.weaponId == 19143) 5 else 10
                context.ammo?.endGfx = null

                if (!Utils.roll(1, chance)) return@SpecialEffect EffectResult.CONTINUE
                val projectileDelay =
                    ProjectileManager.send(
                        Projectile.ARROW,
                        context.component5()?.projectileId ?: 27,
                        context.attacker,
                        context.defender,
                    )
                val range = context.addHit(CombatType.RANGED).delay(projectileDelay).roll()
                val magic =
                    context
                        .addHit(
                            CombatType.RANGED,
                        ).delay(projectileDelay + 1)
                        .look(HitLook.MAGIC_DAMAGE)
                        .maxHit((range.maxHit * 0.2).toInt())
                        .roll()
                if (magic.damage == 0) {
                    context.ammo?.endGfx = Graphics(85, 100)
                } else {
                    context.ammo?.endGfx = Graphics(128)
                    magic.setCriticalMark()
                }
                EffectResult.COMPLETE
            },
        )

    val GUTHIX_ARROW =
        SpecialEffect(
            chance = 10,
            execute = { context ->
                val chance = if (context.weaponId == 19146) 5 else 10
                context.ammo?.endGfx = null

                if (!Utils.roll(1, chance)) return@SpecialEffect EffectResult.CONTINUE
                val projectileDelay =
                    ProjectileManager.send(
                        Projectile.ARROW,
                        context.component5()?.projectileId ?: 27,
                        context.attacker,
                        context.defender,
                    )
                val range = context.addHit(CombatType.RANGED).delay(projectileDelay).roll()
                val magic =
                    context
                        .addHit(
                            CombatType.RANGED,
                        ).delay(projectileDelay + 1)
                        .look(HitLook.MAGIC_DAMAGE)
                        .maxHit((range.maxHit * 0.2).toInt())
                        .roll()
                if (magic.damage == 0) {
                    context.ammo?.endGfx = Graphics(85, 100)
                } else {
                    context.ammo?.endGfx = Graphics(127)
                    magic.setCriticalMark()
                }
                EffectResult.COMPLETE
            },
        )
}
