package com.rs.kotlin.game.player

import com.rs.java.game.Entity
import com.rs.core.thread.WorldThread

class PlayerPendingEffects {
    private val pendingEffects = mutableListOf<PendingEffect>()

    fun addEffect(effect: PendingEffect) {
        pendingEffects.add(effect)
    }

    fun removeEffectsForEntity(entity: Entity) {
        val iterator = pendingEffects.iterator()
        while (iterator.hasNext()) {
            val effect = iterator.next()
            if (effect.attacker == entity || effect.defender == entity) {
                effect.onCancel()
                iterator.remove()
            }
        }
    }

    fun clear() {
        pendingEffects.forEach { it.onCancel() }
        pendingEffects.clear()
    }

    fun process() {
        val currentTick = WorldThread.WORLD_TICK
        val iterator = pendingEffects.iterator()

        while (iterator.hasNext()) {
            val effect = iterator.next()

            if (effect.shouldCancel()) {
                effect.onCancel()
                iterator.remove()
                continue
            }

            if (effect.onTick(currentTick)) {
                effect.onExecute()
                iterator.remove()
            }
        }
    }

    fun hasEffects(): Boolean = pendingEffects.isNotEmpty()
}