package com.rs.kotlin.game.npc.drops.tables.slayer

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object AbyssalDemonTable {
    val table =
        dropTable(
            category = TableCategory.SLAYER,
            herbTables = HerbTableConfig(numerator = 19, denominator = 128),
            rareTable = RareTableConfig(numerator = 2, denominator = 128),
            gemTable = GemTableConfig(numerator = 5, denominator = 128),
        ) {

            always {
                drop("item.infernal_ashes")
            }

            charm {
                gold(amount = 1, percent = 8.96)
                green(amount = 1, percent = 4.48)
                crimson(amount = 1, percent = 31.4)
                blue(amount = 1, percent = 0.896)
            }

            prerollDenom {
                drop("item.abyssal_whip", numerator = 1, denominator = 512) {
                    collectionLog = true
                }
            }

            main(128) {
                drop("item.black_sword", weight = 4)
                drop("item.steel_battleaxe", weight = 3)
                drop("item.black_hatchet", weight = 2)
                drop("item.mithril_kiteshield", weight = 1)
                drop("item.rune_chainbody", weight = 1)
                drop("item.rune_helm", weight = 1)

                drop("item.air_rune", amount = 50, weight = 8)
                drop("item.chaos_rune", amount = 10, weight = 7)
                drop("item.blood_rune", amount = 7, weight = 4)
                drop("item.law_rune", amount = 3, weight = 1)

                drop("item.pure_essence_noted", amount = 60, weight = 5)
                drop("item.adamant_bar", weight = 2)

                drop("item.coins", amount = 132, weight = 35)
                drop("item.coins", amount = 220, weight = 9)
                drop("item.coins", amount = 30, weight = 7)
                drop("item.coins", amount = 44, weight = 6)
                drop("item.coins", amount = 460, weight = 1)

                drop("item.lobster", weight = 2)
                drop("item.cosmic_talisman", weight = 1)
                drop("item.chaos_talisman", weight = 1)
                drop("item.defence_potion_3", weight = 1)
            }

            tertiary {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 128,
                    condition = { context ->
                        !context.player.treasureTrailsManager.hasClueScrollByLevel(
                            TreasureTrailsManager.HARD,
                        )
                    },
                )
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 1200,
                    condition = { context ->
                        !context.player.treasureTrailsManager.hasClueScrollByLevel(
                            TreasureTrailsManager.ELITE,
                        )
                    },
                )
                drop(
                    "item.abyssal_head",
                    numerator = 1,
                    denominator = 6000,
                ) {
                    collectionLog = true
                }
            }
        }.apply { name = "Abyssal demon" }
}
