package com.rs.kotlin.game.player.action

import com.rs.java.game.player.Player
import kotlin.jvm.Transient

class NewActionManager(@Transient private val player: Player?) {
    private var currentAction: NewAction? = null
    private var actionDelay: Int = 0
    private var locked: Boolean = false

    fun process() {
        if (locked || player == null) {
            //println("[NewActionManager] process(): Skipping because locked=$locked or player=null")
            return
        }

        // Decrement action delay first, but not below zero

        // If player dead, force stop everything and return early
        if (player.isDead) {
            //println("[NewActionManager] process(): Player is dead, forcing stop")
            forceStop()
            return
        }

        // Validate current action: if invalid, force stop
        if (currentAction != null && !validateAction()) {
            //println("[NewActionManager] process(): Validation failed for current action, forcing stop")
            forceStop()
            return
        }
        currentAction?.process(player)
        // Process current main action if any
        //if (player.username.equals("andreas") && actionDelay > 0)
        //println("process(): [${player.username}] Delay is: $actionDelay");
        if (actionDelay > 0) {
            actionDelay--
            return;
        }
        if (currentAction != null) {
            val delay = currentAction!!.processWithDelay(player)
            //println("[NewActionManager] process(): processWithDelay() returned $delay for current action ${currentAction!!.javaClass.simpleName}")
            if (delay == -1) {
                //println("[NewActionManager] process(): processWithDelay returned -1, forcing stop")
                forceStop()
                return
            }
            actionDelay = delay
        }
    }


    private fun validateAction(): Boolean {
        val p = player ?: run {
            return false
        }
        if (p.isDead) {
            return false
        }
        if (p.isLocked) {
            return false
        }
        val result = currentAction?.canProcess(p) ?: false
        return result
    }

    fun setAction(newAction: NewAction?): Boolean {
        if (player == null) {
            return false
        }
        if (newAction == null) {
            return false
        }
        if (locked) {
            return false
        }

        // Check if we can replace current action
        if (currentAction != null) {
            //println("[NewActionManager] setAction(): Current action ${currentAction!!.javaClass.simpleName} present, checking interruptibility and priority")
            if (!currentAction!!.isInterruptible() &&
                newAction.getPriority().ordinal <= currentAction!!.getPriority().ordinal) {
                //println("[NewActionManager] setAction(): Current action is not interruptible and new action priority is not higher, reject new action")
                return false
            }

            if (!currentAction!!.onActionReplaced(newAction)) {
                //println("[NewActionManager] setAction(): Current action rejected being replaced by new action")
                return false
            }

            // Stop current action properly
            //println("[NewActionManager] setAction(): Stopping current action ${currentAction!!.javaClass.simpleName} for new action ${newAction.javaClass.simpleName}")
            currentAction!!.stop(player, true)
        }

        if (!newAction.start(player)) {
            //println("[NewActionManager] setAction(): Failed to start new action ${newAction.javaClass.simpleName}")
            return false
        }
        this.currentAction = newAction
        //println("[NewActionManager] setAction(): New action ${newAction.javaClass.simpleName} started and set as current action")
        return true
    }

    fun forceStop() {
        if (currentAction == null) {
            //println("[NewActionManager] forceStop(): No current action to stop")
            return
        }
        // println("[NewActionManager] forceStop(): Stopping current action ${currentAction!!.javaClass.simpleName}")
        currentAction!!.stop(player, false)
        currentAction = null

    }

    fun lock() {
        //println("[NewActionManager] lock(): Locking action manager and forcing stop of current actions")
        this.locked = true
        forceStop()
    }

    fun unlock() {
        //println("[NewActionManager] unlock(): Unlocking action manager")
        this.locked = false
    }

    fun getActionDelay(): Int {
        return actionDelay
    }

    fun setActionDelay(delay: Int) {
        //println("[NewActionManager] setActionDelay(): Setting action delay to $delay")
        this.actionDelay = delay
    }

    fun addActionDelay(delay: Int) {
        //println("[NewActionManager] addActionDelay(): Adding $delay to action delay (was $actionDelay)")
        this.actionDelay += delay
    }

    fun hasActionWorking(): Boolean {
        return currentAction != null
    }

    fun getCurrentAction(): NewAction? {
        return currentAction
    }

    fun isLocked(): Boolean {
        return locked
    }

    fun interruptNonCombatActions() {
        if (currentAction != null && currentAction!!.getPriority() != NewAction.ActionPriority.COMBAT) {
            //println("[NewActionManager] interruptNonCombatActions(): Interrupting current non-combat action ${currentAction!!.javaClass.simpleName}")
            forceStop()
        }
    }

    fun stopActionOfType(actionClass: Class<out NewAction>) {
        if (currentAction != null && actionClass.isInstance(currentAction)) {
            //println("[NewActionManager] stopActionOfType(): Stopping current action of type ${actionClass.simpleName}")
            forceStop()
        }
    }
}
