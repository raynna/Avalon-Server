package raynna.game.npc.drops.tables

import raynna.game.player.content.treasuretrails.TreasureTrailsManager
import raynna.game.npc.TableCategory
import raynna.game.npc.drops.config.GemTableConfig
import raynna.game.npc.drops.config.HerbTableConfig
import raynna.game.npc.drops.config.SeedTableConfig
import raynna.game.npc.drops.dropTable
import raynna.game.npc.drops.seed.SeedTableType

object IceWarriorDropTable {
    val table =
        dropTable(
            category = TableCategory.REGULAR,
            herbTables = HerbTableConfig(10, 128), // 10/128
            seedTable = SeedTableConfig(SeedTableType.UNCOMMON, numerator = 18, 128), // 18/128
            gemTable = GemTableConfig(3, 128),
        ) {
            main(100) {
                drop("item.iron_battleaxe", weight = 3)
                drop("item.mithril_mace", weight = 1)

                drop("item.nature_rune", amount = 4, weight = 10)
                drop("item.chaos_rune", amount = 3, weight = 8)
                drop("item.law_rune", amount = 2, weight = 7)
                drop("item.cosmic_rune", amount = 2, weight = 5)
                drop("item.mithril_arrow", amount = 3, weight = 5)
                drop("item.adamant_arrow", amount = 2, weight = 2)
                drop("item.death_rune", amount = 2, weight = 3)
                drop("item.blood_rune", amount = 2, weight = 1)
                drop("item.coins", amount = 15, weight = 39)
            }
            tertiary {
                drop(
                    "item.scroll_box_medium",
                    numerator = 1,
                    denominator = 128,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.MEDIUM) },
                )
            }

            charm {
                gold(amount = 1, percent = 39.1)
                green(amount = 1, percent = 1.22)
                crimson(amount = 1, percent = 2.45)
                blue(amount = 1, percent = 0.244)
            }
        }.apply {
            name = "Ice Warrior"
        }
}
