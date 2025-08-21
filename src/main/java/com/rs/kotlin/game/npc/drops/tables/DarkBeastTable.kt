package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.HerbTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object DarkBeastTable {

    val darkBeasts = dropTable(
        herbTable = HerbTableConfig(amount = 1..2, numerator = 24, denominator = 128),
        rareDropTable = true,
        rolls = 1) {

        alwaysDrops {
            drop("item.big_bones")
        }

        mainDrops(512) {
            drop("item.black_battleaxe", weight = 12)
            drop("item.adamant_sq_shield", weight = 4)
            drop("item.rune_chainbody", weight = 4)
            drop("item.rune_helm", weight = 4)
            drop("item.rune_full_helm", weight = 4)
            drop("item.rune_2h_sword", weight = 4)
            drop("item.rune_battleaxe", weight = 4)
            drop("item.dark_bow", weight = 1)

            drop("item.death_rune", amount = 20, weight = 32)
            drop("item.chaos_rune", amount = 30, weight = 28)
            drop("item.blood_rune", amount = 15, weight = 16)

            drop("item.coins", amount = 152, weight = 160)
            drop("item.coins", amount = 64, weight = 24)
            drop("item.coins", amount = 95, weight = 24)
            drop("item.coins", amount = 220, weight = 20)

            drop("item.shark", weight = 12)
            drop("item.adamant_bar_noted", amount = 3, weight = 8)
            drop("item.adamantite_ore_noted", amount = 5, weight = 4)
            drop("item.death_talisman", weight = 4)
            drop("item.runite_ore_noted", weight = 4)
            drop("item.shark", amount = 2, weight = 4)
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

        charmDrops {
            gold(amount = 1, percent = 8.4)
            green(amount = 1, percent = 4.2)
            crimson(amount = 1, percent = 8.4)
            blue(amount = 1, percent = 13.4)
        }
    }.apply { name = "Dark Beast" }
}
