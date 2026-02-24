package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object GargoyleDropTable {
    val table =
        dropTable(
            gemTable = GemTableConfig(5, 128),
            category = TableCategory.SLAYER,
        ) {

            charmDrops {
                gold(amount = 1, percent = 9.78)
                green(amount = 1, percent = 4.89)
                crimson(amount = 1, percent = 4.89)
                blue(amount = 1, percent = 6.84)
            }

            preRollDrops {
                drop("item.granite_maul", numerator = 1, denominator = 256) { collectionLog = true }
                drop("item.mystic_robe_top_dark", numerator = 1, denominator = 512) { collectionLog = true }
            }

            mainDrops(128) {
                drop("item.adamant_platelegs", weight = 4)
                drop("item.rune_full_helm", weight = 3)
                drop("item.rune_2h_sword", weight = 2)
                drop("item.adamant_boots", weight = 1)
                drop("item.rune_battleaxe", weight = 1)
                drop("item.rune_platelegs", weight = 14)

                drop("item.fire_rune", amount = 75, weight = 10)
                drop("item.chaos_rune", amount = 30, weight = 9)
                drop("item.fire_rune", amount = 150, weight = 6)
                drop("item.death_rune", amount = 15, weight = 5)

                drop("item.gold_ore_noted", amount = 10..20, weight = 10)
                drop("item.pure_essence_noted", amount = 150, weight = 6)
                drop("item.steel_bar_noted", amount = 15, weight = 6)
                drop("item.gold_bar_noted", amount = 10..15, weight = 3)
                drop("item.mithril_bar_noted", amount = 15, weight = 2)
                drop("item.runite_ore", weight = 2)

                drop("item.coins", amount = 400..800, weight = 28)
                drop("item.coins", amount = 500..1000, weight = 20)
                drop("item.coins", amount = 10000, weight = 5)
            }

            tertiaryDrops {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 128,
                    condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                )
            }
        }.apply { name = "Gargoyle" }
}
