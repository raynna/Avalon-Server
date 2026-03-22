package raynna.game.npc.drops.rare

import raynna.game.npc.drops.DropTablesSetup
import raynna.game.npc.drops.dropTable

object MegaDropTableViewer {
    val table =
        dropTable(
            name = "Mega Table",
        ) {
            main(128) {
                val entries = DropTablesSetup.megaRareTable.getEntries()

                for (entry in entries) {
                    add(entry)
                }
            }
        }
}
