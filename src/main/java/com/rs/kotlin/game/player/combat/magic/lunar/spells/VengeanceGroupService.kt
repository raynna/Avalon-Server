package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.World
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager

object VengeanceGroupService {
    fun cast(player: Player): Boolean {
        if (player.tickManager.isActive(TickManager.TickKeys.VENGEANCE_COOLDOWN)) {
            player.message("You can only cast vengeance every 30 seconds.")
            return false
        }

        if (!player.isAtMultiArea) {
            player.message("You need to be in a multi area for this spell.")
            return false
        }

        var count = 0

        for (other in World.getPlayers()) {
            if (other == null) continue

            if (other.withinDistance(player, 6) &&
                other.isAcceptAid &&
                other.isAtMultiArea &&
                !other.tickManager.isActive(TickManager.TickKeys.VENGEANCE_COOLDOWN)
            ) {
                other.message("${player.displayName} cast the Group Vengeance spell and you were affected!")

                other.gfx(Graphics(725, 0, 100))
                other.setVengeance(true)

                other.tickManager.addSeconds(
                    TickManager.TickKeys.VENGEANCE_COOLDOWN,
                    30,
                )

                count++
            }
        }

        player.message("The spell affected $count nearby people.")

        player.gfx(Graphics(725, 0, 100))
        player.animate(Animation(4411))

        player.setVengeance(true)

        player.tickManager.addSeconds(
            TickManager.TickKeys.VENGEANCE_COOLDOWN,
            30,
        ) {
            player.message("You can now cast vengeance again.")
        }

        return true
    }
}
