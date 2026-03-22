package raynna.game.npc.drops.rare

import raynna.game.npc.drops.DropTablesSetup
import raynna.game.npc.drops.dropTable

object GemDropTableViewer {
    val table =
        dropTable(
            name = "Gem Table",
        ) {
            main(128) {
                val entries = DropTablesSetup.gemDropTable.getEntries()

                for (entry in entries) {
                    add(entry)
                }
            }
        }
}
