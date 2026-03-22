package raynna.game.player.skills.fishing

import raynna.game.Animation
import raynna.game.WorldTile
import raynna.game.item.Item
import raynna.game.item.ground.GroundItems
import raynna.game.npc.CombatEventNPC
import raynna.game.npc.NPC
import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.game.player.actions.Action
import raynna.util.Utils
import kotlin.math.floor
import kotlin.math.min

class Fishing(
    private val npc: NPC,
    private val spot: SpotDefinition,
) : Action() {
    private val spawnTile = WorldTile(npc)
    private lateinit var rod: RodDefinition
    private var currentCatch: FishDefinition = spot.catches.first()

    override fun start(player: Player): Boolean {
        rod = RodDefinition.getTool(player, spot.tool)
            ?: return player.message("You need a ${itemName(spot.tool.itemId)} to fish here.").let { false }
        if (!check(player)) return false
        currentCatch = rollCatch(player)
        player.message("You attempt to capture a fish...")
        player.actionManager.actionDelay = calculateDelay(player)
        return true
    }

    override fun process(player: Player): Boolean {
        player.animate(Animation(spot.animation))
        return check(player)
    }

    override fun processWithDelay(player: Player): Int {
        giveCatch(player)
        return calculateDelay(player)
    }

    override fun stop(player: Player) {
        player.animate(Animation(-1))
    }

    /**
     * Delay is derived from the current catch's success chance.
     * Each fish has its own low/high curve, so higher-tier fish naturally
     * take longer to catch even at the same fishing level.
     */
    private fun calculateDelay(player: Player): Int {
        val chance = calculateSuccessChance(player, currentCatch)
        val cycles = (1.0 / chance).coerceIn(1.0, 10.0)
        return floor(cycles).toInt()
    }

    /**
     * Wiki skilling success formula:
     *   P(level) = (1 + floor( low*(99−level)/98 + high*(level−1)/98 + 0.5 )) / 256
     *
     * Evaluated against the specific fish's curve, not the spot's.
     */
    private fun calculateSuccessChance(
        player: Player,
        fish: FishDefinition,
    ): Double {
        val level = min(player.skills.getLevel(Skills.FISHING), 99)
        val (low, high) = fish.successCurve
        val scaled = low * (99 - level) / 98.0 + high * (level - 1) / 98.0
        val p = (1 + floor(scaled + 0.5)) / 256.0
        return p.coerceIn(0.0, 1.0)
    }

    private fun giveCatch(player: Player) {
        FishingOutfit.roll(player)

        val fish = currentCatch
        val extraCatch = rollDoubleCatch()
        val amount = if (extraCatch) 2 else 1

        player.inventory.deleteItem(spot.baitId, 1)

        val item = Item(fish.itemId, amount)
        if (player.inventory.hasFreeSlots()) {
            player.inventory.addItem(item)
        } else {
            GroundItems.updateGroundItem(item, player.tile, player)
        }

        val xp = fish.xp * amount * (1.0 + FishingOutfit.xpBonus(player))
        player.skills.addXp(Skills.FISHING, xp)

        player.message(buildMessage(fish, extraCatch))

        fish.task?.let { player.taskManager.progress(it, amount) }

        rollFamiliarBonus(player)
        rollRandomEvent(player)
        rollSpotMove(player)

        currentCatch = rollCatch(player)
    }

    /**
     * Rolls which fish to attempt next, weighted by success chance so
     * higher-level fish are naturally rarer at lower levels.
     */
    private fun rollCatch(player: Player): FishDefinition {
        val level = player.skills.getLevel(Skills.FISHING)
        val eligible = spot.catches.filter { level >= it.level }
        if (eligible.isEmpty()) return spot.catches.first()

        // Weight each eligible fish by its success chance so the higher-tier
        // fish in a multi-catch spot (e.g. salmon vs trout) appears less often
        // at lower levels, matching wiki behaviour.
        val totalWeight = eligible.sumOf { calculateSuccessChance(player, it) }
        val roll = Math.random() * totalWeight
        var cumulative = 0.0
        for (fish in eligible) {
            cumulative += calculateSuccessChance(player, fish)
            if (roll < cumulative) return fish
        }
        return eligible.last()
    }

    private fun rollDoubleCatch(): Boolean = Utils.getRandom(100) <= 5

    private val bonusFishPool = intArrayOf(341, 349, 401, 407)

    private fun rollFamiliarBonus(player: Player) {
        val familiar = player.familiar ?: return
        if (getFamiliarBonus(familiar.id) <= 0) return
        if (Utils.getRandom(50) != 0) return
        player.inventory.addItem(Item(bonusFishPool[Utils.random(bonusFishPool.size)]))
        player.skills.addXp(Skills.FISHING, 5.5)
    }

    private fun getFamiliarBonus(id: Int): Int =
        when (id) {
            6795, 6796 -> 1

            // ibis
            else -> -1
        }

    private fun rollRandomEvent(player: Player) {
        if (Utils.getRandom(6000) == 0) {
            CombatEventNPC.startRandomEvent(player, Skills.FISHING)
        }
    }

    private fun rollSpotMove(player: Player) {
        if (Utils.getRandom(50) == 0 && FishingSpotsHandler.moveSpot(npc)) {
            player.animate(Animation(-1))
        }
    }

    private fun check(player: Player): Boolean {
        if (spawnTile.x != npc.x || spawnTile.y != npc.y) return false

        val level = player.skills.getLevel(Skills.FISHING)
        if (level < currentCatch.level) {
            player.message("You need a Fishing level of ${currentCatch.level} to catch this.")
            return false
        }

        val resolvedRod = RodDefinition.getTool(player, spot.tool)
        if (resolvedRod == null) {
            player.message("You need a ${itemName(spot.tool.itemId)} to fish here.")
            return false
        }
        rod = resolvedRod

        if (spot.baitId != -1 && !player.inventory.containsOneItem(spot.baitId)) {
            player.message("You don't have ${itemName(spot.baitId)} to fish here.")
            return false
        }

        if (!player.inventory.hasFreeSlots()) {
            player.animate(Animation(-1))
            player.dialogueManager.startDialogue("SimpleMessage", "You don't have enough inventory space.")
            return false
        }

        return true
    }

    private fun buildMessage(
        fish: FishDefinition,
        double: Boolean,
    ): String {
        val name = Item(fish.itemId).definitions.name.lowercase()
        val plural = fish == FishDefinition.ANCHOVIES || fish == FishDefinition.SHRIMP
        return when {
            double && plural -> "You manage to catch some extra $name."
            double -> "You manage to catch an extra $name."
            plural -> "You manage to catch some $name."
            else -> "You manage to catch a $name."
        }
    }

    private fun itemName(itemId: Int): String = Item(itemId).definitions.name.lowercase()
}
