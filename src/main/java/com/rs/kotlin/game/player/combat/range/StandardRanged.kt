package com.rs.kotlin.game.player.combat.range

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.item.Item
import com.rs.java.game.npc.NPC
import com.rs.java.game.npc.combat.DragonFire
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.special.ChainMode
import com.rs.kotlin.game.player.combat.special.ChainSettings
import com.rs.kotlin.game.player.combat.special.*
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager
import javax.sound.midi.Soundbank
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min


/** Range Attack distances
 * Weapon Type	                Normal Range	Long Range
 * Salamander[1]	            1	            N/A
 * Darts	                    3	            5
 * Throwing knives and axes	    4	            6
 * Comp ogre bow	            5	            7
 * Toxic blowpipe	            5	            7
 * Hunter's spear	            5	            7
 * Tonalztics of ralos	        6	            8
 * Dorgeshuun crossbow	        6	            8
 * Venator bow	                6	            8
 * Eclipse atlatl	            6	            8
 * Mud pie	                    6	            8
 * Crossbows	                7	            9
 * Shortbows	                7	            9
 * Long-range crossbows[2]	    8	            10
 * Seercull	                    8	            10
 * Light and heavy ballista	    9	            10
 * Chinchompas	                9	            10
 * 3rd age bow	                9	            10
 * Craw's bow and Webweaver bow	9	            10
 * Longbows	                    10	            10
 * Ogre bow	                    10	            10
 * Composite bows	            10	            10
 * Crystal bow	                10	            10
 * Dark bow	                    10	            10
 * Scorching bow	            10	            10
 * Bow of faerdhinen	        10	            10
 * Twisted bow	                10	            10
 */


/** Ranged Attack Speeds
 * Ranged weapons
 * Weapon	            Base   	Rapid
 * Dart	                3 ticks	2 ticks
 * Throwing knife	    3 ticks 2 ticks
 * Chinchompa	        4 ticks 3 ticks
 * Shortbow	            4 ticks	3 ticks
 * Composite bow	    5 ticks	4 ticks
 * Throwing axe	        5 ticks	4 ticks
 * Longbow	            6 ticks	5 ticks
 * Crossbow	            6 ticks	5 ticks
 *
 *
 * Special ranged weapons
 * Weapon	            Base	Rapid
 * Toxic blowpipe (PvM)	3 ticks	2 ticks
 * Toxic blowpipe (PvP)	4 ticks	3 ticks
 * Craw's bow	        4 ticks	3 ticks
 * Eclipse atlatl	    4 ticks	3 ticks
 * Hunters' crossbow	4 ticks	3 ticks
 * Karil's crossbow	    4 ticks	3 ticks
 * Salamander	        4 ticks	N/A
 * Toktz-xil-ul	        4 ticks	3 ticks
 * Bow of faerdhinen	5 ticks	4 ticks
 * Crystal bow	        5 ticks	4 ticks
 * Hunter's spear	    6 ticks	5 ticks
 * Twisted bow	        6 ticks	5 ticks
 * Ballistae	        7 ticks	6 ticks
 * Tonalztics of ralos	7 ticks	6 ticks
 * Ogre bow	            8 ticks	7 ticks
 * Dark bow	            9 ticks	8 ticks
 */


/**
 * Projectiles
 * Bronze dart → 226
 * Iron dart → 227
 * Steel dart → 228
 * Mithril dart → 229
 * Adamant dart → 230
 * Rune dart → 231
 *
 * Bronze javelin → 200
 * Iron javelin → 201
 * Steel javelin → 202
 * Mithril javelin → 201 (note: same as iron)
 * Adamant javelin → 204
 * Rune javelin → 205
 *
 * Bronze knife → 212
 * Iron knife → 213
 * Steel knife → 214
 * Mithril knife → 216
 * Adamant knife → 217
 * Rune knife → 218
 * Black knife → 215
 *
 * Most bolts (rune, bronze, dragon, silver, etc.)	27
 * Bakriminel bolts	3023
 * Coral bolts	3172
 * Royal bolts	3173
 * obby rings = 442
 */

object StandardRanged : RangeData() {
    fun getDefaultWeapon(): RangedWeapon = UNARMED

