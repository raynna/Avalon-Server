package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object MithrilDragonDropTable {
    val table =
        dropTable(
            gemTable = GemTableConfig(4, 128),
            rareTable = RareTableConfig(numerator = 1, denominator = 128),
            herbTables = HerbTableConfig(15, 128),
        ) {
            always {
                drop("item.dragon_bones")
                drop("item.mithril_bar", amount = 3)
            }
            prerollDenom {
                drop("item.dragon_full_helm", numerator = 1, denominator = 32768) {
                    collectionLog = true
                    announce = true
                }
                drop("item.chewed_bones", numerator = 1, denominator = 128) {
                    collectionLog = true
                }
            }
            main(128) {
                drop("item.rune_battleaxe", weight = 8)
                drop("item.rune_dart_p", amount = 14, weight = 7)
                drop("item.rune_battleaxe", weight = 4)
                drop("item.rune_knife", amount = 8, weight = 4)
                drop("item.rune_mace", weight = 3)
                drop("item.rune_spear", weight = 2)
                drop("item.rune_full_helm", weight = 1)

                drop("item.blood_rune", amount = 27, weight = 19)
                drop("item.rune_javelin", amount = 8, weight = 14)
                drop("item.runite_bolts", amount = 10..21, weight = 6)
                drop("item.soul_rune", amount = 10, weight = 5)
                drop("item.rune_arrow", amount = 8, weight = 3)
                drop("item.shark", amount = 1, weight = 6)
                drop("item.shark", amount = 1, weight = 4)
                drop("item.prayer_mix_2", amount = 1, weight = 2)
                drop("item.shark", amount = 6, weight = 2)
                drop("item.super_attack_mix_2", amount = 1, weight = 2)
                drop("item.super_defence_mix_2", amount = 1, weight = 2)
                drop("item.super_strength_mix_2", amount = 1, weight = 2)

                drop("item.coins", amount = 600, weight = 17)
                drop("item.coins", amount = 876, weight = 7)
                drop("item.rune_bar", amount = 2, weight = 3)
            }
            tertiary {
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 350,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
                drop(
                    "item.draconic_visage",
                    numerator = 1,
                    denominator = 10000,
                ) {
                    collectionLog = true
                    announce = true
                }
            }

            charm {
                gold(amount = 4, percent = 19.8)
                green(amount = 4, percent = 49.5)
                crimson(amount = 4, percent = 19.8)
                blue(amount = 4, percent = 3.96)
            }
        }.apply {
            name = "Mithril dragon"
            collectionGroup = "Dragons"
        }
}
