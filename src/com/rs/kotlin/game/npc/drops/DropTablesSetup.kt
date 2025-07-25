package com.rs.kotlin.game.npc.drops

import com.rs.kotlin.game.npc.drops.DropTableRegistry.registerDropTable
import com.rs.kotlin.game.npc.drops.tables.BlackDragonDropTable

object DropTablesSetup {

    lateinit var rareDropTable: RareDropTableEntry
        private set
    lateinit var gemDropTable: GemTableEntry
        private set
    lateinit var superRareTable: SuperRareTableEntry
        private set

    @JvmStatic
    fun setup() {
        rareDropTable = RareDropTableEntry()
        gemDropTable = GemTableEntry()
        superRareTable = SuperRareTableEntry()
        val blackDragon = BlackDragonDropTable.table
        registerDropTable(blackDragon, 50, 52, 54)

        DropTableRegistry.logDropTableSizes();
    }
}
