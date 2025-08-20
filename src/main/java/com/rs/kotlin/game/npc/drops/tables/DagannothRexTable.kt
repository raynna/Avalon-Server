package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object DagannothRexTable {

    val table = dropTable(rareDropTable = true, rolls = 1) {

        alwaysDrops {
            drop("item.dagannoth_bones")
            drop("item.dagannoth_hide");
        }

        mainDrops {
            /** Weapons & Armour */
            drop("item.steel_kiteshield", numerator = 17, denominator = 128)
            drop("item.mithril_warhammer", numerator = 11, denominator = 128)
            drop("item.adamant_hatchet", numerator = 7, denominator = 128)
            drop("item.steel_platebody", amount = 1, numerator = 4, denominator = 128)
            drop("item.mithril_pickaxe", amount = 1, numerator = 3, denominator = 128)
            drop("item.adamant_platebody", amount = 1, numerator = 2, denominator = 128)
            drop("item.fremennik_blade", amount = 1, numerator = 2, denominator = 128)
            drop("item.dragon_hatchet", amount = 1, numerator = 1, denominator = 128)
            drop("item.rune_hatchet", amount = 1, numerator = 1, denominator = 128)
            drop("item.fremennik_shield", amount = 1, numerator = 1, denominator = 128)
            drop("item.fremennik_helm", amount = 1, numerator = 1, denominator = 128)
            drop("item.mithril_2h_sword", amount = 1, numerator = 1, denominator = 128)
            drop("item.ring_of_life", amount = 1, numerator = 1, denominator = 128)
            drop("item.rock_shell_plate", amount = 1, numerator = 1, denominator = 128)
            drop("item.rock_shell_legs", amount = 1, numerator = 1, denominator = 128)
            drop("item.berserker_ring", amount = 1, numerator = 1, denominator = 128)
            drop("item.warrior_ring", amount = 1, numerator = 1, denominator = 128)
            /** Potions */
            drop("item.antifire_2", amount = 1, numerator = 1, denominator = 128)
            drop("item.prayer_potion_2", amount = 1, numerator = 1, denominator = 128)
            drop("item.restore_potion_2", amount = 1, numerator = 1, denominator = 128)
            drop("item.super_attack_2", amount = 1, numerator = 1, denominator = 128)
            drop("item.super_strength_2", amount = 1, numerator = 1, denominator = 128)
            drop("item.super_defence_2", amount = 1, numerator = 1, denominator = 128)
            drop("item.zamorak_brew_2", amount = 1, numerator = 1, denominator = 128)
            /** Ores & Bars */
            drop("item.mithril_ore_noted", amount = 25, numerator = 10, denominator = 128)
            drop("item.adamant_bar", amount = 1, numerator = 3, denominator = 128)
            drop("item.coal_noted", amount = 100, numerator = 2, denominator = 128)
            drop("item.iron_ore_noted", amount = 150, numerator = 1, denominator = 128)
            drop("item.steel_bar_noted", amount = 15..30, numerator = 1, denominator = 128)
            /** Other */
            drop("item.coins", amount = 100..1209, numerator = 10, denominator = 128)
            drop("item.grimy_ranarr", amount = 1, numerator = 7, denominator = 128)
            drop("item.bass", amount = 5, numerator = 7, denominator = 128)
            drop("item.swordfish", amount = 5, numerator = 4, denominator = 128)
            drop("item.shark", amount = 5, numerator = 1, denominator = 128)
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
    }.apply { name = "Dagannoth Rex" }
}
