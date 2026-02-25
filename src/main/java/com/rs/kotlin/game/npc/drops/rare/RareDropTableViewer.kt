package com.rs.kotlin.game.npc.drops.rare

import com.rs.kotlin.game.npc.drops.DropTablesSetup
import com.rs.kotlin.game.npc.drops.dropTable

object RareDropTableViewer {
    val table =
        dropTable(
            name = "Rare Table",
        ) {
            main(128) {
                val entries = DropTablesSetup.rareDropTable.getEntries()

                for (entry in entries) {
                    add(entry)
                }
            }
        }
}
