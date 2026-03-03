package com.rs.kotlin.game.player.skills.mining

import com.rs.kotlin.game.npc.drops.dropTable

class GemRock {
    val gemTable =
        dropTable {
            main(128) {
                drop("item.uncut_opal", weight = 60)
                drop("item.uncut_jade", weight = 30)
                drop("item.uncut_red_topaz", weight = 15)
                drop("item.uncut_sapphire", weight = 9)
                drop("item.uncut_emerald", weight = 5)
                drop("item.uncut_ruby", weight = 5)
                drop("item.uncut_diamond", weight = 4)
            }
        }.apply {
            name = "Gem rock"
            visibleInViewer = false
        }
}
