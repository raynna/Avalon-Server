package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object BansheeTable {

    val table = dropTable(rareDropTable = true, herbTable = true, rolls = 1) {

        charmDrops {
            gold(amount = 1, percent = 2.02)
            green(amount = 1, percent = 7.05)
            crimson(amount = 1, percent = 1.01)
            blue(amount = 1, percent = 0.202)
        }

        mainDrops {
            /** Weapons & armours */
            drop("item.iron_mace", numerator = 2, denominator = 128)
            drop("item.iron_dagger", numerator = 2, denominator = 128)
            drop("item.iron_kiteshield", numerator = 1, denominator = 128)
            drop("item.mystic_gloves_dark", numerator = 1, denominator = 128)
            /** Runes */
            drop("item.air_rune", amount = 3, numerator = 3, denominator = 128)
            drop("item.cosmic_rune", amount = 2, numerator = 3, denominator = 128)
            drop("item.chaos_rune", amount = 3, numerator = 2, denominator = 128)
            drop("item.fire_rune", amount = 7, numerator = 1, denominator = 128)
            drop("item.chaos_rune", amount = 7, numerator = 1, denominator = 128)
            /** Materials */
            drop("item.pure_essence_noted", amount = 13, numerator = 22, denominator = 128)
            drop("item.iron_ore", numerator = 1, denominator = 128)
            /** Coins */
            drop("item.coins", amount = 13, numerator = 10, denominator = 128)
            drop("item.coins", amount = 26, numerator = 8, denominator = 128)
            drop("item.coins", amount = 35, numerator = 8, denominator = 128)
            /** Other */
            drop("item.fishing_bait", amount = 15, numerator = 22, denominator = 128)
            drop("item.fishing_bait", amount = 7, numerator = 5, denominator = 128)
            drop("item.eye_of_newt", numerator = 1, denominator = 128)
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
