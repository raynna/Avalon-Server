package raynna.game.npc.drops

import raynna.game.player.Player
import raynna.game.npc.TableCategory

data class DropContext(
    val player: Player,
    val sourceName: String,
    val sourceAction: String,
    val tableCategory: TableCategory,
    val dropSource: DropSource,
    val receivedDrop: Boolean,
)
