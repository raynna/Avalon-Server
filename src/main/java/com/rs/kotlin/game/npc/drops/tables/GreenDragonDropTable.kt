package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.Skills
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object GreenDragonDropTable {
    val table =
        dropTable(
            gemTable = GemTableConfig(5, 128),
            herbTables = HerbTableConfig(numerator = 15, denominator = 128),
        ) {
            alwaysDrops {
                drop("item.dragon_bones")
                drop("item.green_dragonhide")
            }
            mainDrops(128) {
                drop("item.steel_platelegs", weight = 4)
                drop("item.steel_battleaxe", weight = 3)
                drop("item.mithril_hatchet", weight = 3)
                drop("item.mithril_spear", weight = 2)
                drop("item.mithril_kiteshield", weight = 1)
                drop("item.adamant_full_helm", weight = 1)
                drop("item.rune_dagger", weight = 1)

                drop("item.water_rune", amount = 75, weight = 8)
                drop("item.nature_rune", amount = 15, weight = 5)
                drop("item.law_rune", amount = 3, weight = 3)
                drop("item.fire_rune", amount = 37, weight = 1)

                drop("item.coins", amount = 44, weight = 29)
                drop("item.coins", amount = 132, weight = 25)
                drop("item.coins", amount = 200, weight = 10)
                drop("item.coins", amount = 11, weight = 5)
                drop("item.coins", amount = 440, weight = 1)

                drop("item.bass", weight = 3)
                drop("item.adamantite_ore", weight = 3)
            }
            tertiaryDrops {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 128,
                    condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                )
                drop(
                    "item.green_dragon_egg",
                    numerator = 1,
                    denominator = 200,
                    condition = { player -> player.skills?.getLevelForXp(Skills.SUMMONING) == 99 },
                ) {
                    collectionLog = true
                }
            }

            charmDrops {
                gold(amount = 1, percent = 6.62)
                green(amount = 1, percent = 16.6)
                crimson(amount = 1, percent = 6.62)
                blue(amount = 1, percent = 1.32)
            }
        }.apply {
            name = "Green Dragon"
            collectionGroup = "Dragons"
        }
}
