package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object DagannothPrimeTable {

    val table = dropTable(rareDropTable = true, rolls = 1) {

        alwaysDrops {
            drop("item.dagannoth_bones")
            drop("item.dagannoth_hide");
        }

        mainDrops {
            /** Weapons & Armour */
            drop("item.earth_battlestaff", numerator = 10, denominator = 128)
            drop("item.water_battlestaff", numerator = 5, denominator = 128)
            drop("item.air_battlestaff", numerator = 4, denominator = 128)
            drop("item.battlestaff", amount = 1..10, numerator = 1, denominator = 128)
            drop("item.fremennik_blade", amount = 1, numerator = 1, denominator = 128)
            drop("item.fremennik_shield", amount = 1, numerator = 1, denominator = 128)
            drop("item.fremennik_helm", amount = 1, numerator = 1, denominator = 128)
            drop("item.mud_battlestaff", amount = 1, numerator = 1, denominator = 128)
            drop("item.dragon_hatchet", amount = 1, numerator = 1, denominator = 128)
            drop("item.farseer_helm", amount = 1, numerator = 1, denominator = 128)
            drop("item.skeletal_top", amount = 1, numerator = 1, denominator = 128)
            drop("item.skeletal_bottoms", amount = 1, numerator = 1, denominator = 128)
            drop("item.seers_ring", amount = 1, numerator = 1, denominator = 128)
            /** Runes */
            drop("item.air_rune", amount = 100..200, numerator = 6, denominator = 128)
            drop("item.earth_rune", amount = 50..100, numerator = 5, denominator = 128)
            drop("item.blood_rune", amount = 25..75, numerator = 2, denominator = 128)
            drop("item.law_rune", amount = 10..75, numerator = 2, denominator = 128)
            drop("item.nature_rune", amount = 25..75, numerator = 2, denominator = 128)
            drop("item.mud_rune", amount = 25..75, numerator = 2, denominator = 128)
            drop("item.death_rune", amount = 25..85, numerator = 2, denominator = 128)
            /** Talismans */
            drop("item.earth_talisman_noted", amount = 25..75, numerator = 10, denominator = 128)
            drop("item.air_talisman_noted", amount = 25..75, numerator = 7, denominator = 128)
            drop("item.water_talisman_noted", amount = 1..76, numerator = 7, denominator = 128)
            /** Other */
            drop("item.shark", amount = 5, numerator = 10, denominator = 128)
            drop("item.oyster_pearls", amount = 1, numerator = 5, denominator = 128)
            drop("item.pure_essence_noted", amount = 150, numerator = 5, denominator = 128)
            drop("item.grimy_ranarr", amount = 1, numerator = 5, denominator = 128)
            drop("item.coins", amount = 500..1109, numerator = 3, denominator = 128)
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
    }.apply { name = "Dagannoth Prime" }
}
