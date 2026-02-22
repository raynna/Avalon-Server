package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.Skills
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.HerbTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object RedDragonDropTable {

    val table = dropTable(rareDropTable = true,
        rolls = 1,
        herbTable = HerbTableConfig(1..1, 2, 128)) {
        alwaysDrops {
            drop("item.dragon_bones")
            drop("item.red_dragonhide");
        }
        mainDrops(128) {
            drop("item.mithril_2h_sword", weight = 4)
            drop("item.mithril_hatchet", weight = 3)
            drop("item.mithril_battleaxe", weight = 3)
            drop("item.rune_dart", amount = 8, weight = 3)
            drop("item.mithril_javelin", amount = 20, weight = 1)
            drop("item.mithril_kiteshield", weight = 1)
            drop("item.adamant_platebody", weight = 1)
            drop("item.rune_longsword", weight = 1)

            drop("item.rune_arrow", amount = 4, weight = 8)
            drop("item.law_rune", amount = 4, weight = 5)
            drop("item.blood_rune", amount = 2, weight = 4)
            drop("item.death_rune", amount = 5, weight = 3)

            drop("item.coins", amount = 196, weight = 40)
            drop("item.coins", amount = 66, weight = 29)
            drop("item.coins", amount = 330, weight = 10)
            drop("item.coins", amount = 690, weight = 1)

            drop("item.chocolate_cake", amount = 3, weight = 3)
            drop("item.adamant_bar_noted", amount = 1, weight = 1)
        }
        tertiaryDrops {
            drop(
                "item.scroll_box_hard",
                numerator = 1,
                denominator = 128,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) })
            drop(
                "item.red_dragon_egg",
                numerator = 1,
                denominator = 200,
                condition = { player -> player.skills?.getLevelForXp(Skills.SUMMONING) == 99 })
        }

        charmDrops {
            gold(amount = 1, percent = 18.1)
            green(amount = 1, percent = 45.2)
            crimson(amount = 1, percent = 18.1)
            blue(amount = 1, percent = 3.62)
        }
    }.apply { name = "Red Dragon" }
}
