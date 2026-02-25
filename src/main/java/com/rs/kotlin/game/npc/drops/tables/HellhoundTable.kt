package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.dropTable

object HellhoundTable {
    val table =
        dropTable(category = TableCategory.SLAYER) {

            charm {
                gold(amount = 1, percent = 69.0)
                green(amount = 1, percent = 5.0)
                crimson(amount = 1, percent = 5.0)
                blue(amount = 1, percent = 1.0)
            }
            always {
                drop("item.bones")
            }

            main(50) {
                drop("item.death_rune", amount = 10, weight = 6)
                drop("item.death_rune", amount = 20, weight = 3)
                drop("item.death_rune", amount = 50, weight = 1)
            }

            tertiary {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 64,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.EASY) },
                )
            }
        }.apply { name = "Hellhound" }
}
