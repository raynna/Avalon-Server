package com.rs.kotlin.game.player.combat.range

import com.rs.kotlin.game.player.combat.WeaponStyle


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

object StandardRanged : RangeUtilities(STANDARD_ID) {
    override val weapons = listOf(
        RangedWeapon(
            itemId = 841,
            name = "Shortbow",
            weaponType = WeaponStyle.SHORTBOW,
            levelRequired = 1,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW
        ),
        RangedWeapon(
            itemId = 843,
            name = "Oak shortbow",
            weaponType = WeaponStyle.SHORTBOW,
            levelRequired = 5,
            attackSpeed = 3,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW
        ),
        RangedWeapon(
            itemId = 839,
            name = "Longbow",
            weaponType = WeaponStyle.LONGBOW,
            levelRequired = 1,
            attackSpeed = 5,
            attackRange = 8,
            animationId = 426,
            ammoType = AmmoType.ARROW
        ),
        RangedWeapon(
            itemId = 845,
            name = "Oak longbow",
            weaponType = WeaponStyle.LONGBOW,
            levelRequired = 5,
            attackSpeed = 6,
            attackRange = 8,
            animationId = 426,
            ammoType = AmmoType.ARROW
        ),
        RangedWeapon(
            itemId = 9174,
            name = "Bronze crossbow",
            weaponType = WeaponStyle.CROSSBOW,
            levelRequired = 1,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 423,
            ammoType = AmmoType.BOLT
        ),
        RangedWeapon(
            itemId = 9177,
            name = "Iron crossbow",
            weaponType = WeaponStyle.CROSSBOW,
            levelRequired = 10,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 423,
            ammoType = AmmoType.BOLT
        )
    )

    override val ammunition = listOf(
        RangedAmmo(
            itemId = 882,
            name = "Bronze arrow",
            levelRequired = 1,
            damageBonus = 0,
            projectileId = 10,
            startGfx = 19
        ),
        RangedAmmo(
            itemId = 884,
            name = "Iron arrow",
            levelRequired = 1,
            damageBonus = 1,
            projectileId = 9,
            startGfx = 18
        ),
        // Bolts
        RangedAmmo(
            itemId = 877,
            name = "Bronze bolts",
            ammoType =  AmmoType.BOLT,
            levelRequired = 1,
            damageBonus = 0,
            projectileId = 27,
        ),
        RangedAmmo(
            itemId = 9140,
            name = "Iron bolts",
            ammoType =  AmmoType.BOLT,
            levelRequired = 10,
            damageBonus = 1,
            projectileId = 28,
        )
    )
}
