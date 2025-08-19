package com.rs.kotlin.game.world.projectile

object ProjectileRegistry {
    private val projectileTypes = mutableMapOf<Projectile, ProjectileType>()

    init {
        register(Projectile.ARROW, ProjectileType(speed = 42, arc = 15))
        register(Projectile.DRAGON_ARROW, ProjectileType(speed = 60, arc = 15))
        register(Projectile.BOLT, ProjectileType(speed = 60, arc = 15))
        register(Projectile.THROWING_KNIFE, ProjectileType(speed = 60, arc = 15, displacement = 100))
        register(Projectile.SAP, ProjectileType(speed = 60, displacement = 100))
        register(Projectile.LEECH, ProjectileType(speed = 60, displacement = 100))
        register(Projectile.SOULSPLIT, ProjectileType(startHeight = 0, endHeight = 0, speed = 60, arc = 0))
        register(Projectile.CANNON, ProjectileType(speed = 60, arc = 5))
        register(Projectile.ELEMENTAL_SPELL, ProjectileType(speed = 60, arc = 10))
        register(Projectile.SLAYER_DART, ProjectileType(speed = 60, arc = 10))
        register(Projectile.IBAN_BLAST, ProjectileType(startHeight = 51, speed = 60, arc = 10))
        register(Projectile.STORM_OF_ARMADYL, ProjectileType(startHeight = 0, endHeight = 0, speed = 60, arc = 0))
        register(Projectile.TELEPORT_BLOCK, ProjectileType(startHeight = 21, endHeight = 28, speed = 60, arc = 10))
    }

    fun register(projectile: Projectile, type: ProjectileType) {
        projectileTypes[projectile] = type
    }

    fun get(projectile: Projectile): ProjectileType? = projectileTypes[projectile]
}

