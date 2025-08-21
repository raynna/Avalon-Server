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

        mainDrops(512) {
            drop("item.granite_maul", weight = 2)
            drop("item.mystic_robe_top_dark", weight = 1)

            drop("item.adamant_platelegs", weight = 16)
            drop("item.rune_full_helm", weight = 12)
            drop("item.rune_2h_sword", weight = 8)
            drop("item.adamant_boots", weight = 4)
            drop("item.rune_battleaxe", weight = 4)
            drop("item.rune_platelegs", weight = 4)

            drop("item.fire_rune", amount = 75, weight = 40)
            drop("item.chaos_rune", amount = 30, weight = 32)
            drop("item.fire_rune", amount = 150, weight = 24)
            drop("item.death_rune", amount = 15, weight = 20)

            drop("item.gold_ore_noted", amount = 10..20, weight = 40)
            drop("item.pure_essence_noted", amount = 150, weight = 24)
            drop("item.steel_bar_noted", amount = 15, weight = 24)
            drop("item.gold_bar_noted", amount = 10..15, weight = 12)
            drop("item.mithril_bar_noted", amount = 15, weight = 8)
            drop("item.runite_ore", weight = 8)

            drop("item.coins", amount = 400..800, weight = 112)
            drop("item.coins", amount = 500..1000, weight = 80)
            drop("item.coins", amount = 10000, weight = 20)

            drop("item.lobster", weight = 8)
            drop("item.cosmic_talisman", weight = 4)
            drop("item.chaos_talisman", weight = 4)
            drop("item.defence_potion_3", weight = 4)


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
