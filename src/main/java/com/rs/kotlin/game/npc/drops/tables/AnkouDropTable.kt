package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.config.SeedTableConfig
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.npc.drops.seed.SeedTableType

object AnkouDropTable {
    val table =
        dropTable(
            category = TableCategory.REGULAR,
            herbTables = HerbTableConfig(3, 100),
            seedTable = SeedTableConfig(SeedTableType.UNCOMMON, numerator = 1, 100),
            gemTable = GemTableConfig(2, 100),
        ) {
            always {
                drop("item.bones")
            }
            main(100) {
                drop("item.black_knife", weight = 1)
                drop("item.black_robe_top", weight = 1)

                drop("item.death_rune", amount = 10, weight = 10)
                drop("item.blood_rune", amount = 11, weight = 6)
                drop("item.adamant_arrow", amount = 5..14, weight = 4)
                drop("item.law_rune", amount = 2, weight = 4)
                drop("item.blood_rune", amount = 5, weight = 3)

                drop("item.pure_essence_noted", amount = 15, weight = 33)
                drop("item.mithril_ore_noted", amount = 3..7, weight = 2)

                drop("item.coins", amount = 5..204, weight = 10)

                drop("item.left_skull_half", weight = 3, condition = { context -> false })
                drop("item.bass", amount = 1, weight = 2)
                drop("item.weapon_poison", amount = 1, weight = 2)
                drop("item.fried_mushrooms", amount = 1, weight = 1)
            }
            tertiary {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 256,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                )
            }

            charm {
                gold(amount = 1, percent = 23.9)
                green(amount = 1, percent = 0.747)
                crimson(amount = 1, percent = 1.49)
                blue(amount = 1, percent = 0.149)
            }
        }.apply {
            name = "Ankou"
        }
}
