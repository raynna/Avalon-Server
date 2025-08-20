package com.rs.kotlin.game.npc.drops

import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.drops.DropTableRegistry.registerDropTable
import com.rs.kotlin.game.npc.drops.tables.*

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
        registerDropTable(BlackDragonDropTable.table, Rscm.lookupList("npc_group.black_dragon_lv227"))
        registerDropTable(GoblinDropTable.table, Rscm.lookupList("npc_group.goblin_lv1"), Rscm.lookupList("npc_group.goblin_lv2"))
        registerDropTable(
            GoblinLvl5DropTable.table,
            Rscm.lookupList("npc_group.goblin_lv5"),
            Rscm.lookupList("npc_group.goblin_lv11"),
            Rscm.lookupList("npc_group.goblin_lv12"),
            Rscm.lookupList("npc_group.goblin_lv13"),
            Rscm.lookupList("npc_group.goblin_lv15"),
            Rscm.lookupList("npc_group.goblin_lv16"),
            Rscm.lookupList("npc_group.goblin_lv17"),
            Rscm.lookupList("npc_group.goblin_lv25"))
        registerDropTable(RockCrabTable.table, Rscm.lookupList("npc_group.rock_crab_lv13"))
        registerDropTable(DagannothRexTable.table, Rscm.lookup("npc.dagannoth_rex_lv303"))
        registerDropTable(DagannothPrimeTable.table, Rscm.lookup("npc.dagannoth_prime_lv303"))
        registerDropTable(DagannothSupremeTable.table, Rscm.lookup("npc.dagannoth_supreme_lv303"))

        DropTableRegistry.logDropTableSizes();
    }
}
