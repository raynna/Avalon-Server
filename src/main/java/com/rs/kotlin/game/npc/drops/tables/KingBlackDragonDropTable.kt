package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object KingBlackDragonDropTable {
    val kingBlackDragonTable =
        dropTable(
            gemTable = GemTableConfig(2, 128),
            rareTable = RareTableConfig(numerator = 8, denominator = 128),
            category = TableCategory.BOSS,
        ) {
            alwaysDrops {
                drop("item.dragon_bones")
                drop("item.black_dragonhide", 2)
            }
            mainDrops(128) {
                drop("item.rune_longsword", weight = 10)
                drop("item.adamant_platebody", weight = 9)
                drop("item.adamant_kiteshield", weight = 3)
                drop("item.dragon_helm", weight = 1)
                drop("item.air_rune", amount = 300, weight = 10)
                drop("item.iron_arrow", amount = 690, weight = 10)
                drop("item.runite_bolts", amount = 10..20, weight = 10)
                drop("item.fire_rune", amount = 300, weight = 5)
                drop("item.fire_rune", amount = 300, weight = 10)
                drop("item.law_rune", amount = 30, weight = 5)
                drop("item.blood_rune", amount = 30, weight = 5)
                drop("item.yew_logs_noted", amount = 150, weight = 10)
                drop("item.adamant_bar", amount = 3, weight = 5)
                drop("item.rune_bar", weight = 3)
                drop("item.gold_ore_noted", amount = 100, weight = 2)
                drop("item.amulet_of_power", weight = 7)
                drop("item.dragon_arrowheads", amount = 5..14, weight = 5)
                drop("item.dragon_dart_tip", amount = 5..14, weight = 5)
                drop("item.runite_limbs", weight = 4)
                drop("item.shark", amount = 4, weight = 4)
            }
            preRollDrops {
                drop("item.dragon_pickaxe", numerator = 1, denominator = 1000) { collectionLog = true }
                drop("item.dragon_full_helm_ornament_kit_or", numerator = 1, denominator = 2000) { collectionLog = true }
                drop("item.dragon_full_helm_ornament_kit_sp", numerator = 1, denominator = 2000) { collectionLog = true }
                drop("item.dragon_platebody_ornament_kit_or", numerator = 1, denominator = 2000) { collectionLog = true }
                drop("item.dragon_platebody_ornament_kit_sp", numerator = 1, denominator = 2000) { collectionLog = true }
                drop("item.dragon_platelegs_skirt_ornament_kit_or", numerator = 1, denominator = 2000) { collectionLog = true }
                drop("item.dragon_platelegs_skirt_ornament_kit_sp", numerator = 1, denominator = 2000) { collectionLog = true }
                drop("item.dragon_sq_shield_ornament_kit_or", numerator = 1, denominator = 2000) { collectionLog = true }
                drop("item.dragon_sq_shield_ornament_kit_sp", numerator = 1, denominator = 2000) { collectionLog = true }
            }
            tertiaryDrops {
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 450,
                    condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
                drop("item.kbd_heads", numerator = 1, denominator = 128) { collectionLog = true }
                drop("item.draconic_visage", 1, numerator = 1, denominator = 5000) {
                    collectionLog = true
                    announce = true
                }
                drop("item.starved_ancient_effigy", 1, numerator = 1, denominator = 6000)
            }

            charmDrops {
                gold(amount = 4, percent = 7.9)
                green(amount = 4, percent = 3.95)
                crimson(amount = 4, percent = 63.2)
                blue(amount = 4, percent = 1.58)
            }
        }.apply { name = "King Black Dragon" }
}
