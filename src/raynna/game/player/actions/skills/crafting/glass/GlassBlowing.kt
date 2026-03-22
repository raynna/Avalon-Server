package raynna.game.player.actions.skills.crafting.glass

import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.Animation
import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.game.player.actions.Action
import raynna.data.rscm.Rscm

class GlassBlowing(
    data: GlassBlowingData,
    option: Int,
    private var quantity: Int,
) : Action() {
    private val product: GlassProduct = data.products[option]

    private val baseId: Int =
        data.baseRef as? Int ?: Rscm.lookup(data.baseRef as String)

    private val productId: Int =
        product.idRef as? Int ?: Rscm.lookup(product.idRef as String)

    override fun start(player: Player): Boolean = check(player)

    private fun check(player: Player): Boolean {
        if (!player.hasTool(GLASSBLOWING_PIPE)) {
            player.message("You need a glassblowing pipe to do that.")
            return false
        }

        if (player.skills.getLevel(Skills.CRAFTING) < product.level) {
            player.message("You need a Crafting level of ${product.level}.")
            return false
        }

        if (!player.inventory.containsItem(baseId, 1)) {
            val name = ItemDefinitions.getItemDefinitions(baseId).name
            player.message("You need some $name to do that.")
            return false
        }

        return true
    }

    override fun process(player: Player): Boolean {
        if (quantity <= 0) {
            return false
        }

        if (!check(player)) {
            return false
        }

        player.animate(Animation(BLOW_ANIMATION))
        return true
    }

    override fun processWithDelay(player: Player): Int {
        quantity--

        player.inventory.deleteItem(baseId, 1)
        player.inventory.addItem(productId, 1)

        player.skills.addXp(Skills.CRAFTING, product.xp)

        return 3
    }

    override fun stop(player: Player) {
        setActionDelay(player, 3)
    }

    companion object {
        const val GLASSBLOWING_PIPE = 1785
        private const val BLOW_ANIMATION = 884
    }
}
