package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.Skills
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object DarkBeastTable {

    val table = dropTable(herbTable = true, rareDropTable = true, rolls = 1) {

        charmDrops {
            gold(amount = 1, percent = 8.4)
            green(amount = 1, percent = 4.2)
            crimson(amount = 1, percent = 8.4)
            blue(amount = 1, percent = 13.4)
        }

        alwaysDrops {
            drop("item.big_bones")
        }

        mainDrops {
            drop("item.black_battleaxe", numerator = 12, denominator = 512)
            drop("item.adamant_sq_shield", numerator = 4, denominator = 512)
            drop("item.rune_chainbody", numerator = 4, denominator = 512)
            drop("item.rune_helm", numerator = 4, denominator = 512)
            drop("item.rune_full_helm", numerator = 4, denominator = 512)
            drop("item.rune_2h_sword", numerator = 4, denominator = 512)
            drop("item.rune_battleaxe", numerator = 4, denominator = 512)
            drop("item.dark_bow", numerator = 1, denominator = 512)

            drop("item.death_rune", amount = 20, numerator = 32, denominator = 512)
            drop("item.chaos_rune", amount = 30, numerator = 28, denominator = 512)
            drop("item.blood_rune", amount = 15, numerator = 16, denominator = 512)

            drop("item.coins", amount = 152, numerator = 160, denominator = 512)
            drop("item.coins", amount = 64, numerator = 24, denominator = 512)
            drop("item.coins", amount = 95, numerator = 24, denominator = 512)
            drop("item.coins", amount = 220, numerator = 20, denominator = 512)

            drop("item.shark", numerator = 12, denominator = 512)
            drop("item.adamant_bar_noted", amount = 3, numerator = 8, denominator = 512)
            drop("item.adamantite_ore_noted", amount = 5, numerator = 4, denominator = 512)
            drop("item.death_talisman", numerator = 4, denominator = 512)
            drop("item.runite_ore_noted", numerator = 4, denominator = 512)
            drop("item.shark", amount = 2, numerator = 4, denominator = 512)


        }

        tertiaryDrops {
            drop(
                "item.scroll_box_hard",
                numerator = 1,
                denominator = 128,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) })
            drop(
                "item.scroll_box_elite",
                numerator = 1,
                denominator = 1200,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) })
        }
    }.apply { name = "Dark Beast" }
}
