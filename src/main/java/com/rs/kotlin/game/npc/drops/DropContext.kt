package com.rs.kotlin.game.npc.drops

import com.rs.java.game.player.Player
import com.rs.kotlin.game.npc.TableCategory

data class DropContext(
    val player: Player,
    val sourceName: String,
    val sourceAction: String,
    val tableCategory: TableCategory,
    val dropSource: DropSource,
    val receivedDrop: Boolean,
)
