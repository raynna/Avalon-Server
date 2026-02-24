package com.rs.kotlin.game.npc.drops.tables

import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object KalphiteGuardianDropTable {
    val table =
        dropTable(
            gemTable = GemTableConfig(5, 128),
            rareTable = RareTableConfig(numerator = 1, denominator = 128),
            herbTables = HerbTableConfig(numerator = 23, denominator = 128),
        ) {

            charmDrops {
                gold(amount = 1, percent = 16.3)
                green(amount = 1, percent = 8.13)
                crimson(amount = 1, percent = 8.13)
                blue(amount = 1, percent = 11.4)
            }

            mainDrops(128) {
                // Weapons & armours
                drop("item.mithril_sword", weight = 4)
                drop("item.steel_battleaxe", weight = 3)
                drop("item.mithril_hatchet", weight = 2)
                drop("item.adamant_dagger", weight = 2)
                drop("item.mithril_kiteshield", weight = 1)
                drop("item.rune_helm", weight = 1)
                drop("item.rune_chainbody", weight = 1)
                // Runes
                drop("item.air_rune", amount = 50, weight = 8)
                drop("item.chaos_rune", amount = 10, weight = 7)
                drop("item.blood_rune", amount = 7, weight = 4)
                drop("item.fire_rune", amount = 37, weight = 1)
                drop("item.law_rune", amount = 3, weight = 1)
                // Coins
                drop("item.coins", amount = 132, weight = 40)
                drop("item.coins", amount = 30, weight = 7)
                drop("item.coins", amount = 44, weight = 6)
                drop("item.coins", amount = 220, weight = 6)
                drop("item.coins", amount = 460, weight = 1)
                // Other
                drop("item.lobster", weight = 3)
                drop("item.defence_potion_3", weight = 1)
            }
        }.apply { name = "Kalphite Guardian" }
}
