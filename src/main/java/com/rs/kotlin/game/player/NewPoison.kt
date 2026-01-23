package com.rs.kotlin.game.player

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.Keys
import com.rs.java.game.player.Player
import com.rs.java.game.player.TickManager
import com.rs.java.utils.Utils
import kotlin.math.floor

class NewPoison(@Transient private var entity: Entity) {

    @Transient
    private var poisonedBy: Entity? = null

    fun setEntity(entity: Entity) {
        this.entity = entity
    }

    companion object {
        fun getPoisonSeverity(name: String): Int {
            return when {
                name.contains("(p++)", ignoreCase = true) -> 30
                name.contains("(p+)", ignoreCase = true)  -> 25
                name.contains("(p)", ignoreCase = true)   -> 20
                else -> -1
            }
        }

        val POISON_TICKS = Keys.IntKey.POISON_TICKS
        val POISON_SEVERITY = Keys.IntKey.POISON_SEVERITY
        val POISON_IMMUNE = Keys.IntKey.POISON_IMMUNE_TICKS
    }

    enum class WeaponType {
        MELEE, RANGED, SMOKE_SPELL, EMERALD_BOLT
    }


    /**
     * Attempt to apply poison based on weapon type and base severity.
     */
    fun roll(poisonedBy: Entity, weaponType: WeaponType, baseSeverity: Int) {
        if (isPoisoned()) return
        if (entity is Player && (this.entity as Player).tickTimers[POISON_IMMUNE]?.let { it > 0 } == true) return

        val chance = when (weaponType) {
            WeaponType.MELEE -> 1.0 / 4.0
            WeaponType.RANGED -> 1.0 / 8.0
            WeaponType.SMOKE_SPELL, WeaponType.EMERALD_BOLT -> 1.0
        }
        val random = Utils.randomDouble()
        if (random > chance) return
        val severity = when (weaponType) {
            WeaponType.RANGED -> baseSeverity - 14
            else -> baseSeverity
        }.coerceAtLeast(1)
        this.poisonedBy = poisonedBy
        startPoison(severity)
    }

    /**
     * Starts poison with given severity value.
     */
    fun startPoison(severity: Int) {
        if (entity is Player) {
            if (isPoisoned()) return
            if (entity.tickManager.isActive(TickManager.TickKeys.POISON_IMMUNE_TICKS))
                return
            (entity as Player).packets.sendGameMessage("You are now poisoned.")
        }
        entity.set(POISON_SEVERITY, severity)
        entity.tickTimers[POISON_TICKS] = 30
        refresh()
    }

    /**
     * Called every game tick from main loop.
     */
    fun processPoison() {
        if (entity.isDead) {
            return
        }

        val ticks = entity.tickTimers[POISON_TICKS] ?: 0
        if (ticks > 0) {
            entity.tickTimers[POISON_TICKS] = ticks - 1
            return
        }
        val severity = entity.get(POISON_SEVERITY)
        if (severity <= 0) {
            reset()
            return
        }

        if (entity is Player && (entity as Player).interfaceManager.containsScreenInter()) {
            entity.tickTimers[POISON_TICKS] = 1
            return
        }
        val damage = (severity + 4) / 5.toDouble()
        var heal = false

        if (entity is Player) {
            val player = entity as Player
            if (player.auraManager.hasPoisonPurge() || player.healMode) {
                heal = true
            }
        }
        entity.applyHit(Hit(this.poisonedBy, floor(damage * 10).toInt(), if (heal) Hit.HitLook.HEALED_DAMAGE else Hit.HitLook.POISON_DAMAGE))
        entity.set(POISON_SEVERITY, severity - 2)
        entity.tickTimers[POISON_TICKS] = 30
        refresh()
    }

    fun reset() {
        entity.clear(POISON_SEVERITY)
        entity.tickTimers.remove(POISON_TICKS)
        refresh()
    }

    fun isPoisoned(): Boolean {
        return entity.get(POISON_SEVERITY) > 0
    }

    fun refresh() {
        (entity as? Player)?.packets?.sendVar(102, if (isPoisoned()) 1 else 0)
    }
}
