package com.rs.kotlin.game.npc.drops.tables

import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object CrawlingHandTable {
    val table =
        dropTable(
            gemTable = GemTableConfig(2, 128),
            category = TableCategory.SLAYER,
        ) {

            charmDrops {
                gold(amount = 1, percent = 9.85)
                green(amount = 1, percent = 0.308)
                crimson(amount = 1, percent = 0.616)
                blue(amount = 1, percent = 0.0616)
            }

            alwaysDrops {
                drop("item.bones")
            }

            mainDrops(128) {
                /** Gloves */
                drop("item.leather_gloves", weight = 21)
                drop("item.purple_gloves", weight = 2)
                drop("item.yellow_gloves", weight = 2)
                drop("item.red_gloves", weight = 2)
                drop("item.teal_gloves", weight = 2)
                /** Jewellery */
                drop("item.gold_ring", weight = 3)
                drop("item.sapphire_ring", weight = 2)
                drop("item.emerald_ring", weight = 2)
            }

            tertiaryDrops {
                drop(
                    "item.crawling_hand",
                    numerator = 1,
                    denominator = 128,
                ) { collectionLog = true }
            }
        }.apply { name = "Crawling Hand" }
}
