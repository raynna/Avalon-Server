package com.rs.kotlin.game.npc.drops.tables

import com.rs.kotlin.game.npc.drops.HerbTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object KalphiteWorkerDropTable {

    val table = dropTable(herbTable = HerbTableConfig(numerator = 7, denominator = 128),
        rareDropTable = true,
        rolls = 1) {

        charmDrops {
            gold(amount = 1, percent = 5.99)
            green(amount = 1, percent = 2.99)
            crimson(amount = 1, percent = 1.5)
            blue(amount = 1, percent = 1.5)
        }

        mainDrops(128) {
            /** Weapons & armours */
            drop("item.iron_sword", weight = 3)
            drop("item.steel_dagger", weight = 3)
            drop("item.hardleather_body", weight = 2)
            drop("item.iron_javelin", weight = 1)
            drop("item.steel_longsword", weight = 1)
            /** Runes */
            drop("item.law_rune", amount = 2, weight = 3)
            drop("item.body_rune", amount = 6, weight = 2)
            drop("item.chaos_rune", amount = 3, weight = 2)
            drop("item.fire_rune", amount = 7, weight = 2)
            drop("item.water_rune", amount = 2, weight = 2)
            drop("item.nature_rune", amount = 4, weight = 2)
            drop("item.cosmic_rune", amount = 2, weight = 1)
            /** Coins */
            drop("item.coins", amount = 15, weight = 34)
            drop("item.coins", amount = 5, weight = 12)
            drop("item.coins", amount = 28, weight = 12)
            drop("item.coins", amount = 1, weight = 8)
            drop("item.coins", amount = 62, weight = 4)
            drop("item.coins", amount = 42, weight = 3)
            /** Other */
            drop("item.waterskin_4", amount = 1, weight = 21)
        }
    }.apply { name = "Kalphite Worker" }
}
