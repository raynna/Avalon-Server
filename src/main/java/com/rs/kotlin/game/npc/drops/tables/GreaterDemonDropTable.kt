package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object GreaterDemonDropTable {
    val table =
        dropTable(
            category = TableCategory.REGULAR,
            gemTable = GemTableConfig(5, 128),
        ) {
            always {
                drop("item.accursed_ashes")
            }
            main(128) {
                drop("item.steel_2h_sword", weight = 4)
                drop("item.steel_hatchet", weight = 3)
                drop("item.steel_battleaxe", weight = 3)
                drop("item.mithril_kiteshield", weight = 1)
                drop("item.adamant_platelegs", weight = 1)
                drop("item.rune_full_helm", weight = 1)

                drop("item.fire_rune", amount = 75, weight = 8)
                drop("item.chaos_rune", amount = 15, weight = 3)
                drop("item.death_rune", amount = 5, weight = 3)
                drop("item.fire_rune", amount = 37, weight = 1)

                drop("item.coins", amount = 132, weight = 40)
                drop("item.coins", amount = 44, weight = 29)
                drop("item.coins", amount = 220, weight = 10)
                drop("item.coins", amount = 11, weight = 7)
                drop("item.coins", amount = 460, weight = 1)

                drop("item.tuna", weight = 3)
                drop("item.gold_bar", weight = 1)
                drop("item.thread", weight = 1)
            }
            tertiary {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 128,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                )
            }

            charm {
                gold(amount = 1, percent = 7.2)
                green(amount = 1, percent = 3.6)
                crimson(amount = 1, percent = 25.2)
                blue(amount = 1, percent = 0.72)
            }
        }.apply {
            name = "Greater demon"
        }
}
