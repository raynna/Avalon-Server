package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object RockCrabTable {

    val table =
        dropTable(rolls = 1, rareDropTable = true) {

            charmDrops {
                gold(amount = 1, percent = 29.0)
                green(amount = 1, percent = 0.905)
                crimson(amount = 1, percent = 1.81)
                blue(amount = 1, percent = 0.181)
            }

            mainDrops {//item: Any, amount: Int = 1, numerator: Int = 1, denominator: Int = 4
                drop("item.bronze_pickaxe", numerator = 6, denominator = 128)
                drop("item.iron_pickaxe", numerator = 5, denominator = 128)
                drop("item.seaweed", amount = 1, numerator = 4, denominator = 128)
                drop("item.seaweed", amount = 2, numerator = 4, denominator = 128)
                drop("item.seaweed", amount = 5, numerator = 2, denominator = 128)
                drop("item.edible_seaweed", amount = 2, numerator = 2, denominator = 128)
                drop("item.tin_ore", amount = 3, numerator = 4, denominator = 128)
                drop("item.iron_ore", numerator = 2, denominator = 128)
                drop("item.coal", amount = 2, numerator = 2, denominator = 128)
                drop("item.copper_ore", amount = 3, numerator = 2, denominator = 128)
                drop("item.oyster", amount = 2, numerator = 12, denominator = 128)
                drop("item.oyster", amount = 1, numerator = 9, denominator = 128)
                drop("item.empty_oyster", amount = 1, numerator = 3, denominator = 128)
                drop("item.empty_oyster", amount = 3, numerator = 1, denominator = 128)
                drop("item.oyster_pearl", amount = 1, numerator = 1, denominator = 128)
                drop("item.coins", amount = 4, numerator = 29, denominator = 128)
                drop("item.coins", amount = 36, numerator = 8, denominator = 128)
                drop("item.coins", amount = 8, numerator = 6, denominator = 128)
                drop("item.fishing_bait", amount = 10, numerator = 2, denominator = 128)
                drop("item.opal_bolt_tips", amount = 5, numerator = 2, denominator = 128)
                drop("item.spinach_roll", amount = 1, numerator = 1, denominator = 128)
                drop("item.casket", amount = 1, numerator = 1, denominator = 128)
            }

            tertiaryDrops {
                drop(
                    "item.scroll_box_easy",
                    numerator = 1,
                    denominator = 128,
                    condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.EASY) })
            }
        }.apply { name = "Rock Crab" }
}
