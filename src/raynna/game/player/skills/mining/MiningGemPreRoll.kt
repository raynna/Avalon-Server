package raynna.game.player.skills.mining

import raynna.game.npc.drops.dropTable

object MiningGemPreRoll {
    val table =
        dropTable {
            main(128) {
                drop("item.uncut_sapphire", weight = 32)
                drop("item.uncut_emerald", weight = 16)
                drop("item.uncut_ruby", weight = 8)
                drop("item.uncut_diamond", weight = 2)
            }
        }.apply {
            name = "Mining pre-roll gem"
            visibleInViewer = false
        }
}
