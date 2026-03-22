package raynna.game.player.actions.skills.crafting.gem

import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.Animation
import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.game.player.actions.Action
import raynna.game.player.tasksystem.Task

class GemCutting(
    private val product: GemProduct,
    private var quantity: Int,
) : Action() {
    override fun start(player: Player): Boolean = check(player)

    private fun check(player: Player): Boolean {
        if (!player.hasTool("item.chisel")) {
            player.message("You need a chisel to cut this item.")
            return false
        }

        if (player.skills.getLevel(Skills.CRAFTING) < product.level) {
            player.message("You need a Crafting level of ${product.level} to cut this gem.")
            return false
        }

        if (!player.inventory.containsItem(product.getUncut(), 1)) {
            val name =
                ItemDefinitions.getItemDefinitions(product.getUncut()).name.lowercase()
            player.message("You have run out of $name.")
            return false
        }

        return true
    }

    override fun process(player: Player): Boolean = quantity > 0 && check(player)

    override fun processWithDelay(player: Player): Int {
        quantity--

        player.animate(Animation(product.animation))

        player.inventory.deleteItem(product.getUncut(), 1)
        player.inventory.addItem(product.getCut(), 1)

        player.skills.addXp(Skills.CRAFTING, product.xp)

        val name =
            ItemDefinitions.getItemDefinitions(product.getUncut()).name.lowercase()

        player.message("You cut the $name.", true)

        when (product.getUncut()) {
            1623 -> player.taskManager.progress(Task.CUT_UNCUT_SAPPHIRE)
            1617 -> player.taskManager.progress(Task.CUT_UNCUT_DIAMOND)
            1631 -> player.taskManager.progress(Task.CUT_UNCUT_DRAGONSTONE)
            6571 -> player.taskManager.progress(Task.CUT_UNCUT_ONYX)
        }

        return if (quantity > 0) 1 else -1
    }

    override fun stop(player: Player) {
        setActionDelay(player, 3)
    }
}
