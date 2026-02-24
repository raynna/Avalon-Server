package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.config.SeedTableConfig
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.npc.drops.seed.SeedTableType

object NechryaelDropTable {
    val table =
        dropTable(
            gemTable = GemTableConfig(5, 116),
            rareTable = RareTableConfig(numerator = 1, denominator = 116),
            seedTable = SeedTableConfig(SeedTableType.RARE, 18, 116),
            category = TableCategory.SLAYER,
        ) {

            alwaysDrops {
                drop("item.infernal_ashes")
            }

            charmDrops {
                gold(amount = 1, percent = 8.51)
                green(amount = 1, percent = 4.25)
                crimson(amount = 1, percent = 29.8)
                blue(amount = 1, percent = 0.851)
            }

            mainDrops(116) {
                drop("item.adamant_platelegs", weight = 4)
                drop("item.rune_2h_sword", weight = 4)
                drop("item.rune_full_helm", weight = 3)
                drop("item.adamant_kiteshield", weight = 2)
                drop("item.rune_boots", weight = 1) { collectionLog = true }

                drop("item.chaos_rune", amount = 37, weight = 8)
                drop("item.death_rune", amount = 5, weight = 6)
                drop("item.death_rune", amount = 10, weight = 6)
                drop("item.law_rune", amount = 25..35, weight = 5)
                drop("item.blood_rune", amount = 15..20, weight = 4)

                drop("item.coins", amount = 1000..1499, weight = 13)
                drop("item.coins", amount = 1500..2000, weight = 10)
                drop("item.coins", amount = 2500..2999, weight = 6)
                drop("item.coins", amount = 3000..35000, weight = 3)
                drop("item.coins", amount = 500..999, weight = 2)
                drop("item.coins", amount = 5000, weight = 1)

                drop("item.soft_clay_noted", amount = 25, weight = 4)
                drop("item.tuna", weight = 3)
            }

            tertiaryDrops {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 128,
                    condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                )
            }
        }.apply { name = "Nechryael" }
}
