package com.rs.kotlin.game.npc.drops

import com.rs.kotlin.game.npc.drops.DropTableRegistry.registerItemTable
import com.rs.kotlin.game.npc.drops.DropTableRegistry.registerNamedTable
import com.rs.kotlin.game.npc.drops.DropTableRegistry.registerNpcGroupDropTable
import com.rs.kotlin.game.npc.drops.DropTableRegistry.registerNpcKeyDropTable
import com.rs.kotlin.game.npc.drops.DropTableRegistry.registerObjectTable
import com.rs.kotlin.game.npc.drops.tables.*
import com.rs.kotlin.game.npc.worldboss.WorldBossTable

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

        /**
         * Shared Sub Tables
         **/

        rareDropTable = RareDropTableEntry()
        gemDropTable = GemTableEntry()
        superRareTable = SuperRareTableEntry()
        herbDropTable = HerbTableEntry()

        /**
         * Special / Global Tables
         **/

        registerNamedTable("World Boss", WorldBossTable.regular)
        registerItemTable("item.magic_chest", WorldBossTable.chest)
        registerObjectTable(
            name = "Barrows",
            objectId = 10284,
            BarrowsChestTable.BARROWS_CHEST_TABLE
        )

        /**
         * God Wars Dungeon
         **/

        registerNpcKeyDropTable(GeneralGraardorTable.table, "npc.general_graardor_lv624")
        registerNpcKeyDropTable(KreeArraDropTable.table, "npc.kree_arra_lv580")
        registerNpcKeyDropTable(CommanderZilyanaDropTable.table, "npc.commander_zilyana_lv596")
        registerNpcKeyDropTable(KrilTsutsarothDropTable.table, "npc.k_ril_tsutsaroth_lv650")

        /**
         * Bosses
         **/

        registerNpcKeyDropTable(TzTokJadTable.table, "npc.tztok_jad_lv702")
        registerNpcKeyDropTable(KingBlackDragonDropTable.kingBlackDragonTable, "npc.king_black_dragon_lv276")
        registerNpcKeyDropTable(ChaosElementalDropTable.table, "npc.chaos_elemental_lv305")

        registerNpcGroupDropTable(KalphiteQueenDropTable.table, "npc_group.kalphite_queen_lv333")

        /**
         * Dagannoths
         **/

        registerNpcKeyDropTable(DagannothRexTable.table, "npc.dagannoth_rex_lv303")
        registerNpcKeyDropTable(DagannothPrimeTable.table, "npc.dagannoth_prime_lv303")
        registerNpcKeyDropTable(DagannothSupremeTable.table, "npc.dagannoth_supreme_lv303")

        /**
         * Dragons
         **/

        registerNpcGroupDropTable(GreenDragonDropTable.greenDragonTable, "npc_group.green_dragon_lv79")
        registerNpcGroupDropTable(BlackDragonDropTable.blackDragonTable, "npc_group.black_dragon_lv227")

        /**
         * Slayer Monsters
         **/

        registerNpcGroupDropTable(CrawlingHandTable.table,
            "npc_group.crawling_hand",
            "npc_group.crawling_hand_lv7",
            "npc_group.crawling_hand_lv8",
            "npc_group.crawling_hand_lv11",
            "npc_group.crawling_hand_lv12"
        )

        registerNpcGroupDropTable(BansheeTable.table, "npc_group.banshee_lv23")
        registerNpcGroupDropTable(HellhoundTable.table, "npc_group.hellhound_lv122")

        registerNpcGroupDropTable(AbyssalDemonTable.table,
            "npc_group.abyssal_demon",
            "npc_group.abyssal_demon_lv124"
        )

        registerNpcKeyDropTable(DarkBeastTable.darkBeasts, "npc.dark_beast_lv182")
        registerNpcGroupDropTable(GargoyleDropTable.table, "npc_group.gargoyle_lv111")
        registerNpcGroupDropTable(NechryaelDropTable.table, "npc_group.nechryael_lv115")

        registerNpcGroupDropTable(BloodveldDropTable.table,
            "npc_group.bloodveld_lv76",
            "npc_group.bloodveld_lv81"
        )

        registerNpcGroupDropTable(InfernalMageDropTable.table, "npc_group.infernal_mage_lv66")
        registerNpcGroupDropTable(AberrantSpectreDropTable.table, "npc_group.aberrant_spectre_lv96")

        /**
         * Low-Level Monsters
         **/

        registerNpcGroupDropTable(GoblinDropTable.table,
            "npc_group.goblin_lv1",
            "npc_group.goblin_lv2"
        )

        registerNpcGroupDropTable(GoblinLvl5DropTable.table,
            "npc_group.goblin_lv5",
            "npc_group.goblin_lv11",
            "npc_group.goblin_lv12",
            "npc_group.goblin_lv13",
            "npc_group.goblin_lv15",
            "npc_group.goblin_lv16",
            "npc_group.goblin_lv17",
            "npc_group.goblin_lv25"
        )

        registerNpcGroupDropTable(RockCrabTable.table, "npc_group.rock_crab_lv13")
        registerNpcGroupDropTable(CyclopsDropTable.table,
            "npc_group.cyclops_lv56",
            "npc_group.cyclops_lv76"
        )

        /**
         * Revenants
         */
        registerNpcGroupDropTable(RevenantDropTable.table,
            "npc_group.revenant_imp_lv7",
            "npc_group.revenant_goblin_lv15",
            "npc_group.revenant_goblin_lv22",
            "npc_group.revenant_goblin_lv30",
            "npc_group.revenant_goblin_lv37",
            "npc_group.revenant_icefiend_lv45",
            "npc_group.revenant_pyrefiend_lv52",
            "npc_group.revenant_hobgoblin_lv60",
            "npc_group.revenant_vampyre_lv68",
            "npc_group.revenant_werewolf_lv75",
            "npc_group.revenant_cyclops_lv82",
            "npc_group.revenant_hellhound_lv90",
            "npc_group.revenant_demon_lv98",
            "npc_group.revenant_ork_lv105",
            "npc_group.revenant_dark_beast_lv120",
            "npc_group.revenant_knight_lv126",
            "npc_group.revenant_dragon_lv135")

        /**
         * Kalphites
         **/

        registerNpcGroupDropTable(KalphiteGuardianDropTable.table, "npc_group.kalphite_guardian_lv141")
        registerNpcGroupDropTable(KalphiteSoldierDropTable.table, "npc_group.kalphite_soldier_lv85")
        registerNpcGroupDropTable(KalphiteWorkerDropTable.table, "npc_group.kalphite_worker_lv28")

        DropTableRegistry.logDropTableSizes()
    }
}
