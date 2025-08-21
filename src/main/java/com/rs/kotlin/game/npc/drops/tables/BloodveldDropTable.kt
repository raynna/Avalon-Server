package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object BloodveldDropTable {

    val table = dropTable(herbTable = true, rareDropTable = true, rolls = 1) {

        alwaysDrops {
            drop("item.bones")
        }

        charmDrops {
            gold(amount = 1, percent = 9.1)
            green(amount = 1, percent = 31.8)
            crimson(amount = 1, percent = 4.55)
            blue(amount = 1, percent = 0.91)
        }

        mainDrops {
            drop("item.steel_hatchet", numerator = 4, denominator = 128)
            drop("item.steel_full_helm", numerator = 4, denominator = 128)
            drop("item.steel_scimitar", numerator = 2, denominator = 128)
            drop("item.black_boots", numerator = 1, denominator = 128)
            drop("item.mithril_sq_shield", numerator = 1, denominator = 128)
            drop("item.mithril_chainbody", numerator = 1, denominator = 128)
            drop("item.rune_helm", numerator = 1, denominator = 128)

            drop("item.fire_rune", amount = 60, numerator = 8, denominator = 128)
            drop("item.blood_rune", amount = 10, numerator = 5, denominator = 128)
            drop("item.blood_rune", amount = 3, numerator = 3, denominator = 128)
            drop("item.blood_rune", amount = 30, numerator = 1, denominator = 128)

            drop("item.coins", amount = 120, numerator = 30, denominator = 128)
            drop("item.coins", amount = 40, numerator = 29, denominator = 128)
            drop("item.coins", amount = 200, numerator = 10, denominator = 128)
            drop("item.coins", amount = 10, numerator = 7, denominator = 128)
            drop("item.coins", amount = 450, numerator = 1, denominator = 128)

            drop("item.bones", numerator = 10, denominator = 128)
            drop("item.big_bones", numerator = 7, denominator = 128)
            drop("item.big_bones", amount = 3, numerator = 3, denominator = 128)
            drop("item.meat_pizza", numerator = 3, denominator = 128)
            drop("item.gold_ore", numerator = 2, denominator = 128)
        }

        tertiaryDrops {
            drop(
                "item.scroll_box_hard",
                numerator = 1,
                denominator = 128,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) })
        }
    }.apply { name = "Bloodveld" }
}
