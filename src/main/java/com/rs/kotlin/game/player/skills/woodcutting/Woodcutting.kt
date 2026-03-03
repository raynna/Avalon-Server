package com.rs.kotlin.game.player.skills.woodcutting

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.World
import com.rs.java.game.WorldObject
import com.rs.java.game.WorldTile
import com.rs.java.game.item.Item
import com.rs.java.game.item.ground.GroundItems
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.actions.Action
import com.rs.java.utils.Utils
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager
import com.rs.kotlin.game.world.util.Msg
import kotlin.math.floor
import kotlin.math.min
import kotlin.random.Random

private const val BURN_LOG_PROJECTILE = 1775
private const val BURN_LOG_GFX = 1776
private const val BURN_LOG_GFX_2 = 1777

class Woodcutting(
    private val player: Player,
    private val tree: WorldObject,
    private val definition: TreeDefinition,
) : Action() {
    private lateinit var axe: AxeDefinition

    override fun start(player: Player): Boolean {
        axe = AxeDefinition.getBestAxe(player)
            ?: return player.message("You need an axe to chop this tree.").let { false }
        if (!check()) {
            return false
        }
        player.animate(axe.animation)
        player.actionManager.actionDelay = 4
        return true
    }

    override fun process(player: Player): Boolean {
        if (!check()) {
            return false
        }
        player.animate(axe.animation)
        if (definition == TreeDefinition.IVY) {
            if (tree.rotation == 2) { // TODO get all rotations for each ivy spot
                player.faceWorldTile(tree.x + 1, tree.y, tree.plane)
            }
        } else {
            player.faceObject(tree)
        }
        return true
    }

    override fun processWithDelay(player: Player): Int {
        val chance = calculateSuccessChance()
        val roll = Random.nextDouble()
        val success = roll <= chance
        if (!success) {
            return 4
        }
        giveLog()
        if (Utils.roll(1, definition.depleteChance)) {
            depleteTree()
        }
        return 4
    }

    private fun check(): Boolean {
        if (!World.containsObjectWithId(tree, tree.id)) {
            return false
        }
        if (player.skills.getLevel(Skills.WOODCUTTING) < definition.level) {
            player.message("You need Woodcutting level ${definition.level}.")
            return false
        }
        if (!player.inventory.hasFreeSlots() && definition.logId != -1) {
            player.message("Not enough space in your inventory.")
            return false
        }
        return true
    }

    private fun calculateSuccessChance(): Double {
        val level = min(player.skills.getLevel(Skills.WOODCUTTING), 99)

        val curve = axe.successCurveFor(definition)
        val low = curve.low
        val high = curve.high

        val scaled =
            low * (99 - level) / 98.0 +
                high * (level - 1) / 98.0

        val baseChance = (1 + floor(scaled + 0.5)) / 256.0
        return min(1.0, baseChance)
    }

    private fun addLogsToPlayer(amount: Int) {
        val freeSlots = player.inventory.freeSlots
        val toInventory = minOf(amount, freeSlots)
        val toGround = amount - toInventory

        if (toInventory > 0) {
            player.inventory.addItem(definition.logId, toInventory)
        }

        repeat(toGround) {
            GroundItems.updateGroundItem(Item(definition.logId), player.tile, player)
        }
    }

    private fun burnLog() {
        val targetTile = World.getRandomFreeTileAround(player.tile, 3, 5, 1, 10)

        ProjectileManager.sendToTile(
            Projectile.INFERNO_ADZE,
            BURN_LOG_PROJECTILE,
            player.tile,
            targetTile,
        ) {
            player.skills.addXp(Skills.FIREMAKING, definition.xp / 2.0)
            Msg.collect(player, "The Infernal adze burns the log into ashes.")
            World.sendGraphics(player, Graphics(BURN_LOG_GFX, 0, 0, 0), targetTile)
            World.sendGraphics(player, Graphics(BURN_LOG_GFX_2, 0, 0, 0), targetTile)
        }
    }

    private fun giveLog() {
        LumberjackOutfit.roll(player)
        if (definition.logId != -1) {
            val logs = 1 + if (tryDoubleLog()) 1 else 0

            var kept = 0

            repeat(logs) {
                if (tryInfernalBurn()) {
                    burnLog()
                } else {
                    kept++
                }
            }

            if (kept > 0) {
                addLogsToPlayer(kept)
                player.message("You get some logs.")
                definition.task?.let {
                    player.taskManager.progress(it, kept)
                }
            }

            if (logs > 1 && kept > 0) {
                Msg.collect(player, "You managed to chop an extra log!")
            }
        }
        player.skills.addXp(Skills.WOODCUTTING, definition.xp)
        BirdNests.rollBirdNest(player, tree)
    }

    private fun depleteTree() {
        val stumpId = TreeStumps.getStumpId(tree.id)
        World.spawnObjectTemporary(
            WorldObject(
                stumpId,
                tree.type,
                tree.rotation,
                tree.x,
                tree.y,
                tree.getPlane(),
            ),
            definition.respawnTicks,
        )
        handleUpperLevelRemoval(player, respawnTime = definition.respawnTicks)
    }

    private fun handleUpperLevelRemoval(
        player: Player,
        respawnTime: Int,
    ) {
        if (tree.plane >= 3) return

        if (definition == TreeDefinition.IVY) return

        val upperPlane = tree.plane + 1

        val possibleTiles =
            listOf(
                WorldTile(tree.x - 1, tree.y - 1, upperPlane),
                WorldTile(tree.x, tree.y - 1, upperPlane),
                WorldTile(tree.x - 1, tree.y, upperPlane),
                WorldTile(tree.x, tree.y, upperPlane),
            )

        val upperObject =
            possibleTiles
                .asSequence()
                .map { World.getStandardFloorObject(it) }
                .firstOrNull { it != null }

        if (upperObject != null) {
            World.removeObjectTemporary(upperObject, respawnTime, true)
            player.animate(Animation(-1))
        }
    }

    private fun tryInfernalBurn(): Boolean = axe.isInfernalAdze() && Utils.roll(1, 3) && definition != TreeDefinition.IVY

    private fun tryDoubleLog(): Boolean {
        val chance = doubleLogChance()
        return Random.nextDouble() <= chance
    }

    private fun doubleLogChance(): Double {
        val wcLevel = player.skills.getLevelForXp(Skills.WOODCUTTING)
        val treeLevel = definition.level

        // Base scaling from player level (0% → 15%)
        val levelScaling = (wcLevel - 1) / 98.0 * 0.15

        // Harder trees reduce chance slightly
        val difficultyPenalty = treeLevel / 200.0

        val finalChance =
            (levelScaling - difficultyPenalty)
                .coerceIn(0.02, 0.18) // 2% min, 18% max

        return finalChance
    }

    override fun stop(player: Player) {
        player.animate(-1)
    }
}
