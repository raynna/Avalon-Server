package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.MonsterCategory
import com.rs.kotlin.game.npc.drops.HerbTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object HellhoundTable {

    val table = dropTable(
        category = MonsterCategory.SLAYER,
        rareDropTable = true,
        rolls = 1) {

        charmDrops {
            gold(amount = 1, percent = 69.0)
            green(amount = 1, percent = 5.0)
            crimson(amount = 1, percent = 5.0)
            blue(amount = 1, percent = 1.0)
        }
        alwaysDrops {
            drop("item.bones")
        }

        mainDrops(50) {
            drop("item.death_rune", amount = 10, weight = 6)
            drop("item.death_rune", amount = 20, weight = 3)
            drop("item.death_rune", amount = 50, weight = 1)
        }

        tertiaryDrops {
            drop(
                "item.scroll_box_hard",
                numerator = 1,
                denominator = 64,
                condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.EASY) })
        }
    }.apply { name = "Hellhound" }
}
