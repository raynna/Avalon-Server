package com.rs.kotlin.game.npc.drops.tables

import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.dropTable

object InfernalMageDropTable {
    val table =
        dropTable(category = TableCategory.SLAYER) {

            alwaysDrops {
                drop("item.bones")
            }

            charmDrops {
                gold(amount = 1, percent = 33.5)
                green(amount = 1, percent = 2.39)
                crimson(amount = 1, percent = 2.39)
                blue(amount = 1, percent = 0.479)
            }

            tertiaryDrops {
                drop("item.mystic_boots_dark", numerator = 1, denominator = 512) { collectionLog = true }
                drop("item.mystic_hat_dark", numerator = 1, denominator = 512) { collectionLog = true }
                drop("item.lava_battlestaff", numerator = 1, denominator = 1000) { collectionLog = true }
            }

            mainDrops(128) {
                drop("item.staff", weight = 8)
                drop("item.staff_of_fire", weight = 1)

                drop("item.earth_rune", amount = 10, weight = 6)
                drop("item.fire_rune", amount = 10, weight = 6)
                drop("item.earth_rune", amount = 36, weight = 4)
                drop("item.air_rune", amount = 10, weight = 3)
                drop("item.water_rune", amount = 10, weight = 3)
                drop("item.air_rune", amount = 18, weight = 2)
                drop("item.water_rune", amount = 18, weight = 2)
                drop("item.earth_rune", amount = 18, weight = 2)
                drop("item.fire_rune", amount = 18, weight = 2)

                drop("item.death_rune", amount = 7, weight = 18)
                drop("item.mind_rune", amount = 18, weight = 2)
                drop("item.body_rune", amount = 18, weight = 2)
                drop("item.blood_rune", amount = 4, weight = 2)

                drop("item.coins", amount = 1, weight = 21)
                drop("item.coins", amount = 2, weight = 16)
                drop("item.coins", amount = 4, weight = 9)
                drop("item.coins", amount = 29, weight = 3)
            }
        }.apply { name = "Infernal Mage" }
}
