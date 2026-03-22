package raynna.game.npc.drops.tables

import raynna.game.player.content.treasuretrails.TreasureTrailsManager
import raynna.game.npc.TableCategory
import raynna.game.npc.drops.dropTable

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
