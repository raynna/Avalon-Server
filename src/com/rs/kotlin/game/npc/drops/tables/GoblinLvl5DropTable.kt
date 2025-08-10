package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object GoblinLvl5DropTable {

    val table =
        dropTable(herbTable = true, rolls = 1) {

            alwaysDrops {
                drop("item.bones")
            }

            charmDrops {
                gold(amount = 1, percent = 8.6)
                green(amount = 1, percent = 0.269)
                crimson(amount = 1, percent = 0.538)
                blue(amount = 1, percent = 0.0537)
            }

            mainDrops {//item: Any, amount: Int = 1, numerator: Int = 1, denominator: Int = 4
                drop("item.bronze_spear", numerator = 9, denominator = 128)
                drop("item.bronze_hatchet", numerator = 3, denominator = 128)
                drop("item.bronze_scimitar", numerator = 1, denominator = 128)
                drop("item.bronze_arrow", amount = 7, numerator = 3, denominator = 128)
                drop("item.mind_rune", amount = 2, numerator = 3, denominator = 128)
                drop("item.earth_rune", amount = 4, numerator = 3, denominator = 128)
                drop("item.body_rune", amount = 2, numerator = 3, denominator = 128)
                drop("item.bronze_javelin", amount = 5, numerator = 2, denominator = 128)
                drop("item.chaos_rune", numerator = 1, denominator = 128)
                drop("item.nature_rune", numerator = 1, denominator = 128)
                drop("item.coins", amount = 1, numerator = 34, denominator = 128)
                drop("item.coins", amount = 3, numerator = 13, denominator = 128)
                drop("item.coins", amount = 5, numerator = 8, denominator = 128)
                drop("item.coins", amount = 16, numerator = 7, denominator = 128)
                drop("item.coins", amount = 24, numerator = 3, denominator = 128)
                drop("item.goblin_mail", numerator = 10, denominator = 128)
                drop("item.hammer", numerator = 9, denominator = 128)
                drop("item.goblin_book", numerator = 2, denominator = 128)
                drop("item.grapes", numerator = 1, denominator = 128)
                drop("item.cape", numerator = 1, denominator = 128)
                drop("item.tin_ore", numerator = 1, denominator = 128)
            }

            preRollDrops {
                drop(
                    "item.goblin_skull",
                    condition = { player -> false }//TODO RAG AND BONE MAN QUEST
                )
            }

            tertiaryDrops {
                drop(
                    "item.scroll_box_easy",
                    numerator = 1,
                    denominator = 128,
                    condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.EASY) })
                drop(
                    "item.sling",
                    numerator = 1,
                    denominator = 128
                )
                drop(
                    "item.staff_of_air",
                    numerator = 1,
                    denominator = 128
                )
                drop(
                    "item.goblin_champion_scroll",
                    numerator = 1,
                    denominator = 5000
                )
            }
        }.apply { name = "Higher Level Goblin" }
}
