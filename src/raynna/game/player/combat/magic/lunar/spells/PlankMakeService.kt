package raynna.game.player.combat.magic.lunar.spells

import raynna.game.Animation
import raynna.game.Graphics
import raynna.game.item.Item
import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.util.Utils

object PlankMakeService {
    enum class Plank(
        val baseId: Int,
        val newId: Int,
        val cost: Int,
    ) {
        REGULAR_PLANK(1511, 960, 175),
        OAK_PLANK(1521, 8778, 225),
        TEAK_PLANK(6333, 8780, 225),
        MAHOGANY_PLANK(6332, 8782, 225),
        LIVID_FARM_PLANK(20702, 20703, -1),
        ;

        companion object {
            fun forLog(id: Int): Plank? = Plank.entries.firstOrNull { it.baseId == id }
        }
    }

    fun isLog(id: Int): Boolean = id == 1511 || id == 1521 || id == 6333 || id == 6332

    fun cast(
        player: Player,
        itemId: Int,
        slotId: Int,
        lividFarm: Boolean,
    ): Boolean {
        val plank = Plank.forLog(itemId)

        if (plank == null) {
            if (!lividFarm) {
                player.message("You can only convert: plain, oak, teak and mahogany logs into planks.")
            }
            return false
        }

        if (!player.canBuy(plank.cost)) {
            player.message("You need at least ${plank.cost} coins to cast this spell on this log.")
            return false
        }

        player.lock(1)
        player.animate(Animation(6298))
        player.gfx(Graphics(1063))

        player.inventory.deleteItem(slotId, Item(plank.baseId))
        player.addItem(plank.newId, 1)

        if (!lividFarm) {
            player.interfaceManager.openGameTab(7)
        }
        return true
    }
}
