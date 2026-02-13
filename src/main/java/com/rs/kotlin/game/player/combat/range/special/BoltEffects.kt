package com.rs.kotlin.game.player.combat.range.special

import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.item.Item
import com.rs.java.game.npc.NPC
import com.rs.java.game.npc.combat.DragonFire
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.CombatType
import com.rs.kotlin.game.player.combat.damage.CombatHitRoll
import com.rs.kotlin.game.player.combat.special.SpecialEffect
import com.rs.kotlin.game.player.combat.special.addHit
import com.rs.kotlin.game.player.combat.special.procHit
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager
import kotlin.math.floor
import kotlin.math.min

object BoltEffects {

    val RUBY = SpecialEffect(
        execute = { context ->
            val attacker = context.attacker
            val defender = context.defender
            val guaranteed = context.guaranteedBoltEffect
            val rawChance = if (defender is NPC) 6 else 11
            val extraChance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 2 else 0

            if (!Utils.roll(rawChance + extraChance, 100) && !guaranteed)
                return@SpecialEffect false

            defender.gfx("graphic.ruby_bolt_effect", 0)
            defender.playSound("sound.ruby_bolt_effect", 1)
            val cap = if (guaranteed) 1100 else 1000
            val boost = if (guaranteed) 0.22 else 0.20
            val damage = (defender.hitpoints * boost).toInt().coerceAtMost(cap)
            val selfDamage = (attacker.hitpoints * 0.1).toInt()
            val projectileDelay = ProjectileManager.send(Projectile.BOLT, 27, attacker, context.defender)
            context.procHit(attacker, defender).delay(projectileDelay).damage(damage).apply()
            context.procHit(attacker, attacker).delay(projectileDelay).damage(selfDamage).apply()
            true
        }
    )

    val DIAMOND = SpecialEffect(
        execute = { context ->
            val guaranteed = context.guaranteedBoltEffect
            val rawChance = if (context.defender is NPC) 10 else 5
            val extraChance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 2 else 0

            if (!Utils.roll(rawChance + extraChance, 100) && !guaranteed)
                return@SpecialEffect false
            context.defender.gfx("graphic.diamond_bolt_effect", 0)
            context.defender.playSound("sound.diamond_bolt_effect", 1)
            val projectileDelay = ProjectileManager.send(Projectile.BOLT, 27, context.attacker, context.defender)
            val boost = if (guaranteed) 1.26 else 1.15
            context.addHit(CombatType.RANGED).delay(projectileDelay).damageMultiplier(boost).roll(CombatHitRoll.GUARANTEED)
            true
        }
    )

    val OPAL = SpecialEffect(
        execute = { context ->
            val guaranteed = context.guaranteedBoltEffect
            val chance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 12 else 6

            if (!Utils.roll(chance, 100) && !guaranteed)
                return@SpecialEffect false
            val projectileDelay = ProjectileManager.send(Projectile.BOLT, 27, context.attacker, context.defender)
            val rangedLevel = context.attacker.skills.getLevel(Skills.RANGE)
            val extra = floor(rangedLevel * 0.10).toInt() * 10
            val hit = context.addHit(CombatType.RANGED)
                .delay(projectileDelay)
                .accuracy(if (guaranteed) 2.0 else 1.0)
                .bonus { hit -> if (hit.damage > 0) hit.damage += extra}
                .roll()
            if (hit.damage == 0) return@SpecialEffect false
            context.defender.gfx("graphic.opal_bolt_effect", 0)
            context.defender.playSound("sound.opal_bolt_effect", 1)
            true
        }
    )

    val DRAGONSTONE = SpecialEffect(
        execute = { context ->
            val guaranteed = context.guaranteedBoltEffect
            val chance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 12 else 6

            if (DragonFire.hasFireProtection(context.defender))
                return@SpecialEffect false

            if (!Utils.roll(chance, 100) && !guaranteed)
                return@SpecialEffect false
            val projectileDelay = ProjectileManager.send(Projectile.BOLT, 27, context.attacker, context.defender)
            context.defender.gfx("graphic.dragonstone_bolt_effect", 0)
            context.defender.playSound("sound.dragonstone_bolt_effect", 1)
            val boost =
                if (context.weaponId == Item.getId("item.zaryte_crossbow")) 0.22
                else 0.20

            val rangedLevel = context.attacker.skills.getLevel(Skills.RANGE)

            val extra =
                floor(rangedLevel * boost).toInt() * 10
            val hit = context.addHit(CombatType.RANGED).delay(projectileDelay).roll().apply {
                if (damage > 0) {
                    damage = min(damage + extra, context.defender.hitpoints)
                }
            }
            if (hit.damage == 0) return@SpecialEffect false
            context.defender.gfx("graphic.dragonstone_bolt_effect", 0)
            context.defender.playSound("sound.dragonstone_bolt_effect", 1)
            true
        }
    )


    val ONYX = SpecialEffect(
        execute = { context ->
            val attacker = context.attacker
            val defender = context.defender
            val guaranteed = context.guaranteedBoltEffect
            val extraChance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 2 else 0
            val chance = if (defender is NPC) 11 else 10

            if (!Utils.roll(chance + extraChance, 100) && !guaranteed)
                return@SpecialEffect false
            val projectileDelay = ProjectileManager.send(Projectile.BOLT, 27, attacker, defender)
            val boltHit = context.addHit(CombatType.RANGED)
                .accuracy(if (guaranteed) 2.0 else 1.0)
                .damageMultiplier(if (guaranteed) 1.32 else 1.2)
                .delay(projectileDelay)
                .roll()
            if (boltHit.damage == 0) return@SpecialEffect false
            context.defender.gfx("graphic.onyx_bolt_effect", 0)
            context.defender.playSound("sound.onyx_bolt_effect", 1)
            val heal = (boltHit.damage * 0.25).toInt()
            attacker.applyHeal(Hit(attacker, heal, HitLook.HEALED_DAMAGE))
            true
        }
    )


}
