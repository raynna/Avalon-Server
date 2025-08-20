package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object DagannothSupremeTable {

    val table = dropTable(rareDropTable = true, rolls = 1) {

        alwaysDrops {
            drop("item.dagannoth_bones")
            drop("item.dagannoth_hide");
        }

        mainDrops {
            /** Weapons & Armour */
            drop("item.mithril_knife", amount = 25..50, numerator = 10, denominator = 128)
            drop("item.red_d_hide_vambraces", numerator = 7, denominator = 128)
            drop("item.rune_thrownaxe", amount = 5..10, numerator = 5, denominator = 128)
            drop("item.adamant_dart", amount = 10..25, numerator = 5, denominator = 128)
            drop("item.iron_knife", amount = 200..500, numerator = 5, denominator = 128)
            drop("item.steel_knife", amount = 50..150, numerator = 5, denominator = 128)
            drop("item.fremennik_blade", amount = 1, numerator = 1, denominator = 128)
            drop("item.fremennik_shield", amount = 1, numerator = 1, denominator = 128)
            drop("item.fremennik_helm", amount = 1, numerator = 1, denominator = 128)
            drop("item.seercull", amount = 1, numerator = 1, denominator = 128)
            drop("item.dragon_hatchet", amount = 1, numerator = 1, denominator = 128)
            drop("item.archer_helm", amount = 1, numerator = 1, denominator = 128)
            drop("item.spined_body", amount = 1, numerator = 1, denominator = 128)
            drop("item.spined_chaps", amount = 1, numerator = 1, denominator = 128)
            drop("item.archers_ring", amount = 1, numerator = 1, denominator = 128)
            /** Ammunition */
            drop("item.steel_arrow", amount = 50..250, numerator = 5, denominator = 128)
            drop("item.runite_bolts", amount = 2..12, numerator = 5, denominator = 128)
            drop("item.iron_arrow", amount = 200..700, numerator = 4, denominator = 128)
            /** Other */
            drop("item.coins", amount = 500..1110, numerator = 10, denominator = 128)
            drop("item.oyster_pearls", amount = 1, numerator = 6, denominator = 128)
            drop("item.opal_bolt_tips", amount = 10..30, numerator = 5, denominator = 128)
            drop("item.shark", amount = 5, numerator = 5, denominator = 128)
            drop("item.yew_logs_noted", amount = 50..150, numerator = 5, denominator = 128)
            drop("item.grimy_ranarr", amount = 1, numerator = 5, denominator = 128)
            drop("item.maple_logs_noted", amount = 15..65, numerator = 3, denominator = 128)
            drop("item.runite_limbs", amount = 1, numerator = 2, denominator = 128)
            drop("item.feather", amount = 250..500, numerator = 1, denominator = 128)
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
