package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object RockCrabTable {
    val table =
        dropTable(gemTable = GemTableConfig(1, 128)) {

            charm {
                gold(amount = 1, percent = 29.0)
                green(amount = 1, percent = 0.905)
                crimson(amount = 1, percent = 1.81)
                blue(amount = 1, percent = 0.181)
            }

            main(128) {
                drop("item.bronze_pickaxe", weight = 6)
                drop("item.iron_pickaxe", weight = 5)
                drop("item.seaweed", amount = 1, weight = 4)
                drop("item.seaweed", amount = 2, weight = 4)
                drop("item.seaweed", amount = 5, weight = 2)
                drop("item.edible_seaweed", amount = 2, weight = 2)
                drop("item.tin_ore", amount = 3, weight = 4)
                drop("item.iron_ore", weight = 2)
                drop("item.coal", amount = 2, weight = 2)
                drop("item.copper_ore", amount = 3, weight = 2)
                drop("item.oyster", amount = 2, weight = 12)
                drop("item.oyster", amount = 1, weight = 9)
                drop("item.empty_oyster", amount = 1, weight = 3)
                drop("item.empty_oyster", amount = 3, weight = 1)
                drop("item.oyster_pearl", amount = 1, weight = 1)
                drop("item.coins", amount = 4, weight = 29)
                drop("item.coins", amount = 36, weight = 8)
                drop("item.coins", amount = 8, weight = 6)
                drop("item.fishing_bait", amount = 10, weight = 2)
                drop("item.opal_bolt_tips", amount = 5, weight = 2)
                drop("item.spinach_roll", amount = 1, weight = 1)
                drop("item.casket", amount = 1, weight = 1)
            }

            tertiary {
                drop(
                    "item.scroll_box_easy",
                    numerator = 1,
                    denominator = 128,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.EASY) },
                )
            }
        }.apply { name = "Rock Crab" }
}
