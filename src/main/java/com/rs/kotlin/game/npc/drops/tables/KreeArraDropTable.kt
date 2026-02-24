package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object KreeArraDropTable {
    val table =
        dropTable(
            godwarsGemTable = GemTableConfig(2, 127),
            godwarsRareTable = RareTableConfig(numerator = 8, denominator = 127),
            category = TableCategory.BOSS,
        ) {

            alwaysDrops {
                drop("item.big_bones")
                drop("item.feather", 1..16)
            }

            charmDrops {
                gold(amount = 1, percent = 10.1)
                green(amount = 1, percent = 5.04)
                crimson(amount = 1, percent = 10.1)
                blue(amount = 1, percent = 16.1)
            }

            preRollDrops {
                drop("item.armadyl_helmet", numerator = 1, denominator = 381) {
                    collectionLog = true
                    announce = true
                }
                drop("item.armadyl_chestplate", numerator = 1, denominator = 381) {
                    collectionLog = true
                    announce = true
                }
                drop("item.armadyl_chainskirt", numerator = 1, denominator = 381) {
                    collectionLog = true
                    announce = true
                }
                drop("item.armadyl_hilt", numerator = 1, denominator = 508) {
                    collectionLog = true
                    announce = true
                }
                drop("item.godsword_shard_1", numerator = 1, denominator = 762) { collectionLog = true }
                drop("item.godsword_shard_2", numerator = 1, denominator = 762) { collectionLog = true }
                drop("item.godsword_shard_3", numerator = 1, denominator = 762) { collectionLog = true }
            }

            mainDrops(127) {
                drop("item.black_d_hide_body", weight = 8)
                drop("item.rune_crossbow", weight = 8)

                drop("item.mind_rune", amount = 586..601, weight = 8)
                drop("item.rune_arrow", amount = 100..105, weight = 8)
                drop("item.runite_bolts", amount = 20..25, weight = 8)
                drop("item.dragonstone_bolts_e", amount = 5..10, weight = 8)

                drop("item.coins", amount = 19500..20000, weight = 44)
                drop("item.ranging_potion_3", amount = 3, weight = 8)
                drop("item.super_defence_3", amount = 3, weight = 8)
                drop("item.grimy_dwarf_weed_noted", amount = 8..13, weight = 8)
                drop("item.dwarf_weed_seed", amount = 2, weight = 8)
                drop("item.coins", amount = 20500..21000, weight = 1)
                drop("item.crystal_key", amount = 1, weight = 18)
                drop("item.yew_seed", amount = 1, weight = 1)
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
                    condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
            }
        }.apply { name = "Kree'arra" }
}
