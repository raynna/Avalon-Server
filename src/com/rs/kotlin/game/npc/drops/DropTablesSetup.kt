package com.rs.kotlin.game.npc.drops

import com.rs.kotlin.Rscm
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
    var goblinsLvl1 = listOf("npc.goblin_lv1")
    var goblinsLvl2 = listOf(
        "npc.goblin_lv2", "npc.goblin_lv2_11232", "npc.goblin_lv2_11233",
        "npc.goblin_lv2_11234", "npc.goblin_lv2_11235", "npc.goblin_lv2_11236",
        "npc.goblin_lv2_11237", "npc.goblin_lv2_11238", "npc.goblin_lv2_11239",
        "npc.goblin_lv2_4262", "npc.goblin_lv2_4263", "npc.goblin_lv2_4264",
        "npc.goblin_lv2_4265", "npc.goblin_lv2_4266", "npc.goblin_lv2_4267",
        "npc.goblin_lv2_4268", "npc.goblin_lv2_4269", "npc.goblin_lv2_4270",
        "npc.goblin_lv2_4271", "npc.goblin_lv2_4272", "npc.goblin_lv2_4273",
        "npc.goblin_lv2_4274", "npc.goblin_lv2_4275", "npc.goblin_lv2_4276",
        "npc.goblin_lv2_8637", "npc.goblin_lv2_8638", "npc.goblin_lv2_8862",
        "npc.goblin_lv2_11240", "npc.goblin_lv2_11241", "npc.goblin_lv2_12353",
        "npc.goblin_lv2_12354", "npc.goblin_lv2_12355", "npc.goblin_lv2_12356",
        "npc.goblin_lv2_12357", "npc.goblin_lv2_12358", "npc.goblin_lv2_12359",
        "npc.goblin_lv2_12360", "npc.goblin_lv2_12361", "npc.goblin_lv2_13091",
        "npc.goblin_lv2_13092", "npc.goblin_lv2_13093", "npc.goblin_lv2_13094")
    var goblinsLevel5 = listOf(
        "npc.goblin", "npc.goblin_lv5", "npc.goblin_lv5_3265",
        "npc.goblin_lv5_3266", "npc.goblin_lv5_3267", "npc.goblin_lv5_11260",
        "npc.goblin_lv5_13095", "npc.goblin_lv5_4407", "npc.goblin_lv5_4479",
        "npc.goblin_lv5_4480", "npc.goblin_lv5_4481", "npc.goblin_lv5_4482",
        "npc.goblin_lv5_4483", "npc.goblin_lv5_4484", "npc.goblin_lv5_4485",
        "npc.goblin_lv5_4486", "npc.goblin_lv5_4487", "npc.goblin_lv5_4488",
        "npc.goblin_lv5_4489", "npc.goblin_lv5_4490", "npc.goblin_lv5_4491",
        "npc.goblin_lv5_4492", "npc.goblin_lv5_4499", "npc.goblin_lv5_13095",
        "npc.goblin_lv5_13096", "npc.goblin_lv5_13098", "npc.goblin_lv5_13099")
    var goblinsLevel11 = listOf("npc.goblin_lv11", "npc.goblin_lv11_11259")
    var goblinsLevel12 = listOf("npc.goblin_lv12", "npc.goblin_lv12_6281")
    var goblinsLevel13 = listOf("npc.goblin_lv13", "npc.goblin_lv13_6283", "npc.goblin_lv13_11261", "npc.goblin_lv13_13097")
    var goblinsLevel15 = listOf("npc.goblin_lv15")
    var goblinsLevel16 = listOf("npc.goblin_lv16", "npc.goblin_lv16_4412")
    var goblinsLevel17 = listOf("npc.goblin_lv17")
    var goblinsLevel25 = listOf("npc.goblin_lv25")


    @JvmStatic
    fun setup() {
        rareDropTable = RareDropTableEntry()
        gemDropTable = GemTableEntry()
        superRareTable = SuperRareTableEntry()
        herbDropTable = HerbTableEntry()
        registerDropTable(BlackDragonDropTable.table, Rscm.lookup("npc.black_dragon_lv227"))
        registerDropTable(GoblinDropTable.table, Rscm.lookupList("npc_group.goblin_lv1"), Rscm.lookupList("npc_group.goblin_lv2"))
        registerDropTable(GoblinLvl5DropTable.table,
            Rscm.lookupList("npc_group.goblin_lv5"),
            Rscm.lookupList("npc_group.goblin_lv11"),
            Rscm.lookupList("npc_group.goblin_lv12"),
            Rscm.lookupList("npc_group.goblin_lv13"),
            Rscm.lookupList("npc_group.goblin_lv15"),
            Rscm.lookupList("npc_group.goblin_lv16"),
            Rscm.lookupList("npc_group.goblin_lv17"),
            Rscm.lookupList("npc_group.goblin_lv25"))

        DropTableRegistry.logDropTableSizes();
    }
}
