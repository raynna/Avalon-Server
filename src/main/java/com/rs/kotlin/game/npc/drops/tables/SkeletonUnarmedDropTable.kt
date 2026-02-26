package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object SkeletonUnarmedDropTable {
    val table =
        dropTable(
            category = TableCategory.REGULAR,
            herbTables = HerbTableConfig(21, 128),
            gemTable = GemTableConfig(1, 128),
        ) {
            always {
                drop("item.bones")
            }
            main(128) {
                drop("item.bronze_arrow", amount = 2, weight = 7)
                drop("item.bronze_arrow", amount = 5, weight = 4)
                drop("item.iron_arrow", amount = 1, weight = 4)
                drop("item.air_rune", amount = 15, weight = 2)
                drop("item.earth_rune", amount = 3, weight = 2)
                drop("item.fire_rune", amount = 2, weight = 2)
                drop("item.chaos_rune", amount = 3, weight = 2)
                drop("item.nature_rune", amount = 3, weight = 1)
                drop("item.steel_arrow", amount = 1, weight = 1)

                drop("item.coins", amount = 2, weight = 18)
                drop("item.coins", amount = 12, weight = 15)
                drop("item.coins", amount = 4, weight = 7)
                drop("item.coins", amount = 16, weight = 4)
                drop("item.coins", amount = 25, weight = 4)
                drop("item.coins", amount = 33, weight = 4)
                drop("item.coins", amount = 48, weight = 1)

                drop("item.iron_dagger", amount = 1, weight = 6)
                drop("item.fire_talisman", amount = 1, weight = 2)
                drop("item.iron_ore", amount = 1, weight = 1)
                drop("item.grain", amount = 1, weight = 1)
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
            name = "Skeleton (Unarmed)"
        }
}
