package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.Skills
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object NechryaelDropTable {

    val table = dropTable(rareDropTable = true, rolls = 1) {

        alwaysDrops {
            drop("item.infernal_ashes")
        }

        charmDrops {
            gold(amount = 1, percent = 8.51)
            green(amount = 1, percent = 4.25)
            crimson(amount = 1, percent = 29.8)
            blue(amount = 1, percent = 0.851)
        }

        mainDrops {
            drop("item.adamant_platelegs", numerator = 4, denominator = 116)
            drop("item.rune_2h_sword", numerator = 4, denominator = 116)
            drop("item.rune_full_helm", numerator = 3, denominator = 116)
            drop("item.adamant_kiteshield", numerator = 2, denominator = 116)
            drop("item.rune_boots", numerator = 1, denominator = 116)

            drop("item.chaos_rune", amount = 37, numerator = 8, denominator = 116)
            drop("item.death_rune", amount = 5, numerator = 6, denominator = 116)
            drop("item.death_rune", amount = 10, numerator = 6, denominator = 116)
            drop("item.law_rune", amount = 25..35, numerator = 5, denominator = 116)
            drop("item.blood_rune", amount = 15..20, numerator = 4, denominator = 116)

            drop("item.coins", amount = 1000..1499, numerator = 13, denominator = 116)
            drop("item.coins", amount = 1500..2000, numerator = 10, denominator = 116)
            drop("item.coins", amount = 2500..2999, numerator = 6, denominator = 116)
            drop("item.coins", amount = 3000..35000, numerator = 3, denominator = 116)
            drop("item.coins", amount = 500..999, numerator = 2, denominator = 116)
            drop("item.coins", amount = 5000, numerator = 1, denominator = 116)

            drop("item.soft_clay_noted", amount = 25, numerator = 4, denominator = 116)
            drop("item.tuna", numerator = 3, denominator = 116)


        }

        tertiaryDrops {
            drop(
                "item.scroll_box_hard",
                numerator = 1,
                denominator = 128,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) })
        }
    }.apply { name = "Nechryael" }
}
