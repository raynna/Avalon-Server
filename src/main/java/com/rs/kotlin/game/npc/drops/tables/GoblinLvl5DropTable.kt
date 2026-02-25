package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object GoblinLvl5DropTable {
    val table =
        dropTable(herbTables = HerbTableConfig(numerator = 2, 128)) {

            always {
                drop("item.bones")
            }

            charm {
                gold(amount = 1, percent = 8.6)
                green(amount = 1, percent = 0.269)
                crimson(amount = 1, percent = 0.538)
                blue(amount = 1, percent = 0.0537)
            }

            main(128) {
                drop("item.bronze_spear", weight = 9)
                drop("item.bronze_hatchet", weight = 3)
                drop("item.bronze_scimitar", weight = 1)
                drop("item.bronze_arrow", amount = 7, weight = 3)
                drop("item.mind_rune", amount = 2, weight = 3)
                drop("item.earth_rune", amount = 4, weight = 3)
                drop("item.body_rune", amount = 2, weight = 3)
                drop("item.bronze_javelin", amount = 5, weight = 2)
                drop("item.chaos_rune", weight = 1)
                drop("item.nature_rune", weight = 1)
                drop("item.coins", amount = 1, weight = 34)
                drop("item.coins", amount = 3, weight = 13)
                drop("item.coins", amount = 5, weight = 8)
                drop("item.coins", amount = 16, weight = 7)
                drop("item.coins", amount = 24, weight = 3)
                drop("item.goblin_mail", weight = 10)
                drop("item.hammer", weight = 9)
                drop("item.goblin_book", weight = 2)
                drop("item.grapes", weight = 1)
                drop("item.red_cape", weight = 1)
                drop("item.tin_ore", weight = 1)
            }

            prerollDenom {
                drop(
                    "item.goblin_skull",
                    condition = { player -> false }, // TODO RAG AND BONE MAN QUEST
                )
            }

            tertiary {
                drop(
                    "item.scroll_box_easy",
                    numerator = 1,
                    denominator = 128,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.EASY) },
                )
                drop(
                    "item.sling",
                    numerator = 1,
                    denominator = 128,
                )
                drop(
                    "item.staff_of_air",
                    numerator = 1,
                    denominator = 128,
                )
                drop(
                    "item.goblin_champion_scroll",
                    numerator = 1,
                    denominator = 5000,
                )
            }
        }.apply { name = "Higher Level Goblin" }
}
