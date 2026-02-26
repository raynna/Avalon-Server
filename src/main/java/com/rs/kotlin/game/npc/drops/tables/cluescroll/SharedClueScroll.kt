package com.rs.kotlin.game.npc.drops.tables.cluescroll

import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.npc.drops.sharedWeightedTable

object SharedClueScroll {
    val scrolls =
        sharedWeightedTable {
            add("item.lumber_yard_teleport", amount = 5..15)
            add("item.nardah_teleport", amount = 5..15)
            add("item.bandit_camp_teleport", amount = 5..15)
        }

    val firelighters =
        sharedWeightedTable {
            add("item.red_firelighter")
            add("item.blue_firelighter")
            add("item.green_firelighter")
            add("item.white_firelighter")
            add("item.purple_firelighter")
        }

    val pages =
        sharedWeightedTable {
            add("item.zamorak_page_1")
            add("item.zamorak_page_2")
            add("item.zamorak_page_3")
            add("item.zamorak_page_4")
            add("item.saradomin_page_1")
            add("item.saradomin_page_2")
            add("item.saradomin_page_3")
            add("item.saradomin_page_4")
            add("item.guthix_page_1")
            add("item.guthix_page_2")
            add("item.guthix_page_3")
            add("item.guthix_page_4")
            add("item.ancient_page_1")
            add("item.ancient_page_2")
            add("item.ancient_page_3")
            add("item.ancient_page_4")
            add("item.armadyl_page_1")
            add("item.armadyl_page_2")
            add("item.armadyl_page_3")
            add("item.armadyl_page_4")
            add("item.bandos_page_1")
            add("item.bandos_page_2")
            add("item.bandos_page_3")
            add("item.bandos_page_4")
        }

    val table =
        dropTable(category = TableCategory.CLUE) {
            always {
                registerCollectionFrom(pages)
                registerCollectionFrom(scrolls)
                registerCollectionFrom(firelighters)
            }
            preroll {
                table(scrolls, 1, 1)
                table(pages, 1, 1)
                table(firelighters, 1, 1)
            }
        }.apply {
            name = "Shared clue scroll"
            collectionGroup = "Shared clue scroll"
        }
}
