package raynna.game.player.combat.magic.modern.spells

import raynna.game.Animation
import raynna.game.Graphics
import raynna.game.item.Item
import raynna.game.player.Player
import raynna.game.player.Skills

object SuperHeatService {
    data class OreData(
        val oreId: Int,
        val barId: Int,
        val smithXp: Double,
        val smithLevel: Int,
        val requirementId: Int? = null,
        val requirementAmount: Int = 0,
    )

    private val ores =
        listOf(
            OreData(436, 2349, 6.25, 1, 438, 1), // copper + tin → bronze
            OreData(438, 2349, 6.25, 1, 436, 1), // tin + copper → bronze
            OreData(440, 2351, 12.5, 15), // iron
            OreData(442, 2355, 13.67, 20), // silver
            OreData(453, 2353, 17.5, 15, 440, 2), // coal + iron → steel
            OreData(444, 2357, 22.5, 40), // gold
            OreData(447, 2359, 30.0, 50, 453, 4), // mithril
            OreData(449, 2361, 37.5, 70, 453, 6), // adamant
            OreData(451, 2363, 50.0, 85, 453, 8), // rune
        )

    /** O(1) ore lookup */
    private val oreLookup = ores.associateBy { it.oreId }

    fun cast(
        player: Player,
        itemId: Int,
        slotId: Int,
    ): Boolean {
        if (player.hasSpellDelay()) return false

        val ore =
            oreLookup[itemId] ?: run {
                player.message("You can only cast this spell on ores.")
                return false
            }

        if (player.skills.getLevel(Skills.SMITHING) < ore.smithLevel) {
            player.message("You need level ${ore.smithLevel} Smithing to melt this.")
            return false
        }

        ore.requirementId?.let {
            if (!player.inventory.containsItem(it, ore.requirementAmount)) {
                player.message("You don't have enough ${Item(it).name}.")
                return false
            }
            player.inventory.deleteItem(it, ore.requirementAmount)
        }

        player.castSpellDelay(3)

        player.inventory.deleteItem(slotId, Item(itemId))
        player.inventory.addItem(ore.barId, 1)

        player.skills.addXp(Skills.SMITHING, ore.smithXp)

        player.animate(Animation(722))
        player.gfx(Graphics(148, 0, 100))

        player.lock(1)

        return true
    }
}
