package com.rs.kotlin.game.npc.drops.tables

import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.SeedTableConfig
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.npc.drops.seed.SeedTableType

object IceGiantDropTable {
    val table =
        dropTable(
            category = TableCategory.REGULAR,
            seedTable = SeedTableConfig(SeedTableType.UNCOMMON, numerator = 8, 128),
            gemTable = GemTableConfig(4, 128),
        ) {
            always {
                drop("item.big_bones")
            }
            main(128) {
                drop("item.iron_2h_sword", weight = 5)
                drop("item.black_kiteshield", weight = 4)
                drop("item.steel_hatchet", weight = 4)
                drop("item.steel_sword", weight = 4)
                drop("item.iron_platelegs", weight = 1)
                drop("item.mithril_mace", weight = 1)
                drop("item.mithril_sq_shield", weight = 1)

                drop("item.adamant_arrow", amount = 5, weight = 6)
                drop("item.nature_rune", amount = 6, weight = 4)
                drop("item.mind_rune", amount = 24, weight = 3)
                drop("item.body_rune", amount = 37, weight = 3)
                drop("item.law_rune", amount = 3, weight = 2)
                drop("item.water_rune", amount = 12, weight = 1)
                drop("item.cosmic_rune", amount = 4, weight = 1)
                drop("item.death_rune", amount = 3, weight = 1)
                drop("item.blood_rune", amount = 2, weight = 1)

                drop("item.coins", amount = 117, weight = 32)
                drop("item.coins", amount = 53, weight = 12)
                drop("item.coins", amount = 196, weight = 10)
                drop("item.coins", amount = 8, weight = 7)
                drop("item.coins", amount = 22, weight = 6)
                drop("item.coins", amount = 400, weight = 2)

                drop("item.jug_of_wine", amount = 1, weight = 3)
                drop("item.mithril_ore", amount = 1, weight = 1)
                drop("item.banana", amount = 1, weight = 1)
            }

            charm {
                gold(amount = 1, percent = 43.3)
                green(amount = 1, percent = 1.35)
                crimson(amount = 1, percent = 2.71)
                blue(amount = 1, percent = 0.271)
            }
        }.apply {
            name = "Ice Giant"
        }
}
