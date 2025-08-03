package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.*

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

            mainDrops {//item: Any, amount: Int = 1, numerator: Int = 1, denominator: Int = 4
                drop("item.bronze_spear", numerator = 4, denominator = 128)
                drop("item.bronze_sq_shield", numerator = 3, denominator = 128)
                drop("item.water_rune", amount = 6, numerator = 6, denominator = 128)
                drop("item.body_rune", amount = 7, numerator = 5, denominator = 128)
                drop("item.earth_rune", amount = 4, numerator = 3, denominator = 128)
                drop("item.bronze_bolts", amount = 8, numerator = 3, denominator = 128)
                drop("item.coins", amount = 5, numerator = 28, denominator = 128)
                drop("item.coins", amount = 9, numerator = 3, denominator = 128)
                drop("item.coins", amount = 15, numerator = 3, denominator = 128)
                drop("item.coins", amount = 20, numerator = 2, denominator = 128)
                drop("item.coins", amount = 1, numerator = 1, denominator = 128)
                drop("item.hammer", numerator = 15, denominator = 128)
                drop("item.goblin_mail", numerator = 5, denominator = 128)
                drop("item.chef_s_hat", numerator = 3, denominator = 128)
                drop("item.goblin_book", numerator = 2, denominator = 128)
                drop("item.beer", numerator = 2, denominator = 128)
                drop("item.brass_necklace", numerator = 1, denominator = 128)
                drop("item.air_talisman", numerator = 1, denominator = 128)
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
