package com.rs.kotlin.game.npc.drops.rare

import com.rs.kotlin.game.npc.drops.DropTablesSetup
import com.rs.kotlin.game.npc.drops.dropTable

object GodwarsRareDropTableViewer {
    val table =
        dropTable(
            name = "Godwars Rare Drop Table",
        ) {
            main(128) {
                val entries = DropTablesSetup.godwarsRareDropTable.getEntries()

                for (entry in entries) {
                    if (entry.itemId > 0) {
                        add(entry)
                    }
                }
            }
        }
}
