package com.rs.kotlin.game.world.task

class WorldTaskScope {
    internal var stopRequested = false

    /** Signals the scheduler to remove this task after the current tick. */
    fun stop() {
        stopRequested = true
    }
}
