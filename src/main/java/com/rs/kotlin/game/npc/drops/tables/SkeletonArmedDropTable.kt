package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object SkeletonArmedDropTable {
    val table =
        dropTable(
            category = TableCategory.REGULAR,
            herbTables = HerbTableConfig(20, 128),
            gemTable = GemTableConfig(2, 128),
        ) {
            always {
                drop("item.bones")
            }
            main(128) {
                drop("item.iron_helm", weight = 6)
                drop("item.iron_sword", weight = 4)
                drop("item.iron_hatchet", weight = 2)
                drop("item.iron_scimitar", weight = 1)

                drop("item.air_rune", amount = 12, weight = 3)
                drop("item.air_rune", amount = 15, weight = 3)
                drop("item.water_rune", amount = 9, weight = 3)
                drop("item.chaos_rune", amount = 5, weight = 3)
                drop("item.iron_arrow", amount = 12, weight = 2)
                drop("item.law_rune", amount = 2, weight = 2)
                drop("item.cosmic_rune", amount = 2, weight = 1)

                drop("item.coins", amount = 10, weight = 24)
                drop("item.coins", amount = 5, weight = 25)
                drop("item.coins", amount = 25, weight = 8)
                drop("item.coins", amount = 45, weight = 4)
                drop("item.coins", amount = 65, weight = 3)
                drop("item.coins", amount = 1, weight = 2)

                drop("item.bronze_bar", amount = 1, weight = 5)
            }
            tertiary {
                drop(
                    "item.scroll_box_easy",
                    numerator = 1,
                    denominator = 100,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.EASY) },
                )
            }

            charm {
                gold(amount = 1, percent = 1.08)
                green(amount = 1, percent = 0.54)
                crimson(amount = 1, percent = 1.08)
                blue(amount = 1, percent = 1.73)
            }
        }.apply {
            name = "Skeleton (Armed)"
        }
}