    private val UNARMED = RangedWeapon(
        itemId = listOf(-1),
        name = "Unarmed",
        weaponStyle = WeaponStyle.UNARMED,
        ammoType = AmmoType.NONE
    )
    override val weapons = listOf(
        RangedWeapon(
            itemId = Item.getIds("item.twisted_bow"),
            name = "Twisted bow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 6,
            attackRange = 10,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.DRAGON_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.shortbow"),
            name = "Shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.IRON_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.oak_shortbow"),
            name = "Oak shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.STEEL_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.willow_shortbow"),
            name = "Willow shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.MITHRIL_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.maple_shortbow"),
            name = "Maple shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.ADAMANT_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.yew_shortbow"),
            name = "Oak shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.RUNE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.longbow"),
            name = "Longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackSpeed = 6,
            attackRange = 10,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.IRON_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.oak_longbow"),
            name = "Oak longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackSpeed = 6,
            attackRange = 10,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.STEEL_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.willow_longbow"),
            name = "Willow longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackSpeed = 6,
            attackRange = 10,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.MITHRIL_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.maple_longbow"),
            name = "Maple longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackSpeed = 6,
            attackRange = 10,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.ADAMANT_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.yew_longbow"),
            name = "Yew longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackSpeed = 6,
            attackRange = 10,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.RUNE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.magic_longbow"),
            name = "Magic longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackSpeed = 6,
            attackRange = 10,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.RUNE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds("item.bronze_crossbow"),
            name = "Bronze crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.BRONZE_BOLT
        ),
        RangedWeapon(
            itemId = Item.getIds("item.iron_crossbow"),
            name = "Iron crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.IRON_BOLT
        ),
        RangedWeapon(
            itemId = Item.getIds("item.steel_crossbow"),
            name = "Steel crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.STEEL_BOLT
        ),
        RangedWeapon(
            itemId = Item.getIds("item.black_crossbow"),
            name = "Black crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.BLACK_BOLT
        ),
        RangedWeapon(
            itemId = Item.getIds("item.mith_crossbow"),
            name = "Mithril crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.MITHRIL_BOLT
        ),
        RangedWeapon(
            itemId = Item.getIds("item.adamant_crossbow"),
            name = "Adamant crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.ADAMANT_BOLT
        ),
        RangedWeapon(
            itemId = Item.getIds("item.rune_crossbow"),
            name = "Rune crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.RUNE_BOLT
        ),
        RangedWeapon(
            itemId = Item.getIds("item.chaotic_crossbow", "item.chaotic_crossbow_broken"),
            name = "Chaotic crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.DRAGON_BOLT
        ),
        RangedWeapon(
            itemId = Item.getIds("item.dragon_crossbow", "item.dragon_hunter_crossbow"),
            name = "Dragon crossbows",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.DRAGON_BOLT
        ),
        RangedWeapon(
            itemId = Item.getIds("item.zaryte_crossbow"),
            name = "Zaryte crossbows",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.DRAGON_BOLT,
            special = SpecialAttack.Combat(
                energyCost = 75,
                accuracyMultiplier = 2.0,
                execute = { context ->

                    context.attacker.animate(Animation(4230))

                    ProjectileManager.send(
                        Projectile.BOLT, 328,
                        context.attacker,
                        context.defender
                    )

                    val ammoHasEffect = context.ammo?.specialEffect != null

                    val boltApplied = if (ammoHasEffect) {
                        context.combat.executeAmmoEffect(
                            context.copy(guaranteedBoltEffect = true)
                        )
                    } else false

                    if (!boltApplied) {
                        context.hits {
                            ranged(
                                accuracyMultiplier = 2.0,
                                damageMultiplier = 1.1,
                                delay = context.combat.getHitDelay()
                            )
                        }
                    }
                }
            )
        ),
        RangedWeapon(
            itemId = Item.getIds("item.dorgeshuun_c_bow"),
            name = "Dorgeshuun c'bow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            allowedAmmoIds = setOf(Item.getId("item.bone_bolts"))
        ),
        RangedWeapon(
            itemId = Item.getIds("item.sagaie"),
            name = "Sagaie",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 5,
            animationId = -1,
            ammoType = AmmoType.THROWING,
            effect = SpecialEffect(
                execute = { context ->
                    val attacker = context.attacker
                    val defender = context.defender
                    attacker.animate("animation.sagaie_attack")
                    ProjectileManager.send(Projectile.SAGAIE, "graphic.sagaie_projectile", attacker, defender)
                    val rangedHit = context.rollRanged()
                    context.hits {
                        if (rangedHit.damage > 0) {
                            val distance = Utils.getDistance(attacker, defender)
                            val boost = 20 * (min(5, distance))
                            val boostedHit = rangedHit.copyWithDamage(rangedHit.damage + boost)
                            nextHit(boostedHit, delay = context.combat.getHitDelay())
                        } else {
                            nextHit(Hit(attacker, 0, rangedHit.look), delay = context.combat.getHitDelay())
                        }
                    }
                    true
                }
            )
        ),
        RangedWeapon(
            itemId = Item.getIds("item.zaryte_bow", "item.zaryte_bow_degraded"),
            name = "Zaryte bow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 10,
            ammoType = AmmoType.NONE,
            effect = SpecialEffect(
                execute = { context ->
                    context.startChainAttack(
                        settings = ChainSettings(
                            projectile = Projectile.CHAIN_ARROW,
                            projectileId = Graphics.getGraphics("graphic.zaryte_bow_projectile"),
                            chainMode = ChainMode.RANDOM_NEARBY,
                            firstCombatType = CombatType.RANGED,
                            spreadCombatType = CombatType.RANGED,
                            damageMultiplier = 0.66,
                            damageScaleMode = DamageScaleMode.ABSOLUTE,
                        ),
                        projectile = Projectile.ARROW,
                        projectileId = Graphics.getGraphics("graphic.zaryte_bow_projectile"),
                        animationId = Animation.getId("animation.bow_attack"),
                        graphicsId = Graphics.getGraphics("graphic.zaryte_bow_start"),
                        maxTargets = 2,
                        bounceRange = 10
                    )
                    /*context.attacker.animate("animation.bow_attack")
                    context.attacker.gfx("graphic.zaryte_bow_start", 100)
                    ProjectileManager.send(Projectile.ARROW, "graphic.zaryte_bow_projectile", context.attacker, context.defender)
                    context.hits {
                        ranged(
                            delay = context.combat.getHitDelay()
                        )
                    }*/
                    true
                }
            )
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.bow_of_faerdhinen",
                "item.bow_of_faerdhinen_red",
                "item.bow_of_faerdhinen_white",
                "item.bow_of_faerdhinen_black",
                "item.bow_of_faerdhinen_purple",
                "item.bow_of_faerdhinen_green",
                "item.bow_of_faerdhinen_yellow",
                "item.bow_of_faerdhinen_blue",
            ),
            name = "Bow of faerdhinen",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 10,
            ammoType = AmmoType.NONE,
            effect = SpecialEffect(
                execute = { context ->
                    context.attacker.animate("animation.bow_attack")
                    context.attacker.gfx("graphic.ice_arrow_start", 100)
                    ProjectileManager.send(Projectile.ARROW, "graphic.ice_arrow_projectile", context.attacker, context.defender)
                    context.hits {
                        ranged(delay = context.combat.getHitDelay())
                    }
                    true
                }
            )
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.crystal_bow_full",
                "item.crystal_bow_9_10",
                "item.crystal_bow_8_10",
                "item.crystal_bow_7_10",
                "item.crystal_bow_6_10",
                "item.crystal_bow_5_10",
                "item.crystal_bow_4_10",
                "item.crystal_bow_3_10",
                "item.crystal_bow_2_10",
                "item.crystal_bow_1_10"
            ),
            name = "Crystal bow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 10,
            ammoType = AmmoType.NONE,
            effect = SpecialEffect(
                execute = { context ->
                    context.attacker.animate("animation.bow_attack")
                    context.attacker.gfx("graphic.crystal_bow_start", 100)
                    ProjectileManager.send(Projectile.ARROW, "graphic.crystal_bow_projectile", context.attacker, context.defender)
                    context.hits {
                        ranged(delay = context.combat.getHitDelay())
                    }
                    true
                }
            )
        ),
        RangedWeapon(
            itemId = Item.getIds("item.saradomin_bow"),
            name = "Saradomin bow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.RUNE_ARROW,
            special = SpecialAttack.Combat(
                energyCost = 55,
                damageMultiplier = 1.5,
                execute = { context ->
                    context.attacker.animate(Animation(426))
                    ProjectileManager.send(Projectile.ARROW, "graphic.crystal_bow_projectile", context.attacker, context.defender)
                    context.hits {
                        val rangedHit = ranged()
                        if (rangedHit.damage > 0) {
                            context.attacker.applyHeal(rangedHit.copyWithDamage(rangedHit.damage))
                        }
                    }
                }
            )
        ),
        RangedWeapon(
            itemId = Item.getIds("item.guthix_bow"),
            name = "Guthix bow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.RUNE_ARROW,
            special = SpecialAttack.Combat(
                energyCost = 55,
                damageMultiplier = 1.5,
                execute = { context ->
                    context.attacker.animate(Animation(426))
                    ProjectileManager.send(Projectile.ARROW, "graphic.crystal_bow_projectile", context.attacker, context.defender)
                    context.hits {
                        val rangedHit = ranged()
                        if (rangedHit.damage > 0) {
                            context.attacker.applyHeal(rangedHit.copyWithDamage(rangedHit.damage))
                        }
                    }
                }
            )
        ),
        RangedWeapon(
            itemId = Item.getIds("item.zamorak_bow"),
            name = "Zamorak bow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.RUNE_ARROW,
            special = SpecialAttack.Combat(
                energyCost = 55,
                damageMultiplier = 1.5,
                execute = { context ->
                    context.attacker.animate(Animation(426))
                    ProjectileManager.send(Projectile.ARROW, "graphic.crystal_bow_projectile", context.attacker, context.defender)
                    context.hits {
                        val rangedHit = ranged()
                        if (rangedHit.damage > 0) {
                            context.attacker.applyHeal(rangedHit.copyWithDamage(rangedHit.damage))
                        }
                    }
                }
            )
        ),
        RangedWeapon(
            itemId = Item.getIds("item.magic_shortbow"),
            name = "Magic shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.RUNE_ARROW,
            special = SpecialAttack.Combat(
                energyCost = 55,
                accuracyMultiplier = 1.0,
                damageMultiplier = 1.0,
                execute = { context ->
                    context.attacker.animate(Animation(1074))
                    context.attacker.gfx(Graphics(256, 100))
                    context.attacker.playSound(2545,  1)
                    ProjectileManager.send(
                        Projectile.ARROW,
                        249,
                        context.attacker,
                        context.defender,
                        startHeightOffset = 0,
                        arcOffset = 5,
                        speedAdjustment = -2,
                        startTimeOffset = 0,
                    )
                    ProjectileManager.send(
                        Projectile.ARROW,
                        249,
                        context.attacker,
                        context.defender,
                        startHeightOffset = 0,
                        arcOffset = 5,
                        speedAdjustment = -2,
                        startTimeOffset = 15,
                    )
                    context.hits {
                        val distance = Utils.getDistance(context.attacker, context.defender)
                        val (firstDelay, secondDelay) = context.combat.getDoubleHitDelays(distance)
                        ranged(delay = firstDelay)
                        ranged(delay = secondDelay)
                    }
                }
            )
        ),

        RangedWeapon(
            itemId = Item.getIds(
                "item.dark_bow", "item.dark_bow_lent",
                "item.dark_bow_blue", "item.dark_bow_yellow",
                "item.dark_bow_green", "item.dark_bow_white"
            ),
            name = "Dark bow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 10,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.DRAGON_ARROW,
            special = SpecialAttack.Combat(
                energyCost = 65,
                execute = { context ->
                    context.attacker.animate("animation.bow_attack")
                    val arrowProjectile = if (context.ammo?.ammoTier == AmmoTier.DRAGON_ARROW) 1099 else 1102
                    val endGraphic = if (context.ammo?.ammoTier == AmmoTier.DRAGON_ARROW) 1100 else 1103
                    val soundId = if (context.ammo?.ammoTier == AmmoTier.DRAGON_ARROW) 3733 else 3736
                    val hitSoundId = if (context.ammo?.ammoTier == AmmoTier.DRAGON_ARROW) 3731 else 3732
                    ProjectileManager.send(
                        Projectile.DRAGON_ARROW,
                        arrowProjectile,
                        context.attacker,
                        context.defender,
                        hitGraphic = Graphics(endGraphic, 100)
                    ) {
                        context.defender.playSound(hitSoundId,  1)
                    }
                    ProjectileManager.send(
                        Projectile.DRAGON_ARROW,
                        arrowProjectile,
                        context.attacker,
                        context.defender,
                        arcOffset = 15,
                        startTimeOffset = 5,
                        speedAdjustment = 5,
                        hitGraphic = Graphics(endGraphic, 100),
                    ) {
                        context.defender.playSound(hitSoundId,  10, 1)
                    }
                    context.attacker.playSound(soundId,  1)
                    context.attacker.playSound(soundId,  30,1)
                    context.hits {
                        val distance = Utils.getDistance(context.attacker, context.defender)
                        val (firstDelay, secondDelay) = context.combat.getDarkBowHitDelays(distance)
                        val minDamage = (if (context.ammo?.ammoTier == AmmoTier.DRAGON_ARROW) 80 else 50)
                        val multiplier = (if (context.ammo?.ammoTier == AmmoTier.DRAGON_ARROW) 1.5 else 1.3)

                        val firstHit = ranged(delay = firstDelay, damageMultiplier = multiplier)
                        val secondHit = ranged(delay = secondDelay, damageMultiplier = multiplier)

                        firstHit.damage = min(max(firstHit.damage, minDamage), 480)
                        secondHit.damage = min(max(secondHit.damage, minDamage), 480)
                    }
                }
            ),
            effect = SpecialEffect(
                execute = { context ->
                    context.attacker.animate("animation.bow_attack")
                    val startGfx = context.ammo?.doubleGfx
                    context.attacker.gfx(startGfx)
                    val projectile = context.ammo?.projectileId!!
                    ProjectileManager.send(
                        Projectile.DRAGON_ARROW,
                        projectile,
                        context.attacker,
                        context.defender
                    )
                    ProjectileManager.send(
                        Projectile.DRAGON_ARROW,
                        projectile,
                        context.attacker,
                        context.defender,
                        arcOffset = 10,
                        startTimeOffset = 5,
                        speedAdjustment = 5,
                    )
                    context.hits {
                        val distance = Utils.getDistance(context.attacker, context.defender)
                        val (firstDelay, secondDelay) = context.combat.getDarkBowHitDelays(distance)
                        ranged(delay = firstDelay)
                        ranged(delay = secondDelay)
                    }
                    true
                }
            )
        ),

        RangedWeapon(
            itemId = Item.getIds("item.hand_cannon"),
            name = "Hand cannon",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 8,//12156 block
            attackRange = 7,
            startGfx = Graphics("graphic.hand_cannon_fire"),
            projectileId = 2143,
            animationId = 12174,
            blockAnimationId = 12156,
            soundId = Rscm.sound("sound.hand_cannon_attack"),
            ammoType = AmmoType.CANNON,
            special = SpecialAttack.Combat(
                energyCost = 50,
                accuracyMultiplier = 2.0,
                damageMultiplier = 1.0,
                execute = { context ->
                    context.attacker.animate(Animation(12153))
                    context.attacker.gfx(Graphics(2138))
                    context.attacker.delayGfx(Graphics(2141), 2);
                    context.attacker.playSound(7206,  0,1)
                    context.attacker.playSound(7206,  60,1)
                    ProjectileManager.send(
                        Projectile.HAND_CANNON,
                        2143,
                        context.attacker,
                        context.defender,
                    )
                    ProjectileManager.send(
                        Projectile.HAND_CANNON,
                        2143,
                        context.attacker,
                        context.defender,
                        startTimeOffset = 25,
                    )
                    context.hits {
                        val distance = Utils.getDistance(context.attacker, context.defender)
                        val (firstDelay, secondDelay) = context.combat.getDoubleHitDelays(distance)
                        ranged(delay = firstDelay)
                        ranged(delay = secondDelay)
                    }
                }
            )
        ),

        RangedWeapon(
            itemId = Item.getIds("item.sling", "item.kayle_s_sling"),
            name = "Sling",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 4,
            projectileId = 32,
            animationId = 789,
            ammoType = AmmoType.NONE
        ),

        /**
         * Knives
         */
        RangedWeapon(
            itemId = Item.getIds(
                "item.bronze_knife", "item.bronze_knife_p",
                "item.bronze_knife_p+", "item.bronze_knife_p++"
            ),
            name = "Bronze knife",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 4,
            animationId = Rscm.animation("animation.knife_throw"),
            projectileId = Rscm.graphic("graphic.bronze_knife_projectile"),
            ammoType = AmmoType.THROWING
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.iron_knife", "item.iron_knife_p",
                "item.iron_knife_p_2", "item.iron_knife_p_3"
            ),
            name = "Iron knife",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 4,
            animationId = Rscm.animation("animation.knife_throw"),
            projectileId = Rscm.graphic("graphic.iron_knife_projectile"),
            ammoType = AmmoType.THROWING
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.steel_knife", "item.steel_knife_p",
                "item.steel_knife_p_2", "item.steel_knife_p_3"
            ),
            name = "Steel knife",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 4,
            animationId = Rscm.animation("animation.knife_throw"),
            projectileId = Rscm.graphic("graphic.steel_knife_projectile"),
            ammoType = AmmoType.THROWING
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.black_knife", "item.black_knife_p",
                "item.black_knife_p_2", "item.black_knife_p_3"
            ),
            name = "Black knife",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 4,
            animationId = Rscm.animation("animation.knife_throw"),
            projectileId = Rscm.graphic("graphic.black_knife_projectile"),
            ammoType = AmmoType.THROWING
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.mithril_knife", "item.mithril_knife_p",
                "item.mithril_knife_p_2", "item.mithril_knife_p_3"
            ),
            name = "Mithril knife",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 4,
            animationId = Rscm.animation("animation.knife_throw"),
            projectileId = Rscm.graphic("graphic.mithril_knife_projectile"),
            ammoType = AmmoType.THROWING
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.adamant_knife", "item.adamant_knife_p",
                "item.adamant_knife_p_2", "item.adamant_knife_p_3"
            ),
            name = "Adamant knife",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 4,
            animationId = Rscm.animation("animation.knife_throw"),
            projectileId = Rscm.graphic("graphic.adamant_knife_projectile"),
            ammoType = AmmoType.THROWING
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.rune_knife", "item.rune_knife_p",
                "item.rune_knife_p_2", "item.rune_knife_p_3"
            ),
            name = "Rune knife",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 4,
            animationId = Rscm.animation("animation.knife_throw"),
            projectileId = Rscm.graphic("graphic.rune_knife_projectile"),
            ammoType = AmmoType.THROWING
        ),


        /**
         * Darts
         */
        RangedWeapon(
            itemId = Item.getIds("item.bronze_dart", "item.bronze_dart_p", "item.bronze_dart_p_2", "item.bronze_dart_p_3"),
            name = "Bronze dart",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 3,
            animationId = Rscm.animation("animation.dart_throw"),
            projectileId = Rscm.graphic("graphic.bronze_dart_projectile"),
            startGfx = Graphics("graphic.bronze_dart_start", 100),
            ammoType = AmmoType.DART
        ),
        RangedWeapon(
            itemId = Item.getIds("item.iron_dart", "item.iron_dart_p", "item.iron_dart_p_2", "item.iron_dart_p_3"),
            name = "Iron dart",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 3,
            animationId = Rscm.animation("animation.dart_throw"),
            projectileId = Rscm.graphic("graphic.iron_dart_projectile"),
            startGfx = Graphics("graphic.iron_dart_start", 100),
            ammoType = AmmoType.DART
        ),
        RangedWeapon(
            itemId = Item.getIds("item.steel_dart", "item.steel_dart_p", "item.steel_dart_p_2", "item.steel_dart_p_3"),
            name = "Steel dart",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 3,
            animationId = Rscm.animation("animation.dart_throw"),
            projectileId = Rscm.graphic("graphic.steel_dart_projectile"),
            startGfx = Graphics("graphic.steel_dart_start", 100),
            ammoType = AmmoType.DART
        ),
        RangedWeapon(
            itemId = Item.getIds("item.black_dart", "item.black_dart_p", "item.black_dart_p_2", "item.black_dart_p_3"),
            name = "Black dart",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 3,
            animationId = Rscm.animation("animation.dart_throw"),
            projectileId = Rscm.graphic("graphic.black_dart_projectile"),
            startGfx = Graphics("graphic.mithril_dart_start", 100),//TODO FIND BLACK START ID
            ammoType = AmmoType.DART
        ),
        RangedWeapon(
            itemId = Item.getIds("item.mithril_dart", "item.mithril_dart_p", "item.mithril_dart_p_2", "item.mithril_dart_p_3"),
            name = "Mithril dart",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 3,
            animationId = Rscm.animation("animation.dart_throw"),
            projectileId = Rscm.graphic("graphic.mithril_dart_projectile"),
            startGfx = Graphics("graphic.mithril_dart_start", 100),
            ammoType = AmmoType.DART
        ),
        RangedWeapon(
            itemId = Item.getIds("item.adamant_dart", "item.adamant_dart_p", "item.adamant_dart_p_2", "item.adamant_dart_p_3"),
            name = "Adamant dart",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 3,
            animationId = Rscm.animation("animation.dart_throw"),
            projectileId = Rscm.graphic("graphic.adamant_dart_projectile"),
            startGfx = Graphics("graphic.adamant_dart_start", 100),
            ammoType = AmmoType.DART
        ),
        RangedWeapon(
            itemId = Item.getIds("item.rune_dart", "item.rune_dart_p", "item.rune_dart_p_2", "item.rune_dart_p_3"),
            name = "Rune dart",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 3,
            animationId = Rscm.animation("animation.dart_throw"),
            projectileId = Rscm.graphic("graphic.rune_dart_projectile"),
            startGfx = Graphics("graphic.rune_dart_start", 100),
            ammoType = AmmoType.DART
        ),
        RangedWeapon(
            itemId = Item.getIds("item.dragon_dart", "item.dragon_dart_p", "item.dragon_dart_p_2", "item.dragon_dart_p_3"),
            name = "Dragon dart",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 3,
            animationId = Rscm.animation("animation.dart_throw"),
            projectileId = Rscm.graphic("graphic.dragon_dart_projectile"),
            startGfx = Graphics("graphic.dragon_dart_start", 100),
            ammoType = AmmoType.DART
        ),


        RangedWeapon(
            itemId = Item.getIds("item.morrigan_s_throwing_axe"),
            name = "Morrigan's throwing axe",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 4,
            animationId = 10504,
            ammoType = AmmoType.MORRIGAN_THROWING,
            startGfx = Graphics("graphic.morrigans_throwing_axe_start", 0),
            projectileId = Rscm.graphic("graphic.morrigans_throwing_axe_projectile"),
            special = SpecialAttack.InstantRangeCombat(
                energyCost = 25,
                execute = { context ->
                    context.attacker.animate("animation.morrigans_throwing_axe_attack")
                    context.attacker.gfx("graphic.morrigans_throwing_axe_start")
                    val impact = ProjectileManager.send(Projectile.MORRIGAN_THROWING_AXE, "graphic.morrigans_throwing_axe_projectile", context.attacker, context.defender)
                    context.hits {
                        val rangedHit = ranged(delay = impact - 1)
                        rangedHit.damage = (rangedHit.damage * 0.6).toInt()
                    }
                }
            )
        ),
        RangedWeapon(
            itemId = Item.getIds("item.morrigan_s_javelin", "item.morrigan_s_javelin_p", "item.morrigan_s_javelin_p+", "item.morrigan_s_javelin_p++"),
            name = "Morrigan's Javelin",
            weaponStyle = WeaponStyle.THROWING,
            attackRange = 5,
            animationId = 10501,
            ammoType = AmmoType.JAVELIN,
            startGfx = Graphics("graphic.morrigans_javelin_start", 0),
            projectileId = Rscm.graphic("graphic.morrigans_javelin_projectile"),
            special = SpecialAttack.Combat(
                energyCost = 50,
                execute = { context ->
                    context.attacker.animate("animation.morrigans_javelin_attack")
                    context.attacker.gfx("graphic.morrigans_javelin_start")
                    val impact = ProjectileManager.send(Projectile.ARROW, "graphic.morrigans_javelin_projectile", context.attacker, context.defender)
                    context.hits {
                        val rangedHit = ranged(accuracyMultiplier = 1.5, delay = impact - 1)
                        if (rangedHit.damage > 0) {
                            context.applyBleed(baseHit = rangedHit, bleedPercent = .75, maxTickDamage =50, initialDelay = impact, tickInterval = 2)
                        }
                    }
                }
            )
        ),

        RangedWeapon(itemId = Item.getIds("item.toktz_xil_ul"),
            name = "Toktz_xil_ul",
            weaponStyle = WeaponStyle.THROWING,
            animationId = Rscm.animation("animation.toktz_xil_ul_attack"),
            projectileId = Rscm.graphic("graphic.toktz_xil_ul_projectile"),
            ammoType = AmmoType.THROWING
        ),
        /** Dungeoneering Range Weapons */
        RangedWeapon(
            itemId = Item.getIds(
                "item.tangle_gum_shortbow", "item.tangle_gum_shortbow_b"
            ),
            name = "Tanglegum shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.NOVITE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.tangle_gum_longbow", "item.tangle_gum_longbow_b"
            ),
            name = "Tanglegum longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.NOVITE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.seeping_elm_shortbow", "item.seeping_elm_shortbow_b"
            ),
            name = "Seeping elm shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.BATHUS_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.seeping_elm_longbow", "item.seeping_elm_longbow_b"
            ),
            name = "Seeping elm longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.BATHUS_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.blood_spindle_shortbow", "item.blood_spindle_shortbow_b"
            ),
            name = "Blood spindle shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.MARMAROS_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.blood_spindle_longbow", "item.blood_spindle_longbow_b"
            ),
            name = "Blood spindle longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.MARMAROS_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.utuku_shortbow", "item.utuku_shortbow_b"
            ),
            name = "Utuku shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.KRATONITE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.utuku_longbow", "item.utuku_longbow_b"
            ),
            name = "Utuku longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.KRATONITE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.spinebeam_shortbow", "item.spinebeam_shortbow_b"
            ),
            name = "Spinebeam shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.FRACTITE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.spinebeam_longbow", "item.spinebeam_longbow_b"
            ),
            name = "Spinebeam longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.FRACTITE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.bovistrangler_shortbow", "item.bovistrangler_shortbow_b"
            ),
            name = "Bovistrangler shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.ZEPHYRIUM_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.bovistrangler_longbow", "item.bovistrangler_longbow_b"
            ),
            name = "Bovistrangler longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.ZEPHYRIUM_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.thigat_shortbow", "item.thigat_shortbow_b"
            ),
            name = "Thigat shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.ARGONITE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.thigat_longbow", "item.thigat_longbow_b"
            ),
            name = "Thigat longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.ARGONITE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.thigat_longbow", "item.thigat_longbow_b"
            ),
            name = "Thigat longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.ARGONITE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.corpsethorn_shortbow", "item.corpsethorn_shortbow_b"
            ),
            name = "Corpsethorn shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.KATAGON_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.corpsethorn_longbow", "item.corpsethorn_longbow_b"
            ),
            name = "Corpsethorn longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.KATAGON_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.entgallow_shortbow", "item.entgallow_shortbow_b"
            ),
            name = "Entgallow shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.GORGONITE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.entgallow_longbow", "item.entgallow_longbow_b"
            ),
            name = "Entgallow longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.GORGONITE_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.grave_creeper_shortbow", "item.grave_creeper_shortbow_b"
            ),
            name = "Grave creeper shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.PROMETHIUM_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.grave_creeper_longbow", "item.grave_creeper_longbow_b"
            ),
            name = "Grave creeper longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.PROMETHIUM_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.hexhunter_bow", "item.hexhunter_bow_b"
            ),
            name = "Hexhunter bow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.SAGITTARIAN_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.sagittarian_shortbow", "item.sagittarian_shortbow_b"
            ),
            name = "Sagittarian shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.SAGITTARIAN_ARROW
        ),
        RangedWeapon(
            itemId = Item.getIds(
                "item.sagittarian_longbow", "item.sagittarian_longbow_b"
            ),
            name = "Sagittarian longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackRange = 9,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.SAGITTARIAN_ARROW
        ),
    )

    override val ammunition = listOf(
        RangedAmmo(
            itemId = Item.getIds("item.bronze_arrow"),
            name = "Bronze arrow",
            ammoTier = AmmoTier.BRONZE_ARROW,
            levelRequired = 1,
            projectileId = Rscm.lookup("graphic.bronze_arrow_projectile"),
            startGfx = Graphics("graphic.bronze_arrow_start", 100),
            doubleGfx = Graphics("graphic.double_bronze_arrow_start", 100)
        ),
        RangedAmmo(
            itemId = Item.getIds("item.iron_arrow"),
            name = "Iron arrow",
            ammoTier = AmmoTier.IRON_ARROW,
            levelRequired = 1,
            projectileId = Rscm.lookup("graphic.iron_arrow_projectile"),
            startGfx = Graphics("graphic.iron_arrow_start", 100),
            doubleGfx = Graphics("graphic.double_iron_arrow_start", 100)
        ),
        RangedAmmo(
            itemId = Item.getIds("item.steel_arrow"),
            name = "Steel arrow",
            ammoTier = AmmoTier.STEEL_ARROW,
            levelRequired = 1,
            projectileId = Rscm.lookup("graphic.steel_arrow_projectile"),
            startGfx = Graphics("graphic.steel_arrow_start", 100),
            doubleGfx = Graphics("graphic.double_steel_arrow_start", 100)
        ),
        RangedAmmo(
            itemId = Item.getIds("item.mithril_arrow"),
            name = "Mithril arrow",
            ammoTier = AmmoTier.MITHRIL_ARROW,
            levelRequired = 1,
            projectileId = Rscm.lookup("graphic.mithril_arrow_projectile"),
            startGfx = Graphics("graphic.mithril_arrow_start", 100),
            doubleGfx = Graphics("graphic.double_mithril_arrow_start", 100)
        ),
        RangedAmmo(
            itemId = Item.getIds("item.adamant_arrow"),
            name = "Adamant arrow",
            ammoTier = AmmoTier.ADAMANT_ARROW,
            levelRequired = 1,
            projectileId = Rscm.lookup("graphic.adamant_arrow_projectile"),
            startGfx = Graphics("graphic.adamant_arrow_start", 100),
            doubleGfx = Graphics("graphic.double_adamant_arrow_start", 100)
        ),
        RangedAmmo(
            itemId = Item.getIds("item.rune_arrow"),
            name = "Rune arrow",
            ammoTier = AmmoTier.RUNE_ARROW,
            levelRequired = 1,
            projectileId = Rscm.lookup("graphic.rune_arrow_projectile"),
            startGfx = Graphics("graphic.rune_arrow_start", 100),
            doubleGfx = Graphics("graphic.double_rune_arrow_start", 100)
        ),
        RangedAmmo(
            itemId = Item.getIds("item.amethyst_arrow"),
            name = "Amethyst arrow",
            ammoTier = AmmoTier.RUNE_ARROW,
            levelRequired = 1,
            projectileId = Rscm.lookup("graphic.steel_arrow_projectile"),
            startGfx = Graphics("graphic.steel_arrow_start", 100),
            doubleGfx = Graphics("graphic.double_steel_arrow_start", 100)
        ),
        RangedAmmo(
            itemId = Item.getIds("item.dragon_arrow"),
            name = "Dragon arrow",
            ammoTier = AmmoTier.DRAGON_ARROW,
            levelRequired = 1,
            projectileId = 1120,
            startGfx = Graphics(1116, 100),
            doubleGfx = Graphics(1111, 100),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.guthix_arrows"),
            name = "Guthix arrow",
            ammoTier = AmmoTier.RUNE_ARROW,
            levelRequired = 1,
            projectileId = Rscm.lookup("graphic.guthix_arrow_projectile"),
            doubleGfx = Graphics(124, 100),
            startGfx = Graphics(95, 100),
            specialEffect = SpecialEffect(
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
        ),
        RangedAmmo(
            itemId = Item.getIds("item.saradomin_arrows"),
            name = "Saradomin arrow",
            ammoTier = AmmoTier.RUNE_ARROW,
            levelRequired = 1,
            projectileId = Rscm.lookup("graphic.saradomin_arrow_projectile"),
            doubleGfx = Graphics(125, 100),
            startGfx = Graphics(96, 100),
            specialEffect = SpecialEffect(
                chance = 10,
                execute = { context ->
                    val chance = if (context.weaponId == 19143) 5 else 10
                    context.ammo?.endGfx = null // TODO: handle this elsewhere if possible

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
        ),
        RangedAmmo(
            itemId = Item.getIds("item.zamorak_arrows"),
            name = "Zamorak arrow",
            ammoTier = AmmoTier.RUNE_ARROW,
            levelRequired = 1,
            projectileId = Rscm.lookup("graphic.zamorak_arrow_projectile"),
            doubleGfx = Graphics("graphic.double_zamorak_arrow_start", 100),
            startGfx = Graphics("graphic.zamorak_arrow_start", 100),
            specialEffect = SpecialEffect(
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
        ),
        // Bolts
        RangedAmmo(
            itemId = Item.getIds("item.bronze_bolts", "item.bronze_bolts_p", "item.bronze_bolts_p+", "item.bronze_bolts_p++"),
            name = "Bronze bolts",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.BRONZE_BOLT,
            levelRequired = 1,
            startGfx = Graphics("graphic.bolt_start", 90),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.iron_bolts", "item.iron_bolts_p", "item.iron_bolts_p+", "item.iron_bolts_p++"),
            name = "Iron bolts",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.IRON_BOLT,
            levelRequired = 26,
            startGfx = Graphics("graphic.bolt_start", 90),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.steel_bolts", "item.steel_bolts_p", "item.steel_bolts_p_2", "item.steel_bolts_p_3"),
            name = "Steel bolts",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.STEEL_BOLT,
            levelRequired = 31,
            startGfx = Graphics("graphic.bolt_start", 90),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.black_bolts", "item.black_bolts_p", "item.black_bolts_p_2", "item.black_bolts_p_3"),
            name = "Black bolts",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.BLACK_BOLT,
            levelRequired = 33,
            startGfx = Graphics("graphic.bolt_start", 90),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.mithril_bolts", "item.mithril_bolts_p", "item.mithril_bolts_p_2", "item.mithril_bolts_p_3"),
            name = "Mithril bolts",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.MITHRIL_BOLT,
            levelRequired = 36,
            startGfx = Graphics("graphic.bolt_start", 90),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.adamant_bolts", "item.adamant_bolts_p", "item.adamant_bolts_p_2", "item.adamant_bolts_p_3",
                "item.ruby_bolts", "item.diamond_bolts"),
            name = "Adamant bolts",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.ADAMANT_BOLT,
            levelRequired = 46,
            startGfx = Graphics("graphic.bolt_start", 96),
            projectileId = Rscm.lookup("graphic.bolt_projectile")
        ),
        RangedAmmo(
            itemId = Item.getIds("item.ruby_bolts_e"),
            name = "Ruby bolts (e)",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.ADAMANT_BOLT,
            levelRequired = 46,
            startGfx = Graphics("graphic.bolt_start", 96),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
            specialEffect = SpecialEffect(
                execute = { context ->
                    val zaryteCbowSpec = context.guaranteedBoltEffect
                    val rawChance = if (context.defender is NPC) 6 else 11
                    val extraChance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 2 else 0
                    if (!Utils.roll(rawChance + extraChance, 100) && !zaryteCbowSpec)
                        return@SpecialEffect false
                    context.defender.gfx(754, 0)
                    context.defender.playSound(2912, 1)
                    context.hits {
                        val cap = if (zaryteCbowSpec) 1100 else 1000
                        val boost = if (zaryteCbowSpec) 0.22 else 0.2
                        val damage = (context.defender.hitpoints * boost).toInt().coerceAtMost(cap)
                        val hit = Hit(context.attacker, damage, HitLook.REGULAR_DAMAGE)
                        addHit(context.defender, hit = hit, delay = context.combat.getHitDelay())
                    }
                    context.attacker.applyHit(Hit(context.attacker, (context.attacker.hitpoints * 0.1).toInt(), HitLook.REGULAR_DAMAGE))
                    true
                }
            )
        ),
        RangedAmmo(
            itemId = Item.getIds("item.diamond_bolts_e"),
            name = "Diamond bolts (e)",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.ADAMANT_BOLT,
            levelRequired = 46,
            startGfx = Graphics("graphic.bolt_start", 96),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
            specialEffect = SpecialEffect(
                execute = { context ->
                    val zaryteCbowSpec = context.guaranteedBoltEffect
                    val rawChance = if (context.defender is NPC) 10 else 5
                    val extraChance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 2 else 0
                    if (!Utils.roll(rawChance + extraChance, 100) && !zaryteCbowSpec)
                        return@SpecialEffect false
                    val boost = if (zaryteCbowSpec) 1.26 else 1.15
                    val hit = context.registerDamage(combatType = CombatType.RANGED, damageMultiplier = boost)
                    context.defender.gfx(758, 0)
                    context.defender.playSound(2913, 1)
                    context.combat.delayHits(
                        PendingHit(hit, context.defender, context.combat.getHitDelay())
                    )
                    true
                }
            )
        ),
        RangedAmmo(
            itemId = Item.getIds("item.runite_bolts", "item.dragonstone_bolts", "item.onyx_bolts"),
            name = "Rune bolts",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.RUNE_BOLT,
            levelRequired = 61,
            startGfx = Graphics("graphic.bolt_start", 96),
            projectileId = Rscm.lookup("graphic.bolt_projectile")
        ),
        RangedAmmo(
            itemId = Item.getIds("item.dragonstone_bolts_e"),
            name = "Dragon bolts (e)",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.RUNE_BOLT,
            levelRequired = 61,
            startGfx = Graphics("graphic.bolt_start", 90),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
            specialEffect = SpecialEffect(
                execute = { context ->
                    val zaryteCbowSpec = context.guaranteedBoltEffect
                    val chance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 12 else 6
                    if (DragonFire.hasFireProtection(context.defender))
                        return@SpecialEffect false
                    if (!Utils.roll(chance, 100) && !zaryteCbowSpec)
                        return@SpecialEffect false

                    val hit = context.rollRanged(acc = if (zaryteCbowSpec) 2.0 else 1.0)
                    if (hit.damage == 0)
                        return@SpecialEffect false
                    context.defender.gfx(756, 0)
                    context.defender.playSound(2915, 1)
                    val boost = if (zaryteCbowSpec) 0.22 else 0.20
                    val extraDamage = floor(context.attacker.skills.getLevel(Skills.RANGE) * boost).toInt() * 10
                    hit.damage = min(hit.damage + extraDamage, context.defender.hitpoints);
                    context.combat.delayHits(
                        PendingHit(hit, context.defender, context.combat.getHitDelay())
                    )
                    true
                }
            )
        ),
        RangedAmmo(
            itemId = Item.getIds("item.onyx_bolts_e"),
            name = "Onyx bolts (e)",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.RUNE_BOLT,
            levelRequired = 61,
            startGfx = Graphics("graphic.bolt_start", 96),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
            specialEffect = SpecialEffect(
                execute = { context ->
                    val zaryteCbowSpec = context.guaranteedBoltEffect
                    val extraChance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 2 else 0
                    val chance = if (context.defender is NPC) 11 else 10
                    if (!Utils.roll(chance + extraChance, 100) && !zaryteCbowSpec)
                        return@SpecialEffect false
                    context.hits {
                        val hit = ranged(accuracyMultiplier = if (zaryteCbowSpec) 2.0 else 1.0, damageMultiplier = if (zaryteCbowSpec) 1.32 else 1.20, delay = context.combat.getHitDelay())
                        if (hit.damage == 0)
                            return@hits
                        context.defender.gfx(753, 0)
                        context.defender.playSound(2917, 1)
                        val heal = hit.damage * 0.25
                        context.attacker.applyHeal(Hit(context.attacker, heal.toInt(), HitLook.HEALED_DAMAGE));
                    }
                    true
                }
            )
        ),
        RangedAmmo(
            itemId = Item.getIds("item.dragon_bolts", "item.dragonstone_dragon_bolts", "item.onyx_dragon_bolts"),
            name = "Dragon bolts",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.DRAGON_BOLT,
            levelRequired = 64,
            startGfx = Graphics("graphic.bolt_start", 96),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.dragonstone_dragon_bolts_e"),
            name = "Dragonstone dragon bolts (e)",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.DRAGON_BOLT,
            levelRequired = 64,
            startGfx = Graphics(955, 90),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
            specialEffect = SpecialEffect(
                execute = { context ->

                    val zaryteCbowSpec = context.guaranteedBoltEffect
                    val chance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 12 else 6
                    if (DragonFire.hasFireProtection(context.defender))
                        return@SpecialEffect false
                    if (!Utils.roll(chance, 100) && !context.guaranteedBoltEffect)
                        return@SpecialEffect false
                    val hit = context.rollRanged(acc = if (zaryteCbowSpec) 2.0 else 1.0)
                    if (hit.damage == 0)
                        return@SpecialEffect false
                    context.defender.gfx(756, 0)
                    context.defender.playSound(2915, 1)
                    val boost = if (zaryteCbowSpec) 0.22 else 0.20
                    val extraDamage = floor(context.attacker.skills.getLevel(Skills.RANGE) * boost).toInt() * 10
                    hit.damage = min(hit.damage + extraDamage, context.defender.hitpoints);
                    context.combat.delayHits(
                        PendingHit(hit, context.defender, context.combat.getHitDelay())
                    )
                    true
                }
            )
        ),
        RangedAmmo(
            itemId = Item.getIds("item.onyx_dragon_bolts_e"),
            name = "Onyx dragon bolts (e)",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.DRAGON_BOLT,
            levelRequired = 64,
            startGfx = Graphics(955, 96),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
            specialEffect = SpecialEffect(
                execute = { context ->
                    val zaryteCbowSpec = context.guaranteedBoltEffect
                    val extraChance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 2 else 0
                    val chance = if (context.defender is NPC) 11 else 10
                    if (!Utils.roll(chance + extraChance, 100) && !zaryteCbowSpec)
                        return@SpecialEffect false
                    context.hits {
                        val hit = ranged(damageMultiplier = if (zaryteCbowSpec) 1.32 else 1.20, accuracyMultiplier = if (zaryteCbowSpec) 2.0 else 1.0, delay = context.combat.getHitDelay())
                        if (hit.damage == 0)
                            return@hits
                        context.defender.gfx(753, 0)
                        context.defender.playSound(2917, 1)
                        val heal = hit.damage * 0.25
                        context.attacker.applyHeal(Hit(context.attacker, heal.toInt(), HitLook.HEALED_DAMAGE));
                    }
                    true
                }
            )
        ),
        RangedAmmo(
            itemId = Item.getIds("item.diamond_dragon_bolts_e"),
            name = "Diamond dragon bolts (e)",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.DRAGON_BOLT,
            levelRequired = 64,
            startGfx = Graphics("graphic.bolt_start", 96),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
            specialEffect = SpecialEffect(
                execute = { context ->
                    val zaryteCbowSpec = context.guaranteedBoltEffect
                    val rawChance = if (context.defender is NPC) 10 else 5
                    val extraChance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 2 else 0
                    if (!Utils.roll(rawChance + extraChance, 100) && !zaryteCbowSpec)
                        return@SpecialEffect false
                    val boost = if (zaryteCbowSpec) 1.26 else 1.15
                    val hit = context.registerDamage(combatType = CombatType.RANGED, damageMultiplier = boost)
                    context.defender.gfx(758, 0)
                    context.defender.playSound(2913, 1)
                    context.combat.delayHits(
                        PendingHit(hit, context.defender, context.combat.getHitDelay())
                    )
                    true
                }
            )
        ),
        RangedAmmo(
            itemId = Item.getIds("item.ruby_dragon_bolts_e"),
            name = "Ruby dragon bolts (e)",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.DRAGON_BOLT,
            levelRequired = 64,
            startGfx = Graphics("graphic.bolt_start", 96),
            projectileId = Rscm.lookup("graphic.bolt_projectile"),
            specialEffect = SpecialEffect(
                execute = { context ->
                    val zaryteCbowSpec = context.guaranteedBoltEffect
                    val rawChance = if (context.defender is NPC) 6 else 11
                    val extraChance = if (context.weaponId == Item.getId("item.chaotic_crossbow")) 2 else 0
                    if (!Utils.roll(rawChance + extraChance, 100) && !zaryteCbowSpec)
                        return@SpecialEffect false
                    context.defender.gfx(754, 0)
                    context.defender.playSound(2912, 1)
                    context.hits {
                        val cap = if (zaryteCbowSpec) 1100 else 1000
                        val boost = if (zaryteCbowSpec) 0.22 else 0.2
                        val damage = (context.defender.hitpoints * boost).toInt().coerceAtMost(cap)
                        val hit = Hit(context.attacker, damage, HitLook.REGULAR_DAMAGE)
                        addHit(context.defender, hit = hit, delay = context.combat.getHitDelay())
                    }
                    context.attacker.applyHit(Hit(context.attacker, (context.attacker.hitpoints * 0.1).toInt(), HitLook.REGULAR_DAMAGE))
                    true
                }
            )
        ),
        RangedAmmo(
            itemId = Item.getIds("item.bone_bolts"),
            name = "Bone bolts",
            ammoType = AmmoType.BOLT,
            levelRequired = 28,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),

        RangedAmmo(
            itemId = Item.getIds("item.hand_cannon_shot"),
            name = "Hand cannon shots",
            levelRequired = 1,
            ammoType = AmmoType.CANNON,
            projectileId = Rscm.lookup("graphic.hand_cannon_projectile"),
            startGfx = Graphics("graphic.hand_cannon_fire", 0),
        ),

        /**Dungeoneering arrows*/
        RangedAmmo(
            itemId = Item.getIds("item.novite_arrows", "item.novite_arrows_b"),
            name = "Novite arrows",
            ammoType = AmmoType.ARROW,
            ammoTier = AmmoTier.NOVITE_ARROW,
            levelRequired = 1,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.bathus_arrows", "item.bathus_arrows_b"),
            name = "Bathus arrows",
            ammoType = AmmoType.ARROW,
            ammoTier = AmmoTier.BATHUS_ARROW,
            levelRequired = 10,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.marmaros_arrows", "item.marmaros_arrows_b"),
            name = "Marmaros arrows",
            ammoType = AmmoType.ARROW,
            ammoTier = AmmoTier.MARMAROS_ARROW,
            levelRequired = 20,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.kratonite_arrows", "item.kratonite_arrows_b"),
            name = "Kratonite arrows",
            ammoType = AmmoType.ARROW,
            ammoTier = AmmoTier.KRATONITE_ARROW,
            levelRequired = 30,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.fractite_arrows", "item.fractite_arrows_b"),
            name = "Fractite arrows",
            ammoType = AmmoType.ARROW,
            ammoTier = AmmoTier.FRACTITE_ARROW,
            levelRequired = 40,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.zephyrium_arrows", "item.zephyrium_arrows_b"),
            name = "Zephyrium arrows",
            ammoType = AmmoType.ARROW,
            ammoTier = AmmoTier.ZEPHYRIUM_ARROW,
            levelRequired = 50,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.argonite_arrows", "item.argonite_arrows_b"),
            name = "Argonite arrows",
            ammoType = AmmoType.ARROW,
            ammoTier = AmmoTier.ARGONITE_ARROW,
            levelRequired = 60,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.katagon_arrows", "item.katagon_arrows_b"),
            name = "Katagon arrows",
            ammoType = AmmoType.ARROW,
            ammoTier = AmmoTier.KATAGON_ARROW,
            levelRequired = 70,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.gorgonite_arrows", "item.gorgonite_arrows_b"),
            name = "Katagon arrows",
            ammoType = AmmoType.ARROW,
            ammoTier = AmmoTier.GORGONITE_ARROW,
            levelRequired = 80,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.promethium_arrows", "item.promethium_arrows_b"),
            name = "Promethium arrows",
            ammoType = AmmoType.ARROW,
            ammoTier = AmmoTier.PROMETHIUM_ARROW,
            levelRequired = 90,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),
        RangedAmmo(
            itemId = Item.getIds("item.sagittarian_arrows", "item.sagittarian_arrows_b"),
            name = "Sagittarian arrows",
            ammoType = AmmoType.ARROW,
            ammoTier = AmmoTier.SAGITTARIAN_ARROW,
            levelRequired = 99,
            projectileId = 696,
            startGfx = Graphics(697, 96),
        ),
    )
}
