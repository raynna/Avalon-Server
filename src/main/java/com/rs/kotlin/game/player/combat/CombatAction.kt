package com.rs.kotlin.game.player.combat

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Keys
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.action.NewAction
import com.rs.kotlin.game.player.combat.magic.MagicStyle
import com.rs.kotlin.game.player.combat.melee.MeleeStyle
import com.rs.kotlin.game.player.combat.range.RangeData
import com.rs.kotlin.game.player.combat.range.RangedStyle
import com.rs.kotlin.game.player.interfaces.HealthOverlay
import kotlin.math.abs

class CombatAction(
    private val target: Entity
) : NewAction() {

    enum class CombatPhase {
        HIT
    }

    private val healthOverlay = HealthOverlay()

    private var lastTargetX = -1
    private var lastTargetY = -1

    private var followTask: WorldTask? = null

    private var phase = CombatPhase.HIT
    private var ticksUntilNextPhase = 0
    private lateinit var style: CombatStyle

    override fun start(player: Player): Boolean {
        if (target.isDead) {
            return false
        }
        val spellId = player.getCombatDefinitions().spellId
        style = when {
            spellId != 0 -> MagicStyle
            isRangedWeapon(player) -> RangedStyle
            else -> MeleeStyle
        }
        player.temporaryTarget = target
        player.tickTimers.set(Keys.IntKey.LAST_ATTACK_TICK, 10)
        healthOverlay.sendOverlay(player, target)
        player.setNextFaceEntity(target);
        player.resetWalkSteps()
        if (style == MeleeStyle)
            player.calcFollow(target, if (player.run) 2 else 1, true, true)
        ensureFollowTask(player)
        return true
    }

    override fun process(player: Player): Boolean {
        if (target.isDead) {
            return false
        }
        val spellId = player.getCombatDefinitions().spellId
        style = when {
            spellId != 0 -> MagicStyle
            isRangedWeapon(player) -> RangedStyle
            else -> MeleeStyle
        }
        if (style == MeleeStyle)
            player.calcFollow(target, if (player.run) 2 else 1, true, true)
        ensureFollowTask(player);
        return true
    }

    override fun processWithDelay(player: Player): Int {
        if (!process(player) || !check(player, target)) {
            return -1
        }
        //player.message("process with delay")
        val requiredDistance = style.getAttackDistance()
        if ((!player.clipedProjectile(target, requiredDistance == 0)) || !Utils.isOnRange(player.x, player.y, player.size, target.x, target.y, target.size, requiredDistance)) {
            return 0
        }
        if (Utils.colides(player.x, player.y,   player.size, target.x, target.y, target.size) && !target.hasWalkSteps()) {
            return 0
        }
        if (player.combatDefinitions.spellId <= 0
            && player.equipment.weaponId != 24203 && target.size == 1
            && abs(player.x - target.x) == 1 && abs(player.y - target.y) == 1) {
            return 0;
        }
        return when (phase) {
            CombatPhase.HIT -> {
                if (validateAttack(player, target)) {
                    player.tickTimers.set(Keys.IntKey.LAST_ATTACK_TICK, 10)
                    style.attack()
                }
                phase = CombatPhase.HIT
                style.getAttackSpeed() - 1
            }
        }
    }

    private fun ensureFollowTask(player: Player) {
        /*if (followTask != null) {
            player.message("cant assign new followTask");
            return // already running
        }*/

        val requiredDistance = style.getAttackDistance()
        val size = player.size

        followTask = object : WorldTask() {
            override fun run() {
                if (player.isDead || target.isDead || !player.isActive) {
                    stopFollowTask()
                    return
                }

                // Don't stop on style switch; only stop if combat ends
                if (player.newActionManager.getCurrentAction() != this@CombatAction) {
                    stopFollowTask()
                    return
                }

                if (Utils.colides(player.x, player.y, size, target.x, target.y, target.size) && !target.hasWalkSteps()) {
                    if (player.freezeDelay >= Utils.currentTimeMillis()) {
                        player.packets.sendGameMessage("A magical force prevents you from moving.")
                        stopFollowTask()
                        return
                    }
                    if (!handleCollisionMovement(player, target, size)) {
                        stopFollowTask()
                    }
                    return
                }

                if (shouldAdjustDiagonal(player, target)) {
                    if (player.freezeDelay >= Utils.currentTimeMillis()) {
                        player.packets.sendGameMessage("A magical force prevents you from moving.")
                        stopFollowTask()
                        return
                    }
                    player.calcFollow(target, if (player.run) 2 else 1, true, true)
                    return
                }

                player.resetWalkSteps()
                if ((!player.clipedProjectile(target, requiredDistance == 0)) || !Utils.isOnRange(player.x, player.y, player.size, target.x, target.y, target.size, requiredDistance)) {
                    val moved = lastTargetX != target.x || lastTargetY != target.y
                    lastTargetX = target.x
                    lastTargetY = target.y
                    if (moved || (!player.hasWalkSteps() && player.newActionManager.getCurrentAction() != null)) {
                        player.calcFollow(target, if (player.run) 2 else 1, true, true)
                    }
                }
            }
        }
        WorldTasksManager.schedule(followTask, 0, 0)
    }

    private fun stopFollowTask() {
        followTask?.stop()
        followTask = null
    }

    private fun check(player: Player, target: Entity): Boolean {
        if (!style.canAttack(player, target)) {
            return false
        }
        if (player.isAtMultiArea && !target.isAtMultiArea) {
            if (target.attackedBy != player && target.attackedByDelay > Utils.currentTimeMillis()) {
                player.message("That " + (if (player.getAttackedBy() is Player) "player" else "npc") + " is already in combat.")
                return false
            }
        }
        if (!target.isAtMultiArea && !player.isAtMultiArea) {
            if (player.getAttackedBy() !== target && player.attackedByDelay > Utils.currentTimeMillis()) {
                player.message("You are already in combat.")
                return false
            }
            if (target.attackedBy !== player && target.attackedByDelay > Utils.currentTimeMillis()) {
                player.message(
                    ("That " + (if (player.getAttackedBy() is Player) "player" else "npc") + " is already in combat.")
                )
                return false
            }
        }
        return true;
    }

    private fun validateAttack(player: Player, target: Entity): Boolean {
        return !target.isDead && player.isActive
    }

    fun reset() {
        phase = CombatPhase.HIT
        ticksUntilNextPhase = 0
    }

    override fun stop(player: Player, interrupted: Boolean) {
        println("[CombatAction] stop(): Combat stopped (interrupted=$interrupted)")
        style.onStop(interrupted)
    }

    override fun getPriority(): ActionPriority {
        println("[CombatAction] getPriority(): Returning COMBAT priority")
        return ActionPriority.COMBAT
    }

    private fun isRangedWeapon(player: Player): Boolean {
        val weaponId = player.equipment.getWeaponId()
        val ranged = RangeData.getWeaponByItemId(weaponId);
        if (ranged != null) {
           // println("[CombatAction] isRangedWeapon(): weaponId=${ranged.name}, ${ranged.ammoType.name}")
        }
        return ranged != null
    }

    private fun handleCollisionMovement(player: Player, target: Entity, size: Int): Boolean {
        if (player.freezeDelay >= System.currentTimeMillis()) {
            player.message("A magical force prevents you from moving.")
            return false
        }

        player.resetWalkSteps()
        return listOf(
            { player.addWalkSteps(target.x + target.size, player.y) },
            { player.addWalkSteps(target.x - size, player.y) },
            { player.addWalkSteps(player.x, target.y + target.size) },
            { player.addWalkSteps(player.x, target.y - size) }
        ).any { it() }
    }

    private fun handlePathfinding(player: Player, target: Entity): Boolean {
        player.resetWalkSteps()
        if (!player.hasWalkSteps()) {
            if (player.x == target.x && player.y == target.y && target.hasWalkSteps()) {
                if (player.index < target.index) player.resetWalkSteps() else target.resetWalkSteps()
            } else {
                player.calcFollow(target, if (player.run) 2 else 1, true, true)
            }
        }
        return true
    }

    private fun calculateMaxAttackDistance(player: Player): Int {
        return 0
    }

    private fun Player.hasRequiredWeapon(ids: List<Int>): Boolean {
        return equipment.weaponId in ids ||
                combatDefinitions.autoCastSpell > 0
    }

    private fun isMeleeAttacking(player: Player): Boolean {
        return true
    }

    private fun shouldAdjustDiagonal(player: Player, target: Entity): Boolean {
        return target.size == 1 &&
                abs(player.x - target.x) == 1 &&
                abs(player.y - target.y) == 1 &&
                !target.hasWalkSteps()
    }
}