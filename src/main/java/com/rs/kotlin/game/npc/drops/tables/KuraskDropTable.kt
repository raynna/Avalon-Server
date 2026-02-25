package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.SeedTableConfig
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.npc.drops.seed.SeedTableType

object KuraskDropTable {
    val table =
        dropTable(
            category = TableCategory.SLAYER,
            herbTables = HerbTableConfig(numerator = 18, denominator = 124),
            seedTable = SeedTableConfig(SeedTableType.RARE, numerator = 15, denominator = 124),
            gemTable = GemTableConfig(numerator = 6, denominator = 124),
        ) {

            charm {
                gold(amount = 1, percent = 8.98)
                green(amount = 1, percent = 27.0)
                crimson(amount = 1, percent = 6.74)
                blue(amount = 1, percent = 1.35)
            }

            always {
                drop("item.bones")
            }

            prerollDenom {
                drop("item.leaf_bladed_sword", numerator = 1, denominator = 384) { collectionLog = true }
                drop("item.mystic_robe_top_light", numerator = 1, denominator = 512) { collectionLog = true }
            }

            main(124) {
                drop("item.mithril_kiteshield", weight = 3)
                drop("item.rune_longsword", weight = 3)
                drop("item.adamant_platebody", weight = 3)
                drop("item.rune_hatchet", weight = 3)
                drop("item.nature_rune", amount = 10, weight = 10)
                drop("item.nature_rune", amount = 15, weight = 7)
                drop("item.nature_rune", amount = 30, weight = 4)
                drop("item.coins", amount = 2000..3000, weight = 16)
                drop("item.flax_noted", amount = 100, weight = 6)
                drop("item.white_berries_noted", amount = 12..12, weight = 6)
                drop("item.coins", amount = 10000, weight = 5)
                drop("item.big_bones_noted", amount = 20, weight = 5)
                drop("item.papaya_fruit_noted", amount = 10, weight = 4)
                drop("item.coconut_noted", amount = 10, weight = 4)
            }

            tertiary {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 128,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                )
            }
        }.apply { name = "Kurask" }
}
