package com.rs.kotlin.game.npc.drops.tables

import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.PackageDisplayDrop
import com.rs.kotlin.game.npc.drops.PackageWeightedEntry
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.npc.drops.either
import com.rs.kotlin.game.npc.drops.packageDrop

object CrystalChestTable {
    val table =
        dropTable(sourceAction = "opening", category = TableCategory.OTHER) {

            alwaysDrops {
                drop("item.uncut_dragonstone")
            }

            mainDrops(128) {
                add(
                    PackageWeightedEntry(
                        weight = 17,
                        displayDrops = emptyList(),
                        build = { _ -> emptyList() },
                    ),
                )
                packageDrop(weight = 34) {
                    dropMany("item.spinach_roll" to 1, "item.coins" to 2000)
                }

                packageDrop(weight = 12) {
                    dropMany(
                        "item.air_rune" to 50,
                        "item.water_rune" to 50,
                        "item.earth_rune" to 50,
                        "item.fire_rune" to 50,
                        "item.body_rune" to 50,
                        "item.mind_rune" to 50,
                        "item.chaos_rune" to 10,
                        "item.death_rune" to 10,
                        "item.cosmic_rune" to 10,
                        "item.nature_rune" to 10,
                        "item.law_rune" to 10,
                    )
                }

                packageDrop(weight = 12) {
                    dropMany("item.ruby" to 2, "item.diamond" to 2)
                }

                packageDrop(weight = 12) {
                    drop("item.rune_bar", 3..3)
                }

                packageDrop(weight = 10) {
                    drop("item.iron_ore_noted", 150..150)
                }

                packageDrop(weight = 10) {
                    drop("item.coal_noted", 100..100)
                }

                packageDrop(weight = 8) {
                    dropMany("item.raw_swordfish" to 5, "item.coins" to 1000)
                }

                packageDrop(weight = 2) {
                    drop("item.adamant_sq_shield", 1..1)
                }
                add(
                    PackageWeightedEntry(
                        weight = 10,
                        displayDrops =
                            listOf(
                                PackageDisplayDrop("item.coins", 750..750),
                                PackageDisplayDrop("item.loop_half_of_a_key", 1..1),
                                PackageDisplayDrop("item.tooth_half_of_a_key", 1..1),
                            ),
                        build = { ctx ->
                            val key = either("item.loop_half_of_a_key", "item.tooth_half_of_a_key")

                            listOf(
                                Drop(
                                    itemId = Rscm.lookup("item.coins"),
                                    amount = 750,
                                    context = ctx,
                                ),
                                Drop(
                                    itemId = Rscm.lookup(key),
                                    amount = 1,
                                    context = ctx,
                                ),
                            )
                        },
                    ),
                )
                add(
                    PackageWeightedEntry(
                        weight = 1,
                        // just to make sure dynamic items are shown on dropinterface
                        displayDrops =
                            listOf(
                                PackageDisplayDrop("item.rune_platelegs", 1..1),
                                PackageDisplayDrop("item.rune_plateskirt", 1..1),
                            ),
                        build = { ctx ->
                            val item = either("item.rune_platelegs", "item.rune_plateskirt")

                            listOf(
                                Drop(
                                    itemId = Rscm.lookup(item),
                                    amount = 1,
                                    context = ctx,
                                ),
                            )
                        },
                    ),
                )
            }
        }.apply { name = "Crystal chest" }
}
