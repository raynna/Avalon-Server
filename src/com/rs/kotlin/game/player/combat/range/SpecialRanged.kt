package com.rs.kotlin.game.player.combat.range

import com.rs.kotlin.game.player.combat.WeaponStyle

object SpecialRanged : RangeUtilities(SPECIAL_ID) {
    override val weapons = listOf(
        RangedWeapon(
            itemId = 861,
            name = "Magic shortbow",
            weaponType = WeaponStyle.SHORTBOW,
            levelRequired = 50,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 1074,
            projectileId = 249,
            specialAttack = SpecialAttack(
                name = "Magic shortbow special",
                energyCost = 55,
                damageMultiplier = 1.0,
                accuracyMultiplier = 1.0,
                specialProjectileId = 249
            ),
            ammoType = AmmoType.ARROW
        ),
        RangedWeapon(
            itemId = 11235,
            name = "Dark bow",
            weaponType = WeaponStyle.LONGBOW,
            levelRequired = 60,
            attackSpeed = 9,
            attackRange = 8,
            animationId = 426,
            specialAttack = SpecialAttack(
                name = "Dark bow special",
                energyCost = 65,
                damageMultiplier = 1.5,
                accuracyMultiplier = 1.25,
            ),
            ammoType = AmmoType.ARROW
        ),
        // Crossbows
        RangedWeapon(
            itemId = 9185,
            name = "Rune crossbow",
            weaponType = WeaponStyle.CROSSBOW,
            levelRequired = 40,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 423,
            ammoType = AmmoType.BOLT
        ),
        // Chinchompas
        RangedWeapon(
            itemId = 11959,
            name = "Red chinchompa",
            weaponType = WeaponStyle.CHINCHOMPA,
            levelRequired = 55,
            attackSpeed = 4,
            attackRange = 3,
            animationId = 2779,
            projectileId = 908,
            ammoType = AmmoType.CHINCHOMPA
        )
    )

    override val ammunition = listOf(
        RangedAmmo(
            itemId = 11212,
            name = "Dragon arrow",
            ammoType =  AmmoType.ARROW,
            levelRequired = 60,
            damageBonus = 6,
            projectileId = 1111,
        ),
        // Special bolts
        RangedAmmo(
            itemId = 9242,
            name = "Ruby bolts (e)",
            ammoType =  AmmoType.BOLT,
            levelRequired = 50,
            damageBonus = 3,
            projectileId = 27,
            specialEffect = SpecialEffect(
                type = EffectType.DRAGONFIRE,
                chance = 0.1,
                damage = 20
            )
        ),
        RangedAmmo(
            itemId = 9244,
            name = "Dragonstone bolts (e)",
            ammoType =  AmmoType.BOLT,
            levelRequired = 60,
            damageBonus = 5,
            projectileId = 27,
            specialEffect = SpecialEffect(
                type = EffectType.DRAGONFIRE,
                chance = 0.2,
                damage = 30
            )
        ),
        RangedAmmo(
            itemId = 9245,
            name = "Onyx bolts (e)",
            ammoType =  AmmoType.BOLT,
            levelRequired = 70,
            damageBonus = 7,
            projectileId = 27,
        ),
        // Chinchompas
        RangedAmmo(
            itemId = 11959,
            name = "Red chinchompa",
            ammoType =  AmmoType.CHINCHOMPA,
            levelRequired = 55,
            damageBonus = 0,
            projectileId = 908,
        )
    )
}