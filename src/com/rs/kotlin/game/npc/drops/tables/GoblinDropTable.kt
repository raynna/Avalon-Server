package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.*

object GoblinDropTable {

    val table =
        dropTable(rolls = 1) {

            alwaysDrops {
                drop("item.bones")
            }

            mainDrops {//item: Any, amount: Int = 1, numerator: Int = 1, denominator: Int = 4
                drop(item = "item.bronze_spear", numerator = 4, denominator = 128)
                drop(item = "item.bronze_sq_shield", numerator = 3, denominator = 128)
                drop(item = "item.water_rune", amount = 6, numerator = 6, denominator = 128)
                drop(item = "item.body_rune", amount = 7, numerator = 5, denominator = 128)
                drop(item = "item.earth_rune", amount = 4, numerator = 3, denominator = 128)
                drop(item = "item.bronze_bolts", amount = 8, numerator = 3, denominator = 128)
                drop(item = "item.coins", amount = 5, numerator = 28, denominator = 128)
                drop(item = "item.coins", amount = 9, numerator = 3, denominator = 128)
                drop(item = "item.coins", amount = 15, numerator = 3, denominator = 128)
                drop(item = "item.coins", amount = 20, numerator = 2, denominator = 128)
                drop(item = "item.coins", amount = 1, numerator = 1, denominator = 128)
                drop(item = "item.hammer", numerator = 15, denominator = 128)
                drop(item = "item.goblin_mail", numerator = 5, denominator = 128)
                drop(item = "item.chef_s_hat", numerator = 3, denominator = 128)
                drop(item = "item.goblin_book", numerator = 2, denominator = 128)
                drop(item = "item.beer", numerator = 2, denominator = 128)
                drop(item = "item.brass_necklace", numerator = 1, denominator = 128)
                drop(item = "item.air_talisman", numerator = 1, denominator = 128)
            }

            preRollDrops {
                drop(
                    item = "item.goblin_skull",
                    condition = { player -> false }//TODO RAG AND BONE MAN QUEST
                )
            }

            tertiaryDrops {
                drop(
                    item = "item.scroll_box_easy",
                    numerator = 1,
                    denominator = 128,
                    condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.EASY) })
                drop(
                    item = "item.sling",
                    numerator = 1,
                    denominator = 128
                )
                drop(
                    item = "item.staff_of_air",
                    numerator = 1,
                    denominator = 128
                )
                drop(
                    item = "item.goblin_champion_scroll",
                    numerator = 1,
                    denominator = 5000
                )
            }
        }
}
