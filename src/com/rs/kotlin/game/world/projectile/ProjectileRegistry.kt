package com.rs.kotlin.game.world.projectile

object ProjectileRegistry {
    private val projectileTypes = mutableMapOf<Projectile, ProjectileType>()

    init {
        register(Projectile.ARROW, ProjectileType(startHeight = 30, endHeight = 20, delay = 10, duration = 40, arc = 15))
        register(Projectile.SAP, ProjectileType(startHeight = 36, endHeight = 36, delay = 31, duration = 36, displacement = 100))
        register(Projectile.LEECH, ProjectileType(startHeight = 36, endHeight = 36, delay = 31, duration = 36, displacement = 100))
        register(Projectile.CANNON, ProjectileType(startHeight = 31, endHeight = 28, delay = 30, duration = 45, arc = 5))
    }

    fun register(projectile: Projectile, type: ProjectileType) {
        projectileTypes[projectile] = type
    }

    fun get(projectile: Projectile): ProjectileType? = projectileTypes[projectile]
}

