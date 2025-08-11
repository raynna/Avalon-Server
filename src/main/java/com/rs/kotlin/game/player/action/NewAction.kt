package com.rs.kotlin.game.player.action

import com.rs.java.game.player.Player

abstract class NewAction {
    enum class ActionPriority {
        COMBAT,      // Highest priority (player attacks, NPC attacks)
        MOVEMENT,    // Movement and pathfinding
        SKILLING,    // Woodcutting, mining, etc.
        LOW          // Emotes, trivial actions
    }

    protected var interrupted: Boolean = false

    abstract fun start(player: Player): Boolean
    abstract fun process(player: Player): Boolean
    abstract fun processWithDelay(player: Player): Int
    abstract fun stop(player: Player, interrupted: Boolean)

    fun canProcess(player: Player): Boolean {
        return !player.isDead && !player.isLocked
    }

    open fun getPriority(): ActionPriority {
        return ActionPriority.LOW
    }

    open fun isInterruptible(): Boolean {
        return true
    }

    protected fun setActionDelay(player: Player, delay: Int) {
        player.newActionManager.setActionDelay(delay)
    }

    open fun onActionReplaced(newAction: NewAction): Boolean {
        return true
    }

    open fun getCooldown(): Int = 0

    open fun onFinish(player: Player) {}
}