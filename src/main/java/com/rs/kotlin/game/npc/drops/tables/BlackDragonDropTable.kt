package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.Skills
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object BlackDragonDropTable {

    val table = dropTable(rareDropTable = true, rolls = 1) {
        alwaysDrops {
            drop("item.dragon_bones")
            drop("item.black_dragonhide");
        }
        mainDrops(128) {
            drop("item.mithril_2h_sword", weight = 4)
            drop("item.mithril_hatchet", weight = 3)
            drop("item.mithril_battleaxe", weight = 3)
            drop("item.rune_knife", amount = 2, weight = 3)
            drop("item.mithril_kiteshield", weight = 1)
            drop("item.adamant_platebody", weight = 1)
            drop("item.rune_longsword", weight = 1)
            drop("item.adamant_javelin", amount = 30, weight = 20)
            drop("item.fire_rune", amount = 50, weight = 8)
            drop("item.adamant_dart_p", amount = 16, weight = 7)
            drop("item.law_rune", amount = 10, weight = 5)
            drop("item.blood_rune", amount = 15, weight = 3)
            drop("item.air_rune", amount = 75, weight = 1)
            drop("item.coins", amount = 196, weight = 49)
            drop("item.coins", amount = 330, weight = 10)
            drop("item.coins", amount = 690, weight = 1)
            drop("item.adamant_bar", weight = 3)
            drop("item.chocolate_cake", weight = 3)
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
                denominator = 500,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) })
            drop(
                "item.black_dragon_tail_bone",
                numerator = 1,
                denominator = 4,
                condition = { player -> false }) //TODO fur'n seek wishlist
            drop(
                "item.black_dragon_egg",
                numerator = 1,
                denominator = 200,
                condition = { player -> player.skills?.getLevelForXp(Skills.SUMMONING) == 99 })
            drop("item.draconic_visage", 1, numerator = 1, denominator = 10000)
            drop("item.starved_ancient_effigy", 1, numerator = 1, denominator = 18000)
        }

        charmDrops {
            gold(amount = 1, percent = 8.96)
            green(amount = 1, percent = 26.9)
            crimson(amount = 1, percent = 6.72)
            blue(amount = 1, percent = 1.34)
        }

        preRollDrops {
            drop(
                "item.dragon_token",
                condition = { player -> false }//TODO RECIPE FOR DISASTER STEP
            )
        }
    }.apply { name = "Black Dragon" }
}
