package com.rs.kotlin.game.npc.drops

import com.rs.kotlin.game.npc.drops.DropTableRegistry.registerDropTable
import com.rs.kotlin.game.npc.drops.tables.BlackDragonDropTable
import com.rs.kotlin.game.npc.drops.tables.GoblinDropTable
import com.rs.kotlin.game.npc.drops.tables.GoblinLvl5DropTable

object DropTablesSetup {

    lateinit var rareDropTable: RareDropTableEntry
        private set
    lateinit var gemDropTable: GemTableEntry
        private set
    lateinit var superRareTable: SuperRareTableEntry
        private set
    lateinit var herbDropTable: HerbTableEntry
        private set

    @JvmStatic
    fun setup() {
        rareDropTable = RareDropTableEntry()
        gemDropTable = GemTableEntry()
        superRareTable = SuperRareTableEntry()
        herbDropTable = HerbTableEntry()
        registerDropTable(BlackDragonDropTable.table, "npc.black_dragon")
        registerDropTable(GoblinDropTable.table, "npc.goblin_11232", "npc.goblin_4261", "npc.goblin_4262", "npc.goblin_4263", "npc.goblin_4264", "npc.goblin_4265")
        registerDropTable(GoblinLvl5DropTable.table, "npc.goblin", "npc.goblin_3265", "npc.goblin_3266", "npc.goblin_3267", "npc.goblin")

        DropTableRegistry.logDropTableSizes();
    }
}
