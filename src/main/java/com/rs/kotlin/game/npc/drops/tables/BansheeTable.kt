package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.HerbTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object BansheeTable {

    val table = dropTable(herbTable = HerbTableConfig(numerator = 34, denominator = 128),
        rareDropTable = true,
        rolls = 1) {

        charmDrops {
            gold(amount = 1, percent = 2.02)
            green(amount = 1, percent = 7.05)
            crimson(amount = 1, percent = 1.01)
            blue(amount = 1, percent = 0.202)
        }

        mainDrops(128) {
            /** Weapons & armours */
            drop("item.iron_mace", weight = 2)
            drop("item.iron_dagger", weight = 2)
            drop("item.iron_kiteshield", weight = 1)
            drop("item.mystic_gloves_dark", weight = 1)
            /** Runes */
            drop("item.air_rune", amount = 3, weight = 3)
            drop("item.cosmic_rune", amount = 2, weight = 3)
            drop("item.chaos_rune", amount = 3, weight = 2)
            drop("item.fire_rune", amount = 7, weight = 1)
            drop("item.chaos_rune", amount = 7, weight = 1)
            /** Materials */
            drop("item.pure_essence_noted", amount = 13, weight = 22)
            drop("item.iron_ore", weight = 1)
            /** Coins */
            drop("item.coins", amount = 13, weight = 10)
            drop("item.coins", amount = 26, weight = 8)
            drop("item.coins", amount = 35, weight = 8)
            /** Other */
            drop("item.fishing_bait", amount = 15, weight = 22)
            drop("item.fishing_bait", amount = 7, weight = 5)
            drop("item.eye_of_newt", weight = 1)
        }

        tertiaryDrops {
            drop(
                "item.scroll_box_easy",
                numerator = 1,
                denominator = 128,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.EASY) })
        }
    }.apply { name = "Banshee" }
}
