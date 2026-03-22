package raynna.game.npc.drops.rare

import raynna.game.npc.drops.DropTablesSetup
import raynna.game.npc.drops.dropTable

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
