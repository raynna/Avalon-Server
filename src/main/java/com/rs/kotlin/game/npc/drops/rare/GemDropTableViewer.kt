package com.rs.kotlin.game.npc.drops.rare

import com.rs.kotlin.game.npc.drops.DropTablesSetup
import com.rs.kotlin.game.npc.drops.dropTable

object GemDropTableViewer {
    val table =
        dropTable(
            name = "Gem Table",
        ) {
            mainDrops(128) {
                val entries = DropTablesSetup.gemDropTable.getEntries()

                for (entry in entries) {
                    add(entry)
                }
            }
        }
}
