package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.player.Player
import com.rs.java.game.player.TickManager.TickKeys

object MagicImbueService {
    fun cast(player: Player): Boolean {
        val active = player.tickManager.isActive(TickKeys.MAGIC_IMBUE)

        if (active) {
            player.message("You can only cast this spell every 12 seconds.")
            return false
        }
        player.message("You are charged to combine runes.")
        player.tickManager.addTicks(TickKeys.MAGIC_IMBUE, 21) {
            player.message("Magic Imbue spell has run out.")
        }
        return true
    }
}
