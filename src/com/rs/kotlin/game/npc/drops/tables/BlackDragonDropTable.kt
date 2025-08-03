package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.Skills
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.*

object BlackDragonDropTable {

    val table = dropTable(rareDropTable = true, rolls = 1) {

        alwaysDrops {
            drop("item.dragon_bones")
            drop("item.black_dragonhide");
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

        mainDrops {
            drop("item.mithril_2h_sword", numerator = 4, denominator = 128)
            drop("item.mithril_hatchet", numerator = 3, denominator = 128)
            drop("item.mithril_battleaxe", numerator = 3, denominator = 128)
            drop("item.rune_knife", amount = 2, numerator = 3, denominator = 128)
            drop("item.mithril_kiteshield", numerator = 1, denominator = 128)
            drop("item.adamant_platebody", numerator = 1, denominator = 128)
            drop("item.rune_longsword", numerator = 1, denominator = 128)
            drop("item.adamant_javelin", amount = 30, numerator = 20, denominator = 128)
            drop("item.fire_rune", amount = 50, numerator = 8, denominator = 128)
            drop("item.adamant_dart_p", amount = 16, numerator = 7, denominator = 128)
            drop("item.law_rune", amount = 10, numerator = 5, denominator = 128)
            drop("item.blood_rune", amount = 15, numerator = 3, denominator = 128)
            drop("item.air_rune", amount = 75, numerator = 1, denominator = 128)
            drop("item.coins", amount = 196, numerator = 40, denominator = 128)
            drop("item.coins", amount = 330, numerator = 10, denominator = 128)
            drop("item.coins", amount = 690, numerator = 1, denominator = 128)
            drop("item.adamant_bar", numerator = 3, denominator = 128)
            drop("item.chocolate_cake", numerator = 3, denominator = 128)
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
    }.apply { name = "Black Dragon" }
}
