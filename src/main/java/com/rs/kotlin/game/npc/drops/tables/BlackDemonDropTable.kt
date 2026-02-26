package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object BlackDemonDropTable {
    val table =
        dropTable(
            category = TableCategory.REGULAR,
            herbTables = HerbTableConfig(23, 128),
            gemTable = GemTableConfig(5, 128),
            rareTable = RareTableConfig(1, 128),
        ) {
            always {
                drop("item.infernal_ashes")
            }
            main(128) {
                drop("item.black_sword", weight = 4)
                drop("item.steel_battleaxe", weight = 3)
                drop("item.steel_battleaxe", weight = 3)
                drop("item.black_hatchet", weight = 2)
                drop("item.mithril_kiteshield", weight = 1)
                drop("item.rune_helm", weight = 1)
                drop("item.rune_chainbody", weight = 1)

                drop("item.air_rune", amount = 50, weight = 8)
                drop("item.chaos_rune", amount = 10, weight = 7)
                drop("item.blood_rune", amount = 7, weight = 4)
                drop("item.fire_rune", amount = 37, weight = 1)
                drop("item.law_rune", amount = 3, weight = 1)

                drop("item.coins", amount = 132, weight = 40)
                drop("item.coins", amount = 30, weight = 7)
                drop("item.coins", amount = 44, weight = 6)
                drop("item.coins", amount = 220, weight = 6)
                drop("item.coins", amount = 460, weight = 1)

                drop("item.lobster", weight = 3)
                drop("item.adamant_bar", weight = 2)
                drop("item.defence_potion_3", weight = 1)
            }
            tertiary {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 128,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                )
            }

            charm {
                gold(amount = 1, percent = 22.3)
                green(amount = 1, percent = 11.2)
                crimson(amount = 1, percent = 44.6)
                blue(amount = 1, percent = 1.49)
            }
        }.apply {
            name = "Black demon"
        }
}
