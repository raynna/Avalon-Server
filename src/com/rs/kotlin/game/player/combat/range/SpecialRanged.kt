package com.rs.kotlin.game.player.combat.range

import com.rs.kotlin.game.player.combat.WeaponStyle

object SpecialRanged : RangeData() {
    override val weapons = listOf(
        RangedWeapon(
            itemId = 861,
            name = "Magic shortbow",
            weaponStyle = WeaponStyle.SHORTBOW,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 1074,
            projectileId = 249,
            ammoType = AmmoType.ARROW
        ),
        RangedWeapon(
            itemId = 11235,
            name = "Dark bow",
            weaponStyle = WeaponStyle.LONGBOW,
            attackSpeed = 9,
            attackRange = 8,
            animationId = 426,
            ammoType = AmmoType.ARROW
        ),
        // Crossbows
        RangedWeapon(
            itemId = 9185,
            name = "Rune crossbow",
            weaponStyle = WeaponStyle.CROSSBOW,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 423,
            ammoType = AmmoType.BOLT
        ),
    )

    override val ammunition = listOf(
        RangedAmmo(
            itemId = 11212,
            name = "Dragon arrow",
            ammoType =  AmmoType.ARROW,
            levelRequired = 60,
            projectileId = 1111,
        ),
        // Special bolts
        RangedAmmo(
            itemId = 9242,
            name = "Ruby bolts (e)",
            ammoType =  AmmoType.BOLT,
            levelRequired = 50,
            projectileId = 27
        ),
        RangedAmmo(
            itemId = 9244,
            name = "Dragonstone bolts (e)",
            ammoType =  AmmoType.BOLT,
            levelRequired = 60,
            projectileId = 27
        ),
        RangedAmmo(
            itemId = 9245,
            name = "Onyx bolts (e)",
            ammoType =  AmmoType.BOLT,
            levelRequired = 70,
            projectileId = 27,
        ),
        // Chinchompas
        RangedAmmo(
            itemId = 11959,
            name = "Red chinchompa",
            ammoType =  AmmoType.CHINCHOMPA,
            levelRequired = 55,
            projectileId = 908,
        )
    )
}