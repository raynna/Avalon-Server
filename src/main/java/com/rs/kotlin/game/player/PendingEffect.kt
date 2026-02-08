package com.rs.kotlin.game.player

import com.rs.core.thread.WorldThread
import com.rs.java.game.Entity

abstract class PendingEffect(
    private val _attacker: Entity,
    private val _defender: Entity,
    private val _startTick: Int = WorldThread.WORLD_TICK,
    private val _durationTicks: Int
) {
    var active: Boolean = true

    val attacker: Entity get() = _attacker
    val defender: Entity get() = _defender
    val startTick: Int get() = _startTick
    val durationTicks: Int get() = _durationTicks

    abstract fun onTick(currentTick: Int): Boolean
    abstract fun onExecute()
    abstract fun onCancel()

    open fun apply() {

    }
    open fun shouldCancel(): Boolean {
        return !active ||
                attacker.hasFinished() ||
                defender.hasFinished()
    }
}