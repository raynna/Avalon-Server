package raynna.game.player.skills.woodcutting

import raynna.game.item.Item
import raynna.game.item.ground.GroundItems
import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.util.Utils
import raynna.game.world.util.Msg
import raynna.data.rscm.Rscm

object LumberjackOutfit {
    private const val BASE_CHANCE = 1024 // original 1024 - scaled between level 1-99
    private const val MIN_CHANCE = 128 // original 128

    private val PIECE_KEYS =
        listOf(
            "item.lumberjack_hat",
            "item.lumberjack_top",
            "item.lumberjack_legs",
            "item.lumberjack_boots",
        )

    private val PIECES: List<Int> by lazy {
        PIECE_KEYS.map { Rscm.item(it) }
    }

    private fun Player.owns(itemId: Int): Boolean =
        inventory.containsOneItem(itemId) ||
            bank.containsOneItem(itemId) ||
            equipment.containsOneItem(itemId)

    private fun Player.missingPieces(): List<Int> = PIECES.filterNot { owns(it) }

    fun roll(player: Player) {
        val missing = player.missingPieces()
        if (missing.isEmpty()) return

        val level = player.skills.getRealLevel(Skills.WOODCUTTING)

        val chance = (BASE_CHANCE - level * 10).coerceAtLeast(MIN_CHANCE)

        if (!Utils.roll(1, chance)) return

        val pieceId = missing.random()

        if (player.inventory.hasFreeSlots()) {
            player.inventory.addItem(pieceId, 1)
        } else {
            GroundItems.updateGroundItem(
                Item(pieceId),
                player.location,
                player,
            )
        }
        Msg.collect(player, "You find a piece of the lumberjack outfit!")
    }

    fun xpBonus(player: Player): Double {
        val equipped = PIECES.count { player.equipment.containsOneItem(it) }

        if (equipped == 0) return 0.0

        val base = equipped * 0.01
        val fullSetBonus = if (equipped == PIECES.size) 0.01 else 0.0

        return base + fullSetBonus
    }
}
