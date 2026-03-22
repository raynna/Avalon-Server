package raynna.game.player.skills.mining

import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.World
import raynna.game.WorldObject
import raynna.game.item.Item
import raynna.game.item.ground.GroundItems
import raynna.game.player.Equipment
import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.game.player.actions.Action
import raynna.util.Utils
import raynna.game.world.util.Msg
import kotlin.math.floor
import kotlin.math.min
import kotlin.random.Random

class Mining(
    private val player: Player,
    private val rock: WorldObject,
    private val definition: RockDefinition,
) : Action() {
    private lateinit var pickaxe: PickaxeDefinition

    private fun miningLevel(): Int = min(player.skills.getLevel(Skills.MINING), 99)

    override fun start(player: Player): Boolean {
        pickaxe = PickaxeDefinition.getBestPickaxe(player) ?: run {
            player.message("You need a pickaxe to mine this rock.")
            return false
        }

        if (!check()) return false

        player.animate(pickaxe.animation)
        player.actionManager.actionDelay = pickaxe.getMiningDelay()
        return true
    }

    override fun process(player: Player): Boolean {
        if (!check()) return false

        player.animate(pickaxe.animation)
        player.faceObject(rock)
        return true
    }

    override fun processWithDelay(player: Player): Int {
        if (rollGemPreDrop()) {
            return pickaxe.getMiningDelay()
        }

        if (!calculateSuccessChance()) {
            return pickaxe.getMiningDelay()
        }

        giveOre()

        if (!definition.alwaysSuccess) {
            depleteRock()
            player.animate(-1)
        }

        return pickaxe.getMiningDelay()
    }

    private fun check(): Boolean {
        if (!World.containsObjectWithId(rock, rock.id)) {
            return false
        }

        if (player.skills.getLevel(Skills.MINING) < definition.level) {
            player.message("You need Mining level ${definition.level}.")
            return false
        }

        if (!player.inventory.hasFreeSlots()) {
            player.message("Not enough space in your inventory.")
            return false
        }

        return true
    }

    private fun hasGlory(): Boolean =
        player.equipment
            .getItem(Equipment.SLOT_AMULET.toInt())
            ?.isAnyOf("item_group.glory") == true

    private fun varrockArmourTier(): Int {
        val body = player.equipment.getItem(Equipment.SLOT_CHEST.toInt()) ?: return 0

        return when {
            body.isAnyOf("item.varrock_armour_4") -> 4
            body.isAnyOf("item.varrock_armour_3") -> 3
            body.isAnyOf("item.varrock_armour_2") -> 2
            body.isAnyOf("item.varrock_armour_1") -> 1
            else -> 0
        }
    }

    private fun canVarrockDouble(): Boolean {
        val tier = varrockArmourTier()
        if (tier == 0) return false

        return when (definition) {
            RockDefinition.CLAY,
            RockDefinition.COPPER,
            RockDefinition.TIN,
            RockDefinition.IRON,
            RockDefinition.SILVER,
            RockDefinition.COAL,
            RockDefinition.GOLD,
            -> true

            RockDefinition.MITHRIL -> tier >= 2

            RockDefinition.ADAMANTITE -> tier >= 3

            RockDefinition.RUNITE -> tier >= 4

            else -> false
        }
    }

    private fun shouldDoubleOre(): Boolean {
        if (!canVarrockDouble()) return false

        return Utils.roll(1, 10)
    }

    private fun computeChance(
        low: Int,
        high: Int,
        level: Int,
    ): Double {
        val scaled =
            low * (99 - level) / 98.0 +
                high * (level - 1) / 98.0

        return (1 + floor(scaled + 0.5)) / 256.0
    }

    private fun calculateSuccessChance(): Boolean {
        if (definition.alwaysSuccess) return true

        val level = miningLevel()

        var low = definition.lowChance
        var high = definition.highChance

        if (definition == RockDefinition.GEM && hasGlory()) {
            low = 84
            high = 210
        }

        val chance = computeChance(low, high, level)

        return Random.nextDouble() < chance.coerceAtMost(1.0)
    }

    private fun rollGemPreDrop(): Boolean {
        if (definition == RockDefinition.GEM) return false

        val chance = if (hasGlory()) 64 else 256

        if (!Utils.roll(1, chance)) return false

        val drops = MiningGemPreRoll.table.rollDrops(player, 0)

        for (drop in drops) {
            giveOrDrop(drop.itemId, drop.amount)
        }

        player.message("You find a gem.")

        return true
    }

    private fun giveOre() {
        when {
            definition.variants != null -> giveVariantOre()
            definition == RockDefinition.GEM -> giveGemOre()
            else -> giveStandardOre()
        }
    }

    private fun giveVariantOre() {
        val variants = definition.variants ?: return
        val level = miningLevel()

        for (variant in variants.asReversed()) {
            val chance =
                computeChance(
                    variant.lowChance,
                    variant.highChance,
                    level,
                )

            if (Random.nextDouble() < chance) {
                player.inventory.addItem(variant.oreId, 1)
                player.skills.addXp(Skills.MINING, variant.xp)

                player.message("You manage to mine some ${if (definition == RockDefinition.GRANITE) "granite" else "sandstone"}.")
                return
            }
        }
    }

    private fun giveGemOre() {
        val drops = GemRock().gemTable.rollDrops(player, 0)

        for (drop in drops) {
            giveOrDrop(drop.itemId, drop.amount)
        }

        player.skills.addXp(Skills.MINING, definition.xp)

        player.message("You manage to mine a gem.")

        depleteRock()
    }

    private fun giveStandardOre() {
        val oreId =
            definition.alternativeOre?.invoke(player)
                ?: definition.oreId

        val amount = if (shouldDoubleOre()) 2 else 1

        repeat(amount) {
            player.inventory.addItem(oreId, 1)
            player.skills.addXp(Skills.MINING, definition.xp)
        }
        val name = ItemDefinitions.getItemDefinitions(oreId).name
        player.message("You manage to mine some $name.")
        if (amount == 2) {
            Msg.collect(player, "Your Varrock armour allows you to mine extra ore.")
        }
    }

    private fun giveOrDrop(
        itemId: Int,
        amount: Int,
    ) {
        if (!player.inventory.canHold(itemId, amount)) {
            GroundItems.updateGroundItem(
                Item(itemId, amount),
                player.tile,
                player,
                60,
            )
        } else {
            player.inventory.addItem(itemId, amount)
        }
    }

    private fun depleteRock() {
        val nextId =
            RockDepletion.getDepletedId(rock.id)
                ?: return

        val delay = definition.respawnTicks

        World.spawnObjectTemporary(
            WorldObject(
                nextId,
                rock.type,
                rock.rotation,
                rock.x,
                rock.y,
                rock.plane,
            ),
            delay,
        )
    }

    override fun stop(player: Player) {
        player.animate(-1)
    }
}
