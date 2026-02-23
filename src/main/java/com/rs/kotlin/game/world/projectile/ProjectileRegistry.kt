package com.rs.kotlin.game.world.projectile

import com.rs.kotlin.game.world.util.RollableInt

object ProjectileRegistry {
    private val projectileTypes = mutableMapOf<Projectile, ProjectileType>()

    init {
        register(Projectile.ARROW, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15, displacement = 11))
        register(Projectile.INSTANT_ARROW, ProjectileType(startHeight = 40, endHeight = 36, startTime = 0, arc = 15, displacement = 11))
        register(Projectile.BOLT, ProjectileType(startHeight = 38, endHeight = 36, startTime = 41, arc = 5, displacement = 11))
        register(Projectile.DRAGON_ARROW, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15, displacement = 11))
        register(Projectile.BOLT, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15))
        register(Projectile.THROWING_KNIFE, ProjectileType(startHeight = 40, endHeight = 36, startTime = 32, arc = 15, displacement = 11))
        register(Projectile.JAVELIN, ProjectileType(startHeight = 40, endHeight = 36, startTime = 32, arc = 15, displacement = 11))
        register(Projectile.THROWNAXE, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15, displacement = 11))
        register(Projectile.INSTANT_THROWNAXE, ProjectileType(startHeight = 40, endHeight = 36, startTime = 0, arc = 15, displacement = 11))
        register(
            Projectile.MORRIGAN_THROWING_AXE,
            ProjectileType(startHeight = 20, endHeight = 36, startTime = 42, arc = 15, displacement = 11),
        )
        register(Projectile.DART, ProjectileType(startHeight = 40, endHeight = 36, startTime = 32, arc = 15, displacement = 11))
        register(Projectile.SAP, ProjectileType(startHeight = 40, endHeight = 36, startTime = 31, multiplier = 8, arc = 11))
        register(Projectile.LEECH, ProjectileType(startHeight = 40, endHeight = 36, startTime = 31, multiplier = 8, arc = 11))
        register(Projectile.SOULSPLIT, ProjectileType(startHeight = 10, endHeight = 10, startTime = 31, multiplier = 10, arc = 15))
        register(Projectile.CANNON, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15))
        register(Projectile.ELEMENTAL_SPELL, ProjectileType(startHeight = 43, endHeight = 31, startTime = 51, arc = 11, displacement = 64))
        register(Projectile.CRUMBLE_UNDEAD, ProjectileType(startHeight = 31, endHeight = 31, startTime = 46, arc = 11, displacement = 64))
        register(Projectile.STANDARD_MAGIC, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 11))
        register(
            Projectile.STANDARD_MAGIC_FAST,
            ProjectileType(startHeight = 40, endHeight = 36, multiplier = 10, startTime = 21, arc = 11),
        )
        register(
            Projectile.STANDARD_MAGIC_INSTANT,
            ProjectileType(startHeight = 40, endHeight = 36, multiplier = 15, startTime = 0, arc = 11),
        )
        register(Projectile.MIASMIC_SPELL, ProjectileType(startHeight = 43, endHeight = 31, startTime = 51, arc = 11))
        register(Projectile.ANCIENT_SPELL, ProjectileType(startHeight = 43, endHeight = 31, startTime = 51, arc = 11))
        register(Projectile.BLOOD_SPELL, ProjectileType(startHeight = 28, endHeight = 0, startTime = 51, arc = 11))
        register(Projectile.ICE_SPELL, ProjectileType(startHeight = 28, endHeight = 0, startTime = 51, arc = 11))
        register(Projectile.SLAYER_DART, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15, displacement = 64))
        register(Projectile.IBAN_BLAST, ProjectileType(startHeight = 36, endHeight = 31, startTime = 60, arc = 15, displacement = 64))
        register(Projectile.STORM_OF_ARMADYL, ProjectileType(startHeight = 11, endHeight = 11, startTime = 41, multiplier = 8, arc = 0))
        register(Projectile.TELEPORT_BLOCK, ProjectileType(startHeight = 11, endHeight = 36, startTime = 41, arc = 15))
        register(Projectile.POLYPORE_STAFF, ProjectileType(startHeight = 40, endHeight = 36, startTime = 51, arc = 15, displacement = 96))
        register(Projectile.SAGAIE, ProjectileType(startHeight = 40, endHeight = 36, startTime = 32, arc = 15, displacement = 11))
        register(Projectile.SPREAD_ALL_SLOW, ProjectileType(startHeight = 36, endHeight = 36, startTime = 21, arc = 0, multiplier = 15))
        register(Projectile.CHAIN_ARROW, ProjectileType(startHeight = 40, endHeight = 36, startTime = 0, arc = 15, displacement = 11))
        register(Projectile.HAND_CANNON, ProjectileType(startHeight = 25, endHeight = 25, startTime = 30, arc = 0, displacement = 31))
        register(
            Projectile.ICE_BARRAGE,
            ProjectileType(
                startHeight = 43,
                endHeight = 0,
                startTime = 51,
                arc = 16,
                displacement = 64,
                multiplier = 10,
                lengthAdjustment = -5,
            ),
        )

        // NPCS
        register(Projectile.DRAGONFIRE, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15, displacement = 128))
        register(
            Projectile.CHAOS_ELEMENTAL,
            ProjectileType(startHeight = 40, endHeight = 36, startTime = 31, multiplier = 10, arc = 15, displacement = 0),
        )
        register(
            Projectile.GENERAL_GRAARDOR,
            ProjectileType(
                startHeight = 0,
                endHeight = 0,
                startTime = 31,
                multiplier = 5,
                arc = RollableInt.Range(10, 25),
                displacement = 50,
                lengthAdjustment = 34,
            ),
        )
        register(
            Projectile.KRIL_TSUTSAROTH,
            ProjectileType(
                startHeight = 0,
                endHeight = 0,
                startTime = 31,
                multiplier = 5,
                arc = RollableInt.Range(10, 25),
                displacement = 50,
                lengthAdjustment = 34,
            ),
        )
        register(
            Projectile.KREE_ARRA,
            ProjectileType(
                startHeight = 0,
                endHeight = 0,
                startTime = 31,
                multiplier = 5,
                arc = RollableInt.Range(10, 25),
                displacement = 50,
                lengthAdjustment = 34,
            ),
        )
        register(
            Projectile.COMMANDER_ZILYANA,
            ProjectileType(
                startHeight = 0,
                endHeight = 0,
                startTime = 31,
                multiplier = 5,
                arc = RollableInt.Range(10, 25),
                displacement = 50,
                lengthAdjustment = 34,
            ),
        )
        register(
            Projectile.CORPOREAL_BEAST_ATTACK,
            ProjectileType(
                startHeight = 40,
                endHeight = 36,
                startTime = 11,
                multiplier = 7,
                arc = RollableInt.Range(10, 25),
                displacement = 100,
                lengthAdjustment = 34,
            ),
        )
        register(
            Projectile.CORPOREAL_BEAST_AOE,
            ProjectileType(
                startHeight = 0,
                endHeight = 0,
                startTime = 0,
                multiplier = 10,
                arc = RollableInt.Range(10, 25),
                displacement = 0,
                lengthAdjustment = 34,
            ),
        )
    }

    private fun register(
        projectile: Projectile,
        type: ProjectileType,
    ) {
        projectileTypes[projectile] = type
    }

    fun get(projectile: Projectile): ProjectileType? = projectileTypes[projectile]
}
