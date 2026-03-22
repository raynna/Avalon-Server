package raynna.game.player.combat.magic.lunar.spells

import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.Animation
import raynna.game.Graphics
import raynna.game.player.Player
import raynna.game.player.Skills

object BakePieService {
    data class PieRecipe(
        val unbakedId: Int,
        val bakedId: Int,
        val cookingXp: Double,
        val level: Int,
    )

    private val recipes =
        listOf(
            PieRecipe(2319, 2327, 110.0, 20), // meat pie
            PieRecipe(2321, 2325, 72.0, 10), // berry pie
        )

    fun cast(player: Player): Boolean {
        val recipe =
            recipes.firstOrNull { player.inventory.containsItem(it.unbakedId, 1) }
                ?: run {
                    player.message("You don't have any pies to bake.")
                    return false
                }

        if (player.skills.getLevel(Skills.COOKING) < recipe.level) {
            player.message("You need a cooking level of ${recipe.level} to cook this pie.")
            return false
        }

        player.lock(4)

        player.animate(Animation(4413))
        player.gfx(Graphics(746, 0, 96 shl 16))

        player.inventory.deleteItem(recipe.unbakedId, 1)
        player.inventory.addItem(recipe.bakedId, 1)

        player.skills.addXp(Skills.COOKING, recipe.cookingXp)

        player.message(
            "Your spell bakes the ${
                ItemDefinitions.getItemDefinitions(recipe.unbakedId).name
            }.",
        )

        return true
    }
}
