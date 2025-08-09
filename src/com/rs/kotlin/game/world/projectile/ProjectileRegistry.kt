package com.rs.kotlin.game.world.projectile

object ProjectileRegistry {
    private val projectileTypes = mutableMapOf<Projectile, ProjectileType>()

    init {
        register(Projectile.ARROW, ProjectileType(startHeight = 36, endHeight = 28, delay = 42, speed = 42, arc = 15))
        register(Projectile.SAP, ProjectileType(startHeight = 36, endHeight = 36, delay = 31, speed = 36, displacement = 100))
        register(Projectile.LEECH, ProjectileType(startHeight = 36, endHeight = 36, delay = 31, speed = 36, displacement = 100))
        register(Projectile.SOULSPLIT, ProjectileType(startHeight = 0, endHeight = 0, delay = 48, speed = 36, arc = 0))
        register(Projectile.CANNON, ProjectileType(startHeight = 31, endHeight = 28, delay = 31, speed = 42, arc = 5))
        register(Projectile.ELEMENTAL_SPELL, ProjectileType(startHeight = 31, endHeight = 28, delay = 48, speed = 42, arc = 10))
        register(Projectile.SLAYER_DART, ProjectileType(startHeight = 36, endHeight = 28, delay = 48, speed = 36, arc = 10))
        register(Projectile.IBAN_BLAST, ProjectileType(startHeight = 51, endHeight = 28, delay = 54, speed = 36, arc = 10))
        register(Projectile.STORM_OF_ARMADYL, ProjectileType(startHeight = 0, endHeight = 0, delay = 48, speed = 36, arc = 0))
        register(Projectile.TELEPORT_BLOCK, ProjectileType(startHeight = 21, endHeight = 28, delay = 48, speed = 36, arc = 10))
    }

    fun register(projectile: Projectile, type: ProjectileType) {
        projectileTypes[projectile] = type
    }

    fun get(projectile: Projectile): ProjectileType? = projectileTypes[projectile]
}

