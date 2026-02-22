package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.HerbTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object BrutalGreenDragonDropTable {

    val table = dropTable(rareDropTable = true,  herbTable = HerbTableConfig(numerator = 15, denominator = 128), rolls = 1) {
        alwaysDrops {
            drop("item.dragon_bones")
            drop("item.green_dragonhide", amount = 2);
        }
        mainDrops(128) {
            drop("item.adamant_dart_p", amount = 25, weight = 5)
            drop("item.adamant_2h_sword", weight = 4)
            drop("item.mithril_hasta", weight = 3)
            drop("item.adamant_knife", amount = 8, weight = 3)
            drop("item.adamant_helm", weight = 3)
            drop("item.rune_thrownaxe", amount = 8, weight = 3)
            drop("item.adamant_spear", weight = 2)
            drop("item.adamant_chainbody", weight = 1)
            drop("item.adamant_kiteshield", weight = 1)
            drop("item.adamant_platelegs", weight = 1)
            drop("item.rune_full_helm", weight = 1)
            drop("item.rune_chainbody", weight = 1)

            drop("item.blood_rune", amount = 20, weight = 29)
            drop("item.lava_rune", amount = 35, weight = 8)
            drop("item.steam_rune", amount = 37, weight = 6)
            drop("item.nature_rune", amount = 17, weight = 5)
            drop("item.law_rune", amount = 15, weight = 3)
            drop("item.adamant_arrow", amount = 8, weight = 3)

            drop("item.mithril_ore_noted", amount = 5, weight = 3)

            drop("item.coins", amount = 242, weight = 11)
            drop("item.coins", amount = 621, weight = 10)

            drop("item.curry", amount = 1..2, weight = 2)


        }
        tertiaryDrops {
            drop(
                "item.scroll_box_hard",
                numerator = 1,
                denominator = 128,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) })
        }

        charmDrops {
            gold(amount = 1, percent = 14.3)
            green(amount = 1, percent = 35.6)
            crimson(amount = 1, percent = 14.3)
            blue(amount = 1, percent = 2.85)
        }
    }.apply { name = "Brutal Green Dragon" }
}
