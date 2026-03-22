package raynna.game.npc.drops.rare

import raynna.game.npc.drops.DropTablesSetup
import raynna.game.npc.drops.dropTable

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
