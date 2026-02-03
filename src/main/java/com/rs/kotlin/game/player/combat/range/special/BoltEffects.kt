package com.rs.kotlin.game.player.combat.range.special

import com.rs.java.game.Hit
import com.rs.java.game.item.Item
import com.rs.java.game.npc.NPC
import com.rs.java.game.npc.combat.DragonFire
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.CombatType
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.special.SpecialEffect
import com.rs.kotlin.game.player.combat.special.hits
import com.rs.kotlin.game.player.combat.special.registerDamage
import com.rs.kotlin.game.player.combat.special.rollRanged
import kotlin.math.floor
import kotlin.math.min

object BoltEffects {

    val RUBY = SpecialEffect(
        execute = { context ->
            val guaranteed = context.guaranteedBoltEffect
            val rawChance = if (context.defender is NPC) 6 else 11
            val extraChance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 2 else 0

            if (!Utils.roll(rawChance + extraChance, 100) && !guaranteed)
                return@SpecialEffect false

            context.defender.gfx("graphic.ruby_bolt_effect", 0)
            context.defender.playSound("sound.ruby_bolt_effect", 1)

            context.hits {
                val cap = if (guaranteed) 1100 else 1000
                val boost = if (guaranteed) 0.22 else 0.20
                val damage = (context.defender.hitpoints * boost).toInt().coerceAtMost(cap)

                addHit(
                    context.defender,
                    Hit(context.attacker, damage, Hit.HitLook.REGULAR_DAMAGE),
                    delay = context.combat.getHitDelay()
                )
            }

            context.attacker.applyHit(
                Hit(
                    context.attacker,
                    (context.attacker.hitpoints * 0.1).toInt(),
                    Hit.HitLook.REGULAR_DAMAGE
                )
            )

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

            val boost = if (guaranteed) 1.26 else 1.15
            val hit = context.registerDamage(
                combatType = CombatType.RANGED,
                damageMultiplier = boost
            )

            context.defender.gfx("graphic.diamond_bolt_effect", 0)
            context.defender.playSound("sound.diamond_bolt_effect", 1)

            context.combat.delayHits(
                PendingHit(hit, context.defender, context.combat.getHitDelay())
            )

            true
        }
    )

    val OPAL = SpecialEffect(
        execute = { context ->
            val guaranteed = context.guaranteedBoltEffect
            val chance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 12 else 6

            if (!Utils.roll(chance, 100) && !guaranteed)
                return@SpecialEffect false
            val hit = context.rollRanged(dmg = if (guaranteed) 2.0 else 1.0)
            if (hit.damage == 0) return@SpecialEffect false

            context.defender.gfx("graphic.opal_bolt_effect", 0)
            context.defender.playSound("sound.opal_bolt_effect", 1)

            val rangedLevel = context.attacker.skills.getLevel(Skills.RANGE)
            val extra = floor(rangedLevel * 0.10).toInt() * 10

            hit.damage += extra

            context.combat.delayHits(
                PendingHit(hit, context.defender, context.combat.getHitDelay())
            )

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

            val hit = context.rollRanged(acc = if (guaranteed) 2.0 else 1.0)
            if (hit.damage == 0) return@SpecialEffect false

            context.defender.gfx("graphic.dragonstone_bolt_effect", 0)
            context.defender.playSound("sound.dragonstone_bolt_effect", 1)

            // 20% normally, 22% with Zaryte crossbow passive
            val boost =
                if (context.weaponId == Item.getId("item.zaryte_crossbow")) 0.22
                else 0.20

            val rangedLevel = context.attacker.skills.getLevel(Skills.RANGE)

            val extra =
                floor(rangedLevel * boost).toInt() * 10

            hit.damage = min(hit.damage + extra, context.defender.hitpoints)

            context.combat.delayHits(
                PendingHit(hit, context.defender, context.combat.getHitDelay())
            )

            true
        }
    )


    val ONYX = SpecialEffect(
        execute = { context ->
            val guaranteed = context.guaranteedBoltEffect
            val extraChance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 2 else 0
            val chance = if (context.defender is NPC) 11 else 10

            if (!Utils.roll(chance + extraChance, 100) && !guaranteed)
                return@SpecialEffect false

            context.hits {
                val hit = ranged(
                    accuracyMultiplier = if (guaranteed) 2.0 else 1.0,
                    damageMultiplier = if (guaranteed) 1.32 else 1.20,
                    delay = context.combat.getHitDelay()
                )

                if (hit.damage == 0) return@hits

                context.defender.gfx("graphic.onyx_bolt_effect", 0)
                context.defender.playSound("sound.onyx_bolt_effect", 1)

                val heal = (hit.damage * 0.25).toInt()
                context.attacker.applyHeal(
                    Hit(context.attacker, heal, Hit.HitLook.HEALED_DAMAGE)
                )
            }

            true
        }
    )


}
