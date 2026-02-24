package com.rs.kotlin.game.npc.drops.tables

import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.PackageDisplayDrop
import com.rs.kotlin.game.npc.drops.PackageWeightedEntry
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.npc.drops.packageDrop
import java.util.concurrent.ThreadLocalRandom

object CrystalChestTable {
    val table =
        dropTable(category = TableCategory.OTHER) {

            alwaysDrops {
                drop("item.uncut_dragonstone")
            }

            mainDrops(128) {
                add(
                    PackageWeightedEntry(
                        weight = 17,
                        displayDrops = emptyList(),
                        build = { _, _ -> emptyList() },
                    ),
                )
                packageDrop(weight = 34) {
                    drop("item.spinach_roll", 1..1)
                    drop("item.coins", 2000..2000)
                }

                packageDrop(weight = 12) {
                    drop("item.air_rune", 50..50)
                    drop("item.water_rune", 50..50)
                    drop("item.earth_rune", 50..50)
                    drop("item.fire_rune", 50..50)
                    drop("item.body_rune", 50..50)
                    drop("item.mind_rune", 50..50)
                    drop("item.chaos_rune", 10..10)
                    drop("item.death_rune", 10..10)
                    drop("item.cosmic_rune", 10..10)
                    drop("item.nature_rune", 10..10)
                    drop("item.law_rune", 10..10)
                }

                packageDrop(weight = 12) {
                    drop("item.ruby", 2..2)
                    drop("item.diamond", 2..2)
                }

                packageDrop(weight = 12) {
                    drop("item.rune_bar", 3..3)
                }

                packageDrop(weight = 5) {
                    drop("item.coins", 750..750)
                    drop("item.loop_half_of_a_key", 1..1)
                }
                packageDrop(weight = 5) {
                    drop("item.coins", 750..750)
                    drop("item.tooth_half_of_a_key", 1..1)
                }

                packageDrop(weight = 10) {
                    drop("item.iron_ore_noted", 150..150)
                }

                packageDrop(weight = 10) {
                    drop("item.coal_noted", 100..100)
                }

                packageDrop(weight = 8) {
                    drop("item.raw_swordfish", 5..5)
                    drop("item.coins", 1000..1000)
                }

                packageDrop(weight = 2) {
                    drop("item.adamant_sq_shield", 1..1)
                }

                add(
                    PackageWeightedEntry(
                        weight = 1,
                        displayDrops =
                            listOf(
                                PackageDisplayDrop(Rscm.lookup("item.rune_platelegs"), 1..1),
                                PackageDisplayDrop(Rscm.lookup("item.rune_plateskirt"), 1..1),
                            ),
                        build = { _, source ->
                            val item =
                                if (ThreadLocalRandom.current().nextBoolean()) {
                                    "item.rune_platelegs"
                                } else {
                                    "item.rune_plateskirt"
                                }

                            listOf(
                                Drop(
                                    itemId = Rscm.lookup(item),
                                    amount = 1,
                                    source = source,
                                ),
                            )
                        },
                    ),
                )
            }
        }.apply { name = "Crystal chest" }
}
