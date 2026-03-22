package raynna.game.player.skills.fishing

import raynna.game.item.Item
import raynna.game.item.ground.GroundItems
import raynna.game.player.Player
import raynna.util.Utils
import raynna.game.world.util.Msg
import raynna.data.rscm.Rscm

object FishingOutfit {
    private const val BASE_CHANCE = 100
    private const val RARE_HAT_CHANCE = 1000 // fish hat is rarer than normal pieces

    private val PIECE_KEYS =
        listOf(
            "item.fishing_hat",
            "item.fishing_jacket",
            "item.fishing_waders",
            "item.fishing_boots",
        )

    private val RARE_PIECE_KEY = "item.fishing_hat"

    private val PIECES: List<Int> by lazy {
        PIECE_KEYS.map { Rscm.item(it) }
    }

    private val RARE_PIECE: Int by lazy {
        Rscm.item(RARE_PIECE_KEY)
    }

    private val ALL_PIECES: List<Int> by lazy {
        PIECES + RARE_PIECE
    }

    private fun Player.owns(itemId: Int): Boolean =
        inventory.containsOneItem(itemId) ||
            bank.containsOneItem(itemId) ||
            equipment.containsOneItem(itemId)

    private fun Player.missingPieces(): List<Int> = PIECES.filterNot { owns(it) }

    private fun Player.missingRarePiece(): Boolean = !owns(RARE_PIECE)

    fun roll(player: Player) {
        // Roll for the rare fish hat separately at a much lower rate
        if (player.missingRarePiece() && Utils.roll(1, RARE_HAT_CHANCE)) {
            givePiece(player, RARE_PIECE, rare = true)
            return
        }

        val missing = player.missingPieces()
        if (missing.isEmpty()) return

        if (!Utils.roll(1, BASE_CHANCE)) return

        givePiece(player, missing.random(), rare = false)
    }

    private fun givePiece(
        player: Player,
        pieceId: Int,
        rare: Boolean,
    ) {
        if (player.inventory.hasFreeSlots()) {
            player.inventory.addItem(pieceId, 1)
        } else {
            GroundItems.updateGroundItem(Item(pieceId), player.location, player)
        }
        if (rare) {
            Msg.collect(player, "You find a rare piece of the fishing outfit!")
        } else {
            Msg.collect(player, "You find a piece of the fishing outfit!")
        }
    }

    fun xpBonus(player: Player): Double {
        val equipped = PIECES.count { player.equipment.containsOneItem(it) }

        if (equipped == 0) return 0.0

        val base = equipped * 0.01
        val fullSetBonus = if (equipped == PIECES.size) 0.01 else 0.0

        return base + fullSetBonus
    }
}
