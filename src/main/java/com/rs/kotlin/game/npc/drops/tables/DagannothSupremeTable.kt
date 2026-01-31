package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.MonsterCategory
import com.rs.kotlin.game.npc.drops.dropTable

object DagannothSupremeTable {

    val table = dropTable(category = MonsterCategory.BOSS, rareDropTable = true, rolls = 1) {

        alwaysDrops {
            drop("item.dagannoth_bones")
            drop("item.dagannoth_hide");
        }

        preRollDrops {
            drop("item.archers_ring", numerator = 1, denominator = 128)
            drop("item.seercull", numerator = 1, denominator = 128)
            drop("item.dragon_hatchet", numerator = 1, denominator = 128)
        }

        mainDrops(128) {
            /** Weapons & Armour */
            drop("item.mithril_knife", amount = 25..50, weight = 10)
            drop("item.red_d_hide_vambraces", weight = 7)
            drop("item.rune_thrownaxe", amount = 5..10, weight = 5)
            drop("item.adamant_dart", amount = 10..25, weight = 5)
            drop("item.iron_knife", amount = 200..500, weight = 5)
            drop("item.steel_knife", amount = 50..150, weight = 5)
            drop("item.fremennik_blade", amount = 1, weight = 1)
            drop("item.fremennik_shield", amount = 1, weight = 1)
            drop("item.fremennik_helm", amount = 1, weight = 1)
            drop("item.archer_helm", amount = 1, weight = 1)
            drop("item.spined_body", amount = 1, weight = 1)
            drop("item.spined_chaps", amount = 1, weight = 1)
            /** Ammunition */
            drop("item.steel_arrow", amount = 50..250, weight = 5)
            drop("item.runite_bolts", amount = 2..12, weight = 5)
            drop("item.iron_arrow", amount = 200..700, weight = 4)
            /** Other */
            drop("item.coins", amount = 500..1110, weight = 10)
            drop("item.oyster_pearls", amount = 1, weight = 6)
            drop("item.opal_bolt_tips", amount = 10..30, weight = 5)
            drop("item.shark", amount = 5, weight = 5)
            drop("item.yew_logs_noted", amount = 50..150, weight = 5)
            drop("item.grimy_ranarr", amount = 1, weight = 5)
            drop("item.maple_logs_noted", amount = 15..65, weight = 3)
            drop("item.runite_limbs", amount = 1, weight = 2)
            drop("item.feather", amount = 250..500, weight = 1)
        }

        tertiaryDrops {
            drop(
                "item.scroll_box_hard",
                numerator = 1,
                denominator = 42,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) })
            drop(
                "item.scroll_box_elite",
                numerator = 1,
                denominator = 750,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) })
        }
    }.apply { name = "Dagannoth Supreme" }
}
