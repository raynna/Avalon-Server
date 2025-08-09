package com.rs.kotlin.game.player.combat.range

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager


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
 */

object StandardRanged : RangeData() {
    override val weapons = listOf(
        RangedWeapon(
            itemId = 841,
            name = "Shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.IRON_ARROW
        ),
        RangedWeapon(
            itemId = 843,
            name = "Oak shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.STEEL_ARROW
        ),
        RangedWeapon(
            itemId = 839,
            name = "Longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackSpeed = 6,
            attackRange = 10,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.IRON_ARROW
        ),
        RangedWeapon(
            itemId = 845,
            name = "Oak longbow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackSpeed = 6,
            attackRange = 10,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.STEEL_ARROW
        ),
        RangedWeapon(
            itemId = 9174,
            name = "Bronze crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.BRONZE_BOLT
        ),
        RangedWeapon(
            itemId = 9177,
            name = "Iron crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.IRON_BOLT
        ),
        RangedWeapon(
            itemId = 9177,
            name = "Iron crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            maxAmmoTier = AmmoTier.IRON_BOLT
        ),
        RangedWeapon(
            itemId = 8880,
            name = "Dorgeshuun c'bow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 4230,
            ammoType = AmmoType.BOLT,
            allowedAmmoIds = setOf(8882)
        ),
        RangedWeapon(
            itemId = 19143,
            name = "Saradomin bow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.RUNE_ARROW,
            specialAttack = SpecialAttack(
                energyCost = 55,
                damageMultiplier = 1.5,
                execute = { context ->
                    val special = context.weapon.specialAttack!!
                    context.attacker.animate(Animation(426))
                    ProjectileManager.send(Projectile.ARROW, 249, context.attacker, context.defender)
                    val hit = context.combat.registerHit(
                        context.attacker,
                        context.defender,
                        CombatType.RANGED,
                        context.attackStyle,
                        context.weapon,
                        accuracyMultiplier = special.accuracyMultiplier,
                        damageMultiplier = special.damageMultiplier
                    )
                    if (hit.damage > 0) {
                        context.attacker.applyHeal(hit.copyWithDamage(hit.damage))
                    }
                    context.combat.delayHits(
                        PendingHit(hit, context.combat.getHitDelay())
                    )
                }
            )
        ),
        RangedWeapon(
            itemId = 19146,
            name = "Guthix bow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.RUNE_ARROW,
            specialAttack = SpecialAttack(
                energyCost = 55,
                damageMultiplier = 1.5,
                execute = { context ->
                    val special = context.weapon.specialAttack!!
                    context.attacker.animate(Animation(426))
                    ProjectileManager.send(Projectile.ARROW, 249, context.attacker, context.defender)
                    val hit = context.combat.registerHit(
                        context.attacker,
                        context.defender,
                        CombatType.RANGED,
                        context.attackStyle,
                        context.weapon,
                        accuracyMultiplier = special.accuracyMultiplier,
                        damageMultiplier = special.damageMultiplier
                    )
                    if (hit.damage > 0) {
                        context.attacker.applyHeal(hit.copyWithDamage(hit.damage))
                    }
                    context.combat.delayHits(
                        PendingHit(hit, context.combat.getHitDelay())
                    )
                }
            )
        ),
        RangedWeapon(
            itemId = 19149,
            name = "Zamorak bow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.RUNE_ARROW,
            specialAttack = SpecialAttack(
                energyCost = 55,
                damageMultiplier = 1.5,
                execute = { context ->
                    val special = context.weapon.specialAttack!!
                    context.attacker.animate(Animation(426))
                    ProjectileManager.send(Projectile.ARROW, 249, context.attacker, context.defender)
                    val hit = context.combat.registerHit(
                        context.attacker,
                        context.defender,
                        CombatType.RANGED,
                        context.attackStyle,
                        context.weapon,
                        accuracyMultiplier = special.accuracyMultiplier,
                        damageMultiplier = special.damageMultiplier
                    )
                    if (hit.damage > 0) {
                        context.attacker.applyHeal(hit.copyWithDamage(hit.damage))
                    }
                    context.combat.delayHits(
                        PendingHit(hit, context.combat.getHitDelay())
                    )
                }
            )
        ),
        RangedWeapon(
            itemId = 861,
            name = "Magic shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW,
            maxAmmoTier = AmmoTier.RUNE_ARROW,
            specialAttack = SpecialAttack(
                energyCost = 55,
                accuracyMultiplier = 1.0,
                damageMultiplier = 1.0,
                execute = { context ->
                    val special = context.weapon.specialAttack!!
                    context.attacker.animate(Animation(1074))
                    ProjectileManager.sendWithDelay(Projectile.ARROW, 249, context.attacker, context.defender, -5)
                    ProjectileManager.sendWithDelay(Projectile.ARROW, 249, context.attacker, context.defender, -25)
                    fun registerSpecialHit() = context.combat.registerHit(
                        context.attacker,
                        context.defender,
                        CombatType.RANGED,
                        context.attackStyle,
                        context.weapon,
                        accuracyMultiplier = special.accuracyMultiplier,
                        damageMultiplier = special.damageMultiplier
                    )
                    context.combat.delayHits(
                        PendingHit(registerSpecialHit(), context.combat.getHitDelay() - 1),
                        PendingHit(registerSpecialHit(), context.combat.getHitDelay())
                    )
                }
            )
        ),

        //knifes
        RangedWeapon(
            itemId = 864,
            name = "Bronze knife",
            weaponStyle = WeaponStyle.THROWING,
            attackSpeed = 3,
            attackRange = 4,
            projectileId = 212,
            animationId = 9057,
            ammoType = AmmoType.THROWING
        ),
        RangedWeapon(
            itemId = 870,
            name = "Bronze knife (p)",
            weaponStyle = WeaponStyle.THROWING,
            attackSpeed = 3,
            attackRange = 4,
            projectileId = 212,
            poisonSeverity = 20,
            animationId = 9057,
            ammoType = AmmoType.THROWING
        ),

        //darts
        RangedWeapon(
            itemId = 806,
            name = "Bronze dart",
            weaponStyle = WeaponStyle.THROWING,
            attackSpeed = 3,
            attackRange = 3,
            projectileId = 226,
            animationId = 582,
            ammoType = AmmoType.DART
        )
    )

