package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object IronDragonDropTable {
    val table =
        dropTable(
            rareTable = RareTableConfig(2, 128),
            gemTable = GemTableConfig(3, 128),
        ) {
            always {
                drop("item.dragon_bones")
                drop("item.iron_bar", amount = 5)
            }
            prerollDenom {
                drop("item.dragon_plateskirt", numerator = 1, denominator = 1024) { collectionLog = true }
                drop("item.dragon_platelegs", numerator = 1, denominator = 1024) { collectionLog = true }
            }
            main(128) {
                drop("item.rune_dart_p", amount = 9, weight = 7)
                drop("item.adamant_2h_sword", amount = 1, weight = 4)
                drop("item.adamant_hatchet", amount = 1, weight = 3)
                drop("item.adamant_battleaxe", amount = 1, weight = 3)
                drop("item.rune_knife", amount = 5, weight = 3)
                drop("item.adamant_sq_shield", amount = 1, weight = 1)
                drop("item.rune_helm", amount = 1, weight = 1)
                drop("item.rune_battleaxe", amount = 1, weight = 1)

                drop("item.rune_javelin", amount = 4, weight = 20)
                drop("item.blood_rune", amount = 15, weight = 19)
                drop("item.adamant_bolts", amount = 2..12, weight = 6)
                drop("item.soul_rune", amount = 3, weight = 5)

                drop("item.coins", amount = 270, weight = 20)
                drop("item.coins", amount = 550, weight = 10)
                drop("item.coins", amount = 990, weight = 1)

                drop("item.super_strength_1", amount = 1, weight = 8)
                drop("item.runite_limbs", amount = 1, weight = 5)
                drop("item.adamant_bar", amount = 2, weight = 3)
                drop("item.curry", amount = 1, weight = 3)
            }
            tertiary {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 128,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                )
                drop("item.draconic_visage", numerator = 1, denominator = 10000) {
                    collectionLog = true
                    announce = true
                }
            }

            charm {
                gold(amount = 4, percent = 13.2)
                green(amount = 4, percent = 33.0)
                crimson(amount = 4, percent = 13.2)
                blue(amount = 4, percent = 2.64)
            }
        }.apply {
            name = "Iron dragon"
            collectionGroup = "Dragons"
        }
}
