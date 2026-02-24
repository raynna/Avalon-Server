package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object GeneralGraardorTable {
    val table =
        dropTable(
            godwarsGemTable = GemTableConfig(2, 127),
            godwarsRareTable = RareTableConfig(8, 127),
            category = TableCategory.BOSS,
        ) {

            alwaysDrops {
                drop("item.ourg_bones")
            }

            charmDrops {
                gold(amount = 1, percent = 10.1)
                green(amount = 1, percent = 5.04)
                crimson(amount = 1, percent = 10.1)
                blue(amount = 1, percent = 16.1)
            }

            preRollDrops {
                drop("item.bandos_chestplate", numerator = 1, denominator = 381) {
                    collectionLog = true
                    announce = true
                }
                drop("item.bandos_tassets", numerator = 1, denominator = 381) {
                    collectionLog = true
                    announce = true
                }
                drop("item.bandos_boots", numerator = 1, denominator = 381) {
                    collectionLog = true
                    announce = true
                }
                drop("item.bandos_hilt", numerator = 1, denominator = 508) {
                    collectionLog = true
                    announce = true
                }
                drop("item.godsword_shard_1", numerator = 1, denominator = 762) { collectionLog = true }
                drop("item.godsword_shard_2", numerator = 1, denominator = 762) { collectionLog = true }
                drop("item.godsword_shard_3", numerator = 1, denominator = 762) { collectionLog = true }
            }

            mainDrops(127) {
                drop("item.rune_longsword", weight = 8)
                drop("item.rune_2h_sword", weight = 8)
                drop("item.rune_platebody", weight = 8)
                drop("item.rune_pickaxe", weight = 8)

                drop("item.coins", amount = 19500..20000, weight = 32)
                drop("item.grimy_snapdragon_noted", amount = 3, weight = 8)
                drop("item.snapdragon_seed", amount = 1, weight = 8)
                drop("item.super_restore_4", amount = 3, weight = 8)
                drop("item.adamantite_ore_noted", amount = 15..20, weight = 8)
                drop("item.coal_noted", amount = 115..120, weight = 8)
                drop("item.magic_logs_noted", amount = 15..20, weight = 8)
                drop("item.nature_rune", amount = 65..70, weight = 8)
                drop("item.coins", amount = 20100..20600, weight = 1)
            }

            tertiaryDrops {
                drop(
                    "item.long_bone",
                    numerator = 1,
                    denominator = 400,
                ) { collectionLog = true }
                drop(
                    "item.curved_bone",
                    numerator = 1,
                    denominator = 5012,
                ) { collectionLog = true }
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 250,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
            }
        }.apply { name = "General graardor" }
}
