package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.World
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils

object CureGroupService {
    fun cast(player: Player): Boolean {
        if (!player.isAtMultiArea) {
            player.message("You need to be in a multi area for this spell.")
            return false
        }

        var playersAffected = 0

        for (other in World.getPlayers()) {
            if (other == null) continue
            if (other == player) continue
            if (!other.isAcceptAid) continue
            if (!other.isAtMultiArea) continue
            if (!other.poison.isPoisoned && !other.newPoison.isPoisoned()) return false
            if (!other.withinDistance(player, 4)) continue

            other.poison.reset()
            other.newPoison.reset()

            other.message("You were cured.")
            other.gfx(Graphics(745, 0, 100))
            playersAffected++
        }

        if (player.poison.isPoisoned || player.newPoison.isPoisoned()) {
            player.poison.reset()
            player.newPoison.reset()
            player.message("You were cured.")
            playersAffected++
        }
        if (playersAffected == 0) return false

        player.message("The spell affected $playersAffected player(s).")

        player.animate(Animation(4409))
        player.gfx(Graphics(744, 0, 100))

        return true
    }
}
