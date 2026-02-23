package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.MonsterCategory
import com.rs.kotlin.game.npc.drops.HerbTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object BloodveldDropTable {

    val table = dropTable(
        category = MonsterCategory.SLAYER,
        herbTables = HerbTableConfig(numerator = 1, denominator = 128),
        rareDropTable = true,
        rolls = 1) {

        alwaysDrops {
            drop("item.bones")
        }

        charmDrops {
            gold(amount = 1, percent = 9.1)
            green(amount = 1, percent = 31.8)
            crimson(amount = 1, percent = 4.55)
            blue(amount = 1, percent = 0.91)
        }

        mainDrops(128) {
            drop("item.steel_hatchet", numerator = 4)
            drop("item.steel_full_helm", weight = 4)
            drop("item.steel_scimitar", weight = 2)
            drop("item.black_boots", weight = 1)
            drop("item.mithril_sq_shield", weight = 1)
            drop("item.mithril_chainbody", weight = 1)
            drop("item.rune_helm", weight = 1)

            drop("item.fire_rune", amount = 60, weight = 8)
            drop("item.blood_rune", amount = 10, weight = 5)
            drop("item.blood_rune", amount = 3, weight = 3)
            drop("item.blood_rune", amount = 30, weight = 1)

            drop("item.coins", amount = 120, weight = 30)
            drop("item.coins", amount = 40, weight = 29)
            drop("item.coins", amount = 200, weight = 10)
            drop("item.coins", amount = 10, weight = 7)
            drop("item.coins", amount = 450, weight = 1)

            drop("item.bones", weight = 10)
            drop("item.big_bones", weight = 7)
            drop("item.big_bones", amount = 3, weight = 3)
            drop("item.meat_pizza", weight = 3)
            drop("item.gold_ore", weight = 2)
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
