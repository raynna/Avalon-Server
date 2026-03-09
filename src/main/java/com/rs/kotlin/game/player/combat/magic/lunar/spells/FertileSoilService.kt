package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.WorldObject
import com.rs.java.game.player.Player
import com.rs.java.game.player.actions.skills.farming.FarmingManager

object FertileSoilService {
    fun cast(
        player: Player,
        plant: WorldObject,
    ): Boolean {
        val info =
            FarmingManager.SpotInfo.getInfo(plant.id)
                ?: return false

        val manager = player.farmingManager
        val spot = manager.getSpot(info) ?: return false

        if (!spot.isCleared) {
            player.message("The patch needs to be cleared before fertilising.")
            return false
        }

        if (spot.hasCompost()) {
            player.message("This patch is already treated with compost.")
            return false
        }

        player.animate(Animation(4413))
        player.gfx(Graphics(724))
        spot.superCompost = true
        spot.refresh()

        player.message("You enrich the soil with fertile magic.")

        return true
    }
}
