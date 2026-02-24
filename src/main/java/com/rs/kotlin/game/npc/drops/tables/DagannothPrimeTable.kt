package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.config.SeedTableConfig
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.npc.drops.seed.SeedTableType

object DagannothPrimeTable {
    val table =
        dropTable(
            category = TableCategory.BOSS,
            gemTable = GemTableConfig(10, 128),
            rareTable = RareTableConfig(8, 128),
            seedTable =
                SeedTableConfig(SeedTableType.RARE, numerator = 7, denominator = 128),
        ) {

            alwaysDrops {
                drop("item.dagannoth_bones")
                drop("item.dagannoth_hide")
            }

            preRollDrops {
                drop("item.seers_ring", numerator = 1, denominator = 128) { collectionLog = true }
                drop("item.dragon_hatchet", numerator = 1, denominator = 128) { collectionLog = true }
                drop("item.mud_battlestaff", numerator = 1, denominator = 128) { collectionLog = true }
            }

            mainDrops(128) {
                drop("item.earth_battlestaff", weight = 10)
                drop("item.water_battlestaff", weight = 5)
                drop("item.air_battlestaff", weight = 4)
                drop("item.battlestaff", amount = 1..10, weight = 1)
                drop("item.fremennik_blade", amount = 1, weight = 1)
                drop("item.fremennik_shield", amount = 1, weight = 1)
                drop("item.fremennik_helm", amount = 1, weight = 1)
                drop("item.farseer_helm", amount = 1, weight = 1)
                drop("item.skeletal_top", amount = 1, weight = 1)
                drop("item.skeletal_bottoms", amount = 1, weight = 1)
                drop("item.air_rune", amount = 100..200, weight = 6)
                drop("item.earth_rune", amount = 50..100, weight = 5)
                drop("item.blood_rune", amount = 25..75, weight = 2)
                drop("item.law_rune", amount = 10..75, weight = 2)
                drop("item.nature_rune", amount = 25..75, weight = 2)
                drop("item.mud_rune", amount = 25..75, weight = 2)
                drop("item.death_rune", amount = 25..85, weight = 2)
                drop("item.earth_talisman_noted", amount = 25..75, weight = 10)
                drop("item.air_talisman_noted", amount = 25..75, weight = 7)
                drop("item.water_talisman_noted", amount = 1..76, weight = 7)
                drop("item.shark", amount = 5, weight = 10)
                drop("item.oyster_pearls", amount = 1, weight = 5)
                drop("item.pure_essence_noted", amount = 150, weight = 5)
                drop("item.grimy_ranarr", amount = 1, weight = 5)
                drop("item.coins", amount = 500..1109, weight = 3)
            }

            tertiaryDrops {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 42,
                    condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.HARD) },
                )
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 750,
                    condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
            }
        }.apply { name = "Dagannoth Prime" }
}
