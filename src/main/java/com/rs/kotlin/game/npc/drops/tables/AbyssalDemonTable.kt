package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.Skills
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object AbyssalDemonTable {

    val table = dropTable(herbTable = true, rareDropTable = true, rolls = 1) {

        alwaysDrops {
            drop("item.infernal_ashes")
        }

        charmDrops {
            gold(amount = 1, percent = 8.96)
            green(amount = 1, percent = 4.48)
            crimson(amount = 1, percent = 31.4)
            blue(amount = 1, percent = 0.896)
        }

        mainDrops {
            drop("item.black_sword", numerator = 16, denominator = 512)
            drop("item.steel_battleaxe", numerator = 12, denominator = 512)
            drop("item.black_hatchet", numerator = 8, denominator = 512)
            drop("item.mithril_kiteshield", numerator = 4, denominator = 512)
            drop("item.rune_chainbody", numerator = 4, denominator = 512)
            drop("item.rune_helm", numerator = 4, denominator = 512)
            drop("item.abyssal_whip", numerator = 1, denominator = 512)

            drop("item.air_rune", amount = 50, numerator = 32, denominator = 512)
            drop("item.chaos_rune", amount = 10, numerator = 28, denominator = 512)
            drop("item.blood_rune", amount = 7, numerator = 16, denominator = 512)
            drop("item.law_rune", amount = 3, numerator = 4, denominator = 512)

            drop("item.pure_essence_noted", amount = 60, numerator = 20, denominator = 512)
            drop("item.adamant_bar", numerator = 8, denominator = 512)

            drop("item.coins", amount = 132, numerator = 140, denominator = 512)
            drop("item.coins", amount = 220, numerator = 36, denominator = 512)
            drop("item.coins", amount = 30, numerator = 28, denominator = 512)
            drop("item.coins", amount = 44, numerator = 24, denominator = 512)
            drop("item.coins", amount = 460, numerator = 4, denominator = 512)

            drop("item.lobster", numerator = 8, denominator = 512)
            drop("item.cosmic_talisman", numerator = 4, denominator = 512)
            drop("item.chaos_talisman", numerator = 4, denominator = 512)
            drop("item.defence_potion_3", numerator = 4, denominator = 512)


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
            drop(
                "item.abyssal_head",
                numerator = 1,
                denominator = 6000)
        }
    }.apply { name = "Abyssal demon" }
}
