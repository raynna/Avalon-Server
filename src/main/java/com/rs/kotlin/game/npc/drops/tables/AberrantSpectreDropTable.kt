package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.MonsterCategory
import com.rs.kotlin.game.npc.drops.HerbTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object AberrantSpectreDropTable {

    val table = dropTable(
        category = MonsterCategory.SLAYER,
        herbTable = HerbTableConfig(amount = 1..3, numerator = 78, denominator = 128),
        rareDropTable = true,
        rolls = 1) {

        charmDrops {
            gold(amount = 1, percent = 12.0)
            green(amount = 1, percent = 6.01)
            crimson(amount = 1, percent = 4.01)
            blue(amount = 1, percent = 4.81)
        }

        mainDrops(128) {
            drop("item.steel_hatchet", weight = 3)
            drop("item.mithril_kiteshield", weight = 1)
            drop("item.lava_battlestaff", weight = 1)
            drop("item.adamant_platelegs", weight = 1)
            drop("item.rune_full_helm", weight = 1)
            drop("item.mystic_robe_bottoms_dark", weight = 1)

            drop("item.coins", amount = 460, weight = 1)
        }

        tertiaryDrops {
            drop(
                "item.scroll_box_hard",
                numerator = 1,
                denominator = 128,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) })
        }
    }.apply { name = "Aberrant spectre" }
}
