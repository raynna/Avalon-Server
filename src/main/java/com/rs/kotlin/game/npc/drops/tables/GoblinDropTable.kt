package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object GoblinDropTable {

    val table =
        dropTable(rolls = 1) {

            alwaysDrops {
                drop("item.bones")
            }

            charmDrops {
                gold(amount = 1, percent = 8.6)
                green(amount = 1, percent = 0.269)
                crimson(amount = 1, percent = 0.538)
                blue(amount = 1, percent = 0.0537)
            }

            mainDrops(128) {//item: Any, amount: Int = 1, weight: Int = 1, denominator: Int = 4
                drop("item.bronze_spear", weight = 4)
                drop("item.bronze_sq_shield", weight = 3)
                drop("item.water_rune", amount = 6, weight = 6)
                drop("item.body_rune", amount = 7, weight = 5)
                drop("item.earth_rune", amount = 4, weight = 3)
                drop("item.bronze_bolts", amount = 8, weight = 3)
                drop("item.coins", amount = 5, weight = 28)
                drop("item.coins", amount = 9, weight = 3)
                drop("item.coins", amount = 15, weight = 3)
                drop("item.coins", amount = 20, weight = 2)
                drop("item.coins", amount = 1, weight = 1)
                drop("item.hammer", weight = 15)
                drop("item.goblin_mail", weight = 5)
                drop("item.chef_s_hat", weight = 3)
                drop("item.goblin_book", weight = 2)
                drop("item.beer", weight = 2)
                drop("item.brass_necklace", weight = 1)
                drop("item.air_talisman", weight = 1)
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
        }.apply { name = "Goblin" }
}
