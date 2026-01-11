package com.rs.kotlin.game.world.projectile

object ProjectileRegistry {
    private val projectileTypes = mutableMapOf<Projectile, ProjectileType>()

    init {
        register(Projectile.ARROW, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15, displacement = 11))
        register(Projectile.BOLT, ProjectileType(startHeight = 38, endHeight = 36, startTime = 41, arc = 5, displacement = 11))
        register(Projectile.DRAGON_ARROW, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15, displacement = 11))
        register(Projectile.BOLT, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15))
        register(Projectile.THROWING_KNIFE, ProjectileType(startHeight = 40, endHeight = 36, startTime = 32, arc = 15, displacement = 11))
        register(Projectile.DART, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15))
        register(Projectile.SAP, ProjectileType(startHeight = 40, endHeight = 36, startTime = 31, multiplier = 8, arc = 11))
        register(Projectile.LEECH, ProjectileType(startHeight = 40, endHeight = 36, startTime = 31, multiplier = 8, arc = 11))
        register(Projectile.SOULSPLIT, ProjectileType(startHeight = 10, endHeight = 10, startTime = 31, multiplier = 10, arc = 15))
        register(Projectile.CANNON, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15))
        register(Projectile.ELEMENTAL_SPELL, ProjectileType(startHeight = 43, endHeight = 31, startTime = 51, arc = 11, displacement = 64))
        register(Projectile.CRUMBLE_UNDEAD, ProjectileType(startHeight = 31, endHeight = 31, startTime = 46, arc = 11, displacement = 64))
        register(Projectile.STANDARD_MAGIC, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 11))
        register(Projectile.STANDARD_MAGIC_INSTANT, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 11))
        register(Projectile.MIASMIC_SPELL, ProjectileType(startHeight = 43, endHeight = 31, startTime = 51, arc = 11))
        register(Projectile.ANCIENT_SPELL, ProjectileType(startHeight = 43, endHeight = 31, startTime = 51, arc = 11))
        register(Projectile.BLOOD_SPELL, ProjectileType(startHeight = 28, endHeight = 0, startTime = 51, arc = 11))
        register(Projectile.ICE_SPELL, ProjectileType(startHeight = 28, endHeight = 0, startTime = 51, arc = 11))
        register(Projectile.SLAYER_DART, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15, displacement = 64))
        register(Projectile.IBAN_BLAST, ProjectileType(startHeight = 36, endHeight = 31, startTime = 60, arc = 15, displacement = 64))
        register(Projectile.STORM_OF_ARMADYL, ProjectileType(startHeight = 11, endHeight = 11, startTime = 41, multiplier = 8, arc = 0))
        register(Projectile.TELEPORT_BLOCK, ProjectileType(startHeight = 11, endHeight = 36, startTime = 41, arc = 15))
        register(Projectile.POLYPORE_STAFF, ProjectileType(startHeight = 40, endHeight = 36, startTime = 51, arc = 15, displacement = 96))
        register(Projectile.SAGAIE, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15))
        register(Projectile.SPREAD_ALL_SLOW, ProjectileType(startHeight = 36, endHeight = 36, startTime = 21, arc = 0, multiplier = 15))
        register(Projectile.CHAIN_ARROW, ProjectileType(startHeight = 40, endHeight = 36, startTime = 0, arc = 15, displacement = 11))

        //NPCS
        register(Projectile.DRAGONFIRE, ProjectileType(startHeight = 40, endHeight = 36, startTime = 41, arc = 15, displacement = 64))
    }

    private fun register(projectile: Projectile, type: ProjectileType) {
        projectileTypes[projectile] = type
    }

    fun get(projectile: Projectile): ProjectileType? = projectileTypes[projectile]
}