    override val ammunition = listOf(
        RangedAmmo(
            itemId = 882,
            name = "Bronze arrow",
            ammoTier = AmmoTier.BRONZE_ARROW,
            levelRequired = 1,
            projectileId = 10,
            startGfx = Graphics(19, 100)
        ),
        RangedAmmo(
            itemId = 884,
            name = "Iron arrow",
            ammoTier = AmmoTier.IRON_ARROW,
            levelRequired = 1,
            projectileId = 9,
            startGfx = Graphics(18, 100),
        ),
        RangedAmmo(
            itemId = 886,
            name = "Steel arrow",
            ammoTier = AmmoTier.STEEL_ARROW,
            levelRequired = 1,
            projectileId = 11,
            startGfx = Graphics(20, 100),
        ),
        RangedAmmo(
            itemId = 888,
            name = "Mithril arrow",
            ammoTier = AmmoTier.MITHRIL_ARROW,
            levelRequired = 1,
            projectileId = 12,
            startGfx = Graphics(21, 100),
        ),
        RangedAmmo(
            itemId = 890,
            name = "Adamant arrow",
            ammoTier = AmmoTier.ADAMANT_ARROW,
            levelRequired = 1,
            projectileId = 13,
            startGfx = Graphics(22, 100),
        ),
        RangedAmmo(
            itemId = 892,
            name = "Rune arrow",
            ammoTier = AmmoTier.RUNE_ARROW,
            levelRequired = 1,
            projectileId = 15,
            startGfx = Graphics(24, 100),
        ),
        RangedAmmo(
            itemId = 19157,
            name = "Guthix arrow",
            ammoTier = AmmoTier.RUNE_ARROW,
            levelRequired = 1,
            projectileId = 98,
            startGfx = Graphics(95, 100),
            specialEffect = SpecialEffect(
                chance = 10,
                execute = { context ->
                    val chance = if (context.weapon.itemId == 19146) 5 else 10
                    context.ammo?.endGfx = null//TODO not having to reset this everytime
                    if (Utils.roll(1, chance)) {
                        val hit = context.combat.registerHit(
                            attacker = context.attacker,
                            defender = context.defender,
                            attackStyle = context.attackStyle,
                            weapon = context.weapon,
                            combatType = CombatType.RANGED,
                            hitLook = Hit.HitLook.MAGIC_DAMAGE,
                            damageMultiplier = 0.2
                        )
                        if (hit.damage == 0) {
                            context.ammo?.endGfx = Graphics(85, 100);
                        } else {
                            context.ammo?.endGfx = Graphics(127);
                            hit.setCriticalMark()
                        }
                        context.combat.delayHits(
                            PendingHit(hit, context.combat.getHitDelay() + 1)
                        )
                    }
                }
            )
        ),
        RangedAmmo(
            itemId = 19152,
            name = "Saradomin arrow",
            ammoTier = AmmoTier.RUNE_ARROW,
            levelRequired = 1,
            projectileId = 99,
            startGfx = Graphics(96, 100),
            specialEffect = SpecialEffect(
                chance = 10,
                execute = { context ->
                    val chance = if (context.weapon.itemId == 19143) 5 else 10
                    context.ammo?.endGfx = null//TODO not having to reset this everytime
                    if (Utils.roll(1, chance)) {
                        val hit = context.combat.registerHit(
                            attacker = context.attacker,
                            defender = context.defender,
                            attackStyle = context.attackStyle,
                            weapon = context.weapon,
                            combatType = CombatType.RANGED,
                            hitLook = Hit.HitLook.MAGIC_DAMAGE,
                            damageMultiplier = 0.2
                        )
                        if (hit.damage == 0) {
                            context.ammo?.endGfx = Graphics(85, 100);
                        } else {
                            context.ammo?.endGfx = Graphics(128);
                            hit.setCriticalMark()
                        }
                        context.combat.delayHits(
                            PendingHit(hit, context.combat.getHitDelay() + 1)
                        )
                    }
                }
            )
        ),
        RangedAmmo(
            itemId = 19162,
            name = "Zamorak arrow",
            ammoTier = AmmoTier.RUNE_ARROW,
            levelRequired = 1,
            projectileId = 100,
            startGfx = Graphics(97, 100),
            specialEffect = SpecialEffect(
                chance = 10,
                execute = { context ->
                    val chance = if (context.weapon.itemId == 19149) 5 else 10
                    context.ammo?.endGfx = null//TODO not having to reset this everytime
                    if (Utils.roll(1, chance)) {
                        val hit = context.combat.registerHit(
                            attacker = context.attacker,
                            defender = context.defender,
                            attackStyle = context.attackStyle,
                            weapon = context.weapon,
                            combatType = CombatType.RANGED,
                            hitLook = Hit.HitLook.MAGIC_DAMAGE,
                            damageMultiplier = 0.2
                        )
                        if (hit.damage == 0) {
                            context.ammo?.endGfx = Graphics(85, 100);
                        } else {
                            context.ammo?.endGfx = Graphics(129);
                            hit.setCriticalMark()
                        }
                        context.combat.delayHits(
                            PendingHit(hit, context.combat.getHitDelay() + 1)
                        )
                    }
                }
            )
        ),
        // Bolts
        RangedAmmo(
            itemId = 877,
            name = "Bronze bolts",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.BRONZE_BOLT,
            levelRequired = 1,
            projectileId = 27,
        ),
        RangedAmmo(
            itemId = 9140,
            name = "Iron bolts",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.IRON_BOLT,
            levelRequired = 1,
            projectileId = 27,
        ),
        RangedAmmo(
            itemId = 9287,
            name = "Iron bolts (p)",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.IRON_BOLT,
            levelRequired = 1,
            poisonSeverity = 20,
            projectileId = 27,
        ),
        RangedAmmo(
            itemId = 9294,
            name = "Iron bolts (p+)",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.IRON_BOLT,
            levelRequired = 1,
            poisonSeverity = 25,
            projectileId = 27,
        ),
        RangedAmmo(
            itemId = 9301,
            name = "Iron bolts (p++)",
            ammoType = AmmoType.BOLT,
            ammoTier = AmmoTier.IRON_BOLT,
            levelRequired = 1,
            poisonSeverity = 30,
            projectileId = 27,
        ),
        RangedAmmo(
            itemId = 8882,
            name = "Bone bolts",
            ammoType = AmmoType.BOLT,
            levelRequired = 10,
            projectileId = 27,
        )
    )
}
