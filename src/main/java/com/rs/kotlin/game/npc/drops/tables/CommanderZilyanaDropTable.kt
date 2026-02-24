package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object CommanderZilyanaDropTable {
    val table =
        dropTable(
            category = TableCategory.BOSS,
            godwarsRareTable = RareTableConfig(8, 127),
            godwarsGemTable = GemTableConfig(2, 127),
        ) {

            alwaysDrops {
                drop("item.bones")
            }

            charmDrops {
                gold(amount = 1, percent = 10.1)
                green(amount = 1, percent = 5.04)
                crimson(amount = 1, percent = 10.1)
                blue(amount = 1, percent = 16.1)
            }

            preRollDrops {
                drop("item.saradomin_sword", numerator = 1, denominator = 127) {
                    collectionLog = true
                    announce = true
                }
                drop("item.saradomin_hilt", numerator = 1, denominator = 508) {
                    collectionLog = true
                    announce = true
                }
                drop("item.godsword_shard_1", numerator = 1, denominator = 762) { collectionLog = true }
                drop("item.godsword_shard_2", numerator = 1, denominator = 762) { collectionLog = true }
                drop("item.godsword_shard_3", numerator = 1, denominator = 762) { collectionLog = true }
            }

            mainDrops(127) {
                drop("item.adamant_platebody", weight = 8)
                drop("item.rune_dart", amount = 35..40, weight = 8)
                drop("item.rune_kiteshield", weight = 8)
                drop("item.rune_plateskirt", weight = 8)

                drop("item.prayer_potion_4", amount = 3, weight = 8)
                drop("item.super_defence_3", amount = 3, weight = 8)
                drop("item.magic_potion_3", amount = 3, weight = 8)
                drop("item.saradomin_brew_3", amount = 3, weight = 8)
                drop("item.super_restore_4", amount = 3, weight = 8)

                drop("item.coins", amount = 19500..20000, weight = 31)
                drop("item.diamond_noted", amount = 6, weight = 8)
                drop("item.law_rune", amount = 95..100, weight = 8)
                drop("item.grimy_ranarr_noted", amount = 5, weight = 8)
                drop("item.ranarr_seed", amount = 2, weight = 8)
                drop("item.magic_seed", weight = 1)
            }

            tertiaryDrops {
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 250,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
            }
        }.apply { name = "Commander Zilyana" }
}
