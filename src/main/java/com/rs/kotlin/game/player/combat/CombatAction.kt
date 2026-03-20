package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.game.player.TickManager
import com.rs.java.game.player.actions.Action
import com.rs.java.game.player.actions.combat.QueuedInstantCombat
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.magic.MagicStyle
import com.rs.kotlin.game.player.combat.magic.special.GreaterRunicStaffWeapon
import com.rs.kotlin.game.player.combat.magic.special.NightmareStaff
import com.rs.kotlin.game.player.combat.magic.special.ObliterationWeapon
import com.rs.kotlin.game.player.combat.magic.special.PolyporeStaff
import com.rs.kotlin.game.player.combat.melee.MeleeStyle
import com.rs.kotlin.game.player.combat.range.RangeData
import com.rs.kotlin.game.player.combat.range.RangedStyle
import com.rs.kotlin.game.player.combat.special.SpecialAttack
import com.rs.kotlin.game.world.pvp.PvpManager
import kotlin.math.abs

class CombatAction(
    private val target: Entity,
) : Action() {
    companion object {
        @JvmStatic
        fun getCombatStyle(
            player: Player,
            target: Entity,
        ): CombatStyle {
            val spellId = player.getCombatDefinitions().spellId
            return when {
                NightmareStaff.hasWeapon(player) && player.combatDefinitions.isUsingSpecialAttack -> {
                    MagicStyle(
                        player,
                        target,
                    )
                }

                ObliterationWeapon.hasWeapon(player) && player.combatDefinitions.isUsingSpecialAttack -> {
                    MagicStyle(
                        player,
                        target,
                    )
                }

                spellId != 0 -> {
                    MagicStyle(player, target)
                }

                GreaterRunicStaffWeapon.hasWeapon(player) && GreaterRunicStaffWeapon.getSpellId(player) != -1 -> {
                    MagicStyle(
                        player,
                        target,
                    )
                }

                PolyporeStaff.hasWeapon(player) -> {
                    MagicStyle(player, target)
                }

                isRangedWeapon(player) -> {
                    RangedStyle(player, target)
                }

                else -> {
                    MeleeStyle(player, target)
                }
            }
        }

        private fun isRangedWeapon(player: Player): Boolean {
            val weaponId =
                if (player.predictedWeaponSwitch > 0) player.predictedWeaponSwitch else player.equipment.getWeaponId()
            val ranged = RangeData.getWeaponByItemId(weaponId)
            return ranged != null
        }
    }

    enum class CombatPhase {
        HIT,
    }

    private var phase = CombatPhase.HIT
    private var ticksUntilNextPhase = 0
    private lateinit var style: CombatStyle

    override fun start(player: Player): Boolean {
        if (target.isDead || target.hasFinished() || player.isLocked) {
            stop(player)
            return false
        }
        style = getCombatStyle(player, target)
        player.faceEntity(target)
        player.setNextFaceEntity(target)
        player.resetWalkSteps()
        player.tickManager.addTicks(TickManager.TickKeys.LAST_INTERACTION_TARGET, 10)
        player.temporaryTarget = target
        player.healthOverlay.sendOverlay(player, target)
        handleFollowing(player)
        return true
    }

    override fun process(player: Player): Boolean {
        if (target.isDead || target.hasFinished() || player.isLocked) {
            stop(player)
            return false
        }
        val activeInstantSpecial = player.activeInstantSpecial
        if (activeInstantSpecial != null) {
            val special = activeInstantSpecial.special

            if (special is SpecialAttack.InstantRangeCombat) {
                if (!check(player, target)) {
                    return false
                }

                val style = activeInstantSpecial.context.combat
                val dist = style.getAttackDistance()

                if (player.isOutOfRange(target, dist)) {
                    player.calcFollow(target, if (player.run) 2 else 1, true, true)
                    return true
                }

                if (shouldAdjustDiagonal(player, target)) {
                    player.calcFollow(target, if (player.run) 2 else 1, true, true)
                    return true
                }

                player.faceEntity(activeInstantSpecial.context.defender)

                special.execute(activeInstantSpecial.context)

                player.combatDefinitions.decreaseSpecialAttack(special.energyCost)

                player.clearActiveInstantSpecial()

                player.setLastAttackTimer(16)
                player.setGraniteMaulTimer(5)

                target.setInCombat(16)
                target.setPjTimer(12)

                player.stopAll(false, true, true)

                return true
            }
        }
        if (player.temporaryTarget == null || player.temporaryTarget == target) {
            player.healthOverlay.updateHealthOverlay(player, target, true)
            player.temporaryTarget = target
        }
        updateStyle(player)

        val toExecute = mutableListOf<QueuedInstantCombat<out SpecialAttack>>()
        var totalEnergyCost = 0

        for (queued in player.queuedInstantCombats.toList()) {
            val queuedStyle = queued.context.combat
            if (player.combatDefinitions.spellId > 0 && queuedStyle !is MagicStyle) {
                continue
            }
            if (!player.isOutOfRange(target, queuedStyle.getAttackDistance()) &&
                !shouldAdjustDiagonal(player, target)
            ) {
                val cost = queued.special.energyCost
                if (player.combatDefinitions.specialAttackPercentage < totalEnergyCost + cost) {
                    player.queuedInstantCombats.clear()
                    break
                }
                totalEnergyCost += cost
                toExecute.add(queued)
            }
        }

        if (toExecute.isNotEmpty()) {
            for (queued in toExecute) {
                if (player.equipment.getWeaponId() != queued.context.weaponId) {
                    continue
                }
                if (queued.special is SpecialAttack.InstantCombat) {
                    if (!check(player, target)) {
                        return false
                    }
                    player.faceEntity(queued.context.defender)
                    player.combatDefinitions.decreaseSpecialAttack(queued.special.energyCost)
                    queued.special.execute(queued.context)
                    player.queuedInstantCombats.remove(queued)
                    player.setLastAttackTimer(16)
                    player.setGraniteMaulTimer(5)
                    target.setInCombat(16)
                    target.setPjTimer(12)
                }
            }
            player.stopAll(false, true, true)
        }

        /*val activeInstantSpecial = player.activeInstantSpecial
        if (activeInstantSpecial != null) {
            player.clearActiveInstantSpecial()
            val special = activeInstantSpecial.special
            if (special is SpecialAttack.InstantRangeCombat) {
                if (!check(player, target))
                    return false
                player.faceEntity(activeInstantSpecial.context.defender)
                special.execute(activeInstantSpecial.context)
                player.stopAll(false, true, true)
                return true
            }
        }*/

        player.combatStyle = style
        val requiredDistance = getAdjustedFollowDistance(target)
        if (player.isOutOfRange(target, requiredDistance)) {
            player.resetWalkSteps()
            player.calcFollow(target, if (player.run) 2 else 1, true, true)
        }
        if (player.isDiagonalMeleeBlocked(target, style)) {
            player.resetWalkSteps()
            player.calcFollow(target, if (player.run) 2 else 1, true, true)
        }
        if (player.isOutOfRegion(target)) {
            return false
        }
        handleFollowing(player)
        return true
    }

    override fun processWithDelay(player: Player): Int {
        if (!process(player)) {
            return -1
        }
        updateStyle(player)
        val requiredDistance = style.getAttackDistance()
        if (player.isOutOfRange(target, requiredDistance)) {
            return 0
        }

        if (player.isCollidingWithTarget(target)) {
            if (player.isFrozen) {
                player.packets.sendGameMessage("A magical force prevents you from moving.")
                return 0
            }
            handleCollisionMovement(player, target, player.size)
            return 0
        }
        if (player.isDiagonalMeleeBlocked(target, style)) {
            return 0
        }
        if (player.familiarAutoAttack) {
            if (player.familiar != null && !player.familiar.combat.hasTarget() && player.isAtMultiArea && target.isAtMultiArea) {
                player.familiar.setTarget(target)
            }
        }

        return when (phase) {
            CombatPhase.HIT -> {
                if (!check(player, target)) {
                    return -1
                }
                if (validateAttack(player, target)) {
                    target.attackedBy = player

                    player.setLastAttackTimer(16)
                    player.setGraniteMaulTimer(5)
                    target.setInCombat(16)
                    target.setPjTimer(12)
                    if (target is Player) {
                        target.skullList[player] = 1440
                        PvpManager.onPlayerDamagedByPlayer(target, player)
                    }
                    style.attack()
                }
                phase = CombatPhase.HIT
                val foodLock = player.getFoodLockTicks()
                style.getAttackSpeed() + foodLock - 1
            }
        }
    }

    override fun stop(player: Player) {
        player.resetWalkSteps()
        player.setNextFaceEntity(null)
        if (::style.isInitialized) {
            style.onStop(true)
        }
    }

    private fun check(
        player: Player,
        target: Entity,
    ): Boolean {
        if (target.isDead || target.hasFinished()) {
            return false
        }
        if (target is NPC && target.isCantInteract) {
            return false
        }
        if (!style.canAttack(player, target)) {
            return false
        }
        if (!player.controlerManager.canHit(target) || !player.controlerManager.keepCombating(target) ||
            !player.controlerManager.canAttack(
                target,
            )
        ) {
            return false
        }
        if (!PvpManager.canPlayerAttack(player, target)) {
            return false
        }
        if (!target.isAtMultiArea) {
            if (player.isPjBlocked && player.attackedBy != target) {
                player.message("You are already in combat.")
                return false
            }
            if (target.isPjBlocked && target.attackedBy != player) {
                if (target is NPC) {
                    if (target.id == 4474 || target.id == 7891) {
                        return true
                    }
                }
                player.message("That " + (if (target is Player) "player" else "npc") + " is already in combat.")
                return false
            }
        }
        return true
    }

    private fun handleFollowing(player: Player) {
        val requiredDistance = getAdjustedFollowDistance(target)
        if (player.isCollidingWithTarget(target)) {
            if (player.isFrozen) {
                player.resetWalkSteps()
                player.packets.sendGameMessage("A magical force prevents you from moving.")
                return
            }
            if (!player.hasWalkSteps()) {
                handleCollisionMovement(player, target, player.size)
            }
            return
        }

        if (shouldAdjustDiagonal(player, target)) {
            if (player.isFrozen) {
                player.packets.sendGameMessage("A magical force prevents you from moving.")
                return
            }
            if (!player.hasWalkSteps()) {
                val dx = target.x - player.x
                val dy = target.y - player.y
                if (abs(dx) == 1 && abs(dy) == 1) {
                    if (!player.addWalkSteps(target.x, player.y)) {
                        player.addWalkSteps(player.x, target.y)
                    }
                }
            }
            return
        }
        if (player.isOutOfRange(target, requiredDistance)) {
            if (!player.hasWalkSteps()) {
                player.calcFollow(target, if (player.run) 2 else 1, true, true)
            }
        }
    }

    private fun validateAttack(
        player: Player,
        target: Entity,
    ): Boolean = !target.isDead && player.isActive

    fun reset() {
        phase = CombatPhase.HIT
        ticksUntilNextPhase = 0
    }

    private fun getAdjustedFollowDistance(target: Entity): Int {
        var baseDistance = style.getAttackDistance()
        if ((style is MagicStyle || style is RangedStyle) && target.hasWalkSteps()) {
            baseDistance = (baseDistance - 1).coerceAtLeast(0)
        }
        return baseDistance
    }

    private fun handleCollisionMovement(
        player: Player,
        target: Entity,
        size: Int,
    ): Boolean {
        if (player.isFrozen) {
            player.message("A magical force prevents you from moving.")
            return false
        }

        val candidates =
            listOf(
                player.x to target.y - size,
                player.x to target.y + target.size,
                target.x - size to player.y,
                target.x + target.size to player.y,
            ).sortedBy { (x, y) ->
                abs(player.x - x) + abs(player.y - y)
            }

        for ((x, y) in candidates) {
            if (player.addWalkSteps(x, y)) return true
        }
        return false
    }

    private fun Player.isCollidingWithTarget(target: Entity): Boolean =
        Utils.colides(this.x, this.y, this.size, target.x, target.y, target.size) &&
            !target.hasWalkSteps()

    private fun Player.isDiagonalMeleeBlocked(
        target: Entity,
        style: CombatStyle,
    ): Boolean {
        val dx = abs(this.x - target.x)
        val dy = abs(this.y - target.y)
        val attackDistance = style.getAttackDistance()

        if (target is Player) {
            val targetAction = target.actionManager.action
            if (targetAction is CombatAction && targetAction.target == this) {
                return false
            }
        }
        return attackDistance < 1 &&
            target.size == 1 &&
            dx == 1 && dy == 1
    }

    private fun shouldAdjustDiagonal(
        player: Player,
        target: Entity,
    ): Boolean {
        val attackDistance = style.getAttackDistance()

        // In mutual melee combat, never try to adjust diagonal — let isDiagonalMeleeBlocked allow the hit
        if (target is Player) {
            val targetAction = target.actionManager.action
            if (targetAction is CombatAction && targetAction.target == player) {
                return false
            }
        }

        return target.size == 1 &&
            abs(player.x - target.x) == 1 &&
            abs(player.y - target.y) == 1 &&
            attackDistance < 1
    }

    private fun updateStyle(player: Player): Boolean {
        val newStyle = getCombatStyle(player, target)
        val changed = !::style.isInitialized || newStyle::class != style::class
        style = newStyle
        player.combatStyle = style

        return changed
    }
}
