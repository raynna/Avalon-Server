package com.rs.kotlin.game.world.projectile

object ProjectileRegistry {
    private val projectileTypes = mutableMapOf<Projectile, ProjectileType>()

    init {
        register(Projectile.ARROW, ProjectileType(startHeight = 36, endHeight = 28, delay = 42, duration = 42, arc = 15))
        register(Projectile.SAP, ProjectileType(startHeight = 36, endHeight = 36, delay = 31, duration = 36, displacement = 100))
        register(Projectile.LEECH, ProjectileType(startHeight = 36, endHeight = 36, delay = 31, duration = 36, displacement = 100))
        register(Projectile.CANNON, ProjectileType(startHeight = 31, endHeight = 28, delay = 31, duration = 42, arc = 5))
        register(Projectile.ELEMENTAL_SPELL, ProjectileType(startHeight = 31, endHeight = 28, delay = 48, duration = 42, arc = 10))
    }

    fun register(projectile: Projectile, type: ProjectileType) {
        projectileTypes[projectile] = type
    }

    fun get(projectile: Projectile): ProjectileType? = projectileTypes[projectile]
}

