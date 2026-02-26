package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object LesserDemonDropTable {
    val table =
        dropTable(
            category = TableCategory.REGULAR,
            gemTable = GemTableConfig(4, 128),
            herbTables = HerbTableConfig(1, 128),
        ) {
            always {
                drop("item.accursed_ashes")
            }
            main(128) {
                drop("item.steel_full_helm", weight = 4)
                drop("item.steel_hatchet", weight = 4)
                drop("item.steel_scimitar", weight = 3)
                drop("item.mithril_sq_shield", weight = 1)
                drop("item.mithril_chainbody", weight = 1)
                drop("item.rune_helm", weight = 1)

                drop("item.fire_rune", amount = 60, weight = 8)
                drop("item.chaos_rune", amount = 12, weight = 5)
                drop("item.death_rune", amount = 3, weight = 3)
                drop("item.fire_rune", amount = 30, weight = 1)

                drop("item.coins", amount = 120, weight = 40)
                drop("item.coins", amount = 40, weight = 29)
                drop("item.coins", amount = 200, weight = 10)
                drop("item.coins", amount = 10, weight = 7)
                drop("item.coins", amount = 450, weight = 1)

                drop("item.jug_of_wine", weight = 3)
                drop("item.gold_ore", weight = 2)
            }
            charm {
                gold(amount = 1, percent = 9.63)
                green(amount = 1, percent = 4.82)
                crimson(amount = 1, percent = 19.3)
                blue(amount = 1, percent = 0.642)
            }
        }.apply {
            name = "Lesser demon"
        }
}
