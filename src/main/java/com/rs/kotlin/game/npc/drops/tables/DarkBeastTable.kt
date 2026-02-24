package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.config.SeedTableConfig
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.npc.drops.seed.SeedTableType

object DarkBeastTable {
    val table =
        dropTable(
            category = TableCategory.SLAYER,
            gemTable = GemTableConfig(3, 128),
            rareTable = RareTableConfig(3, 128),
            herbTables =
                listOf(
                    HerbTableConfig(amount = 1..1, numerator = 19, denominator = 128),
                    HerbTableConfig(amount = 2..2, numerator = 5, denominator = 128),
                ),
            seedTable =
                SeedTableConfig(SeedTableType.RARE, amount = 1..1, numerator = 3, denominator = 128),
        ) {

            alwaysDrops {
                drop("item.big_bones")
            }

            preRollDrops {
                drop("item.dark_bow", numerator = 1, denominator = 512) { collectionLog = true }
            }

            mainDrops(128) {
                drop("item.black_battleaxe", weight = 6)
                drop("item.adamant_sq_shield", weight = 1)
                drop("item.rune_chainbody", weight = 1)
                drop("item.rune_helm", weight = 1)
                drop("item.rune_full_helm", weight = 1)
                drop("item.rune_2h_sword", weight = 1)
                drop("item.rune_battleaxe", weight = 1)

                drop("item.death_rune", amount = 20, weight = 8)
                drop("item.chaos_rune", amount = 30, weight = 7)
                drop("item.blood_rune", amount = 15, weight = 4)

                drop("item.coins", amount = 152, weight = 40)
                drop("item.coins", amount = 64, weight = 6)
                drop("item.coins", amount = 95, weight = 6)
                drop("item.coins", amount = 220, weight = 5)

                drop("item.shark", weight = 3)
                drop("item.adamant_bar_noted", amount = 3, weight = 2)
                drop("item.adamantite_ore_noted", amount = 5, weight = 1)
                drop("item.death_talisman", weight = 1)
                drop("item.runite_ore_noted", weight = 1)
                drop("item.shark", amount = 2, weight = 1)
            }

            tertiaryDrops {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 128,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                )
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 1200,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
                drop(
                    "item.long_bone",
                    numerator = 1,
                    denominator = 400,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                ) { collectionLog = true }
                drop(
                    "item.curved_bone",
                    numerator = 1,
                    denominator = 5012,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                ) { collectionLog = true }
            }

            charmDrops {
                gold(amount = 1, percent = 8.4)
                green(amount = 1, percent = 4.2)
                crimson(amount = 1, percent = 8.4)
                blue(amount = 1, percent = 13.4)
            }
        }.apply { name = "Dark Beast" }
}
