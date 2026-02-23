package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.HerbTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object KalphiteSoldierDropTable {

    val table = dropTable(herbTables = HerbTableConfig(numerator = 1, denominator = 128),
        rareDropTable = true,
        rolls = 1) {

        charmDrops {
            gold(amount = 1, percent = 9.26)
            green(amount = 1, percent = 5.15)
            crimson(amount = 1, percent = 7.53)
            blue(amount = 1, percent = 6.95)
        }

        mainDrops(128) {
            /** Weapons & armours */
            drop("item.steel_full_helm", weight = 4)
            drop("item.steel_hatchet", weight = 4)
            drop("item.steel_scimitar", weight = 3)
            drop("item.mithril_chainbody", weight = 1)
            drop("item.mithril_sq_shield", weight = 1)
            drop("item.adamant_helm", weight = 1)
            /** Runes */
            drop("item.fire_rune", amount = 60, weight = 8)
            drop("item.chaos_rune", amount = 12, weight = 5)
            drop("item.death_rune", amount = 3, weight = 3)
            drop("item.nature_rune", amount = 1..4, weight = 2)
            drop("item.fire_rune", amount = 30, weight = 1)
            /** Coins */
            drop("item.coins", amount = 120, weight = 40)
            drop("item.coins", amount = 40, weight = 29)
            drop("item.coins", amount = 200, weight = 10)
            drop("item.coins", amount = 10, weight = 7)
            drop("item.coins", amount = 450, weight = 1)
            /** Other */
            drop("item.waterskin_4", weight = 3)
        }
    }.apply { name = "Kalphite Soldier" }
}
