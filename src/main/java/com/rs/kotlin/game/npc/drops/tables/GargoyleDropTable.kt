package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object GargoyleDropTable {

    val table = dropTable(rareDropTable = true, rolls = 1) {

        charmDrops {
            gold(amount = 1, percent = 9.78)
            green(amount = 1, percent = 4.89)
            crimson(amount = 1, percent = 4.89)
            blue(amount = 1, percent = 6.84)
        }

        mainDrops {
            drop("item.granite_maul", numerator = 2, denominator = 512)
            drop("item.mystic_robe_top_dark", numerator = 1, denominator = 512)

            drop("item.adamant_platelegs", numerator = 16, denominator = 512)
            drop("item.rune_full_helm", numerator = 12, denominator = 512)
            drop("item.rune_2h_sword", numerator = 8, denominator = 512)
            drop("item.adamant_boots", numerator = 4, denominator = 512)
            drop("item.rune_battleaxe", numerator = 4, denominator = 512)
            drop("item.rune_platelegs", numerator = 4, denominator = 512)

            drop("item.fire_rune", amount = 75, numerator = 40, denominator = 512)
            drop("item.chaos_rune", amount = 30, numerator = 32, denominator = 512)
            drop("item.fire_rune", amount = 150, numerator = 24, denominator = 512)
            drop("item.death_rune", amount = 15, numerator = 20, denominator = 512)

            drop("item.gold_ore_noted", amount = 10..20, numerator = 40, denominator = 512)
            drop("item.pure_essence_noted", amount = 150, numerator = 24, denominator = 512)
            drop("item.steel_bar_noted", amount = 15, numerator = 24, denominator = 512)
            drop("item.gold_bar_noted", amount = 10..15, numerator = 12, denominator = 512)
            drop("item.mithril_bar_noted", amount = 15, numerator = 8, denominator = 512)
            drop("item.runite_ore", numerator = 8, denominator = 512)

            drop("item.coins", amount = 400..800, numerator = 112, denominator = 512)
            drop("item.coins", amount = 500..1000, numerator = 80, denominator = 512)
            drop("item.coins", amount = 10000, numerator = 20, denominator = 512)

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
        }
    }.apply { name = "Gargoyle" }
}
