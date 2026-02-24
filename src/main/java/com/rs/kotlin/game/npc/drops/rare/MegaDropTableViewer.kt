package com.rs.kotlin.game.npc.drops.rare

import com.rs.kotlin.game.npc.drops.DropTablesSetup
import com.rs.kotlin.game.npc.drops.dropTable

object MegaDropTableViewer {
    val table =
        dropTable(
            name = "Mega Table",
        ) {
            mainDrops(128) {
                val entries = DropTablesSetup.megaRareTable.getEntries()

                for (entry in entries) {
                    add(entry)
                }
            }
        }
}
