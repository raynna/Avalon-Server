package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.item.meta.RuneEssencePouchMetaData
import com.rs.java.game.player.Player

object RepairRunePouchService {
    fun cast(player: Player): Boolean {
        var repaired = false

        for (item in player.inventory.items.containerItems) {
            val meta = item?.metadata as? RuneEssencePouchMetaData ?: continue

            if (meta.isDegraded) {
                meta.repair()
                repaired = true
            }
        }

        if (!repaired) {
            player.message("None of your rune pouches need repairing.")
            return false
        }

        player.animate(Animation(6297))
        player.gfx(Graphics(1062))

        return true
    }
}
