package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager.TickKeys
import com.rs.java.utils.Utils

object DisruptionShieldService {
    fun cast(player: Player): Boolean {
        if (player.tickManager.isActive(TickKeys.DISRUPTION_SHIELD)) {
            player.message("You can only cast disruption shield every 1 minute.")
            return false
        }
        player.gfx(Graphics(1320, 0, 100))
        player.animate(Animation(8770))

        player.setDisruption(true)
        player.tickManager.addTicks(TickKeys.DISRUPTION_SHIELD, 96) {
            player.message("You can now cast disruption shield again.")
        }
        player.message("You cast a Disruption Shield.")

        return true
    }
}
