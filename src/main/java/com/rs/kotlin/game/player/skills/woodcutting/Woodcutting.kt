package com.rs.kotlin.game.player.skills.woodcutting

import com.rs.java.game.Animation
import com.rs.java.game.World
import com.rs.java.game.WorldObject
import com.rs.java.game.WorldTile
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.actions.Action
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class Woodcutting(
    private val player: Player,
    private val tree: WorldObject,
    private val definition: TreeDefinition,
) : Action() {
    private var ticksChopping = 0
    private lateinit var axe: AxeDefinition

    private fun debug(msg: String) {
        if (com.rs.Settings.DEBUG) {
            println("[Woodcutting] ${player.username}: $msg")
        }
    }

    override fun start(player: Player): Boolean {
        axe = AxeDefinition.getBestAxe(player)
            ?: return player.message("You need an axe to chop this tree.").let { false }

        if (player.skills.getLevel(Skills.WOODCUTTING) < definition.level) {
            player.message("You need Woodcutting level ${definition.level}.")
            return false
        }
        if (definition.depletionMode == DepletionMode.TIMED) {
            if (!tree.hasMeta("tree_life")) {
                tree.setMeta("tree_life", definition.despawnTicks)
            }
        }
        player.animate(axe.animation)
        return true
    }

    override fun process(player: Player): Boolean {
        if (!World.containsObjectWithId(tree, tree.id)) {
            return false
        }
        if (player.skills.getLevel(Skills.WOODCUTTING) < definition.level) {
            player.message("You need Woodcutting level ${definition.level}.")
            return false
        }
        if (!player.inventory.hasFreeSlots()) {
            player.message("Not enough space in your inventory.")
            return false
        }
        player.animate(axe.animation)
        return true
    }

    override fun processWithDelay(player: Player): Int {
        val chance = calculateSuccessChance()
        val roll = Random.nextDouble()
        val success = roll <= chance
        var life = tree.getMeta<Int>("tree_life") ?: definition.despawnTicks
        debug("Roll: $roll | Needed: <= $chance | Timer: $life")
        when (definition.depletionMode) {
            DepletionMode.INSTANT -> {
                if (!success) return 4

                giveLog()
                depleteTree()
                return -1
            }

            DepletionMode.TIMED -> {
                var life = tree.getMeta<Int>("tree_life")!!

                life -= 4
                tree.setMeta("tree_life", life)

                if (!success) return 4

                giveLog()

                if (life <= 0) {
                    depleteTree()
                    tree.removeMeta("tree_life")
                    return -1
                }

                return 4
            }
        }
    }

    private fun calculateSuccessChance(): Double {
        val level = min(player.skills.getLevel(Skills.WOODCUTTING), 99)

        val low = definition.low
        val high = definition.high

        val scaled =
            low * (99 - level) / 98.0 +
                high * (level - 1) / 98.0

        val baseChance = (1 + floor(scaled + 0.5)) / 256.0

        val modifiedChance = baseChance * axe.efficiencyMultiplier

        return min(1.0, modifiedChance)
    }

    private fun giveLog() {
        player.inventory.addItem(definition.logId, 1)
        player.skills.addXp(Skills.WOODCUTTING, definition.xp)
        player.message("You get some logs.")
        player.animate(-1)
    }

    private fun depleteTree() {
        World.spawnObjectTemporary(
            WorldObject(
                definition.stumpId,
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

        if (definition.name == "IVY" || definition.name == "VINES") return

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

    override fun stop(player: Player) {
        player.animate(-1)
        ticksChopping = 0
    }
}
