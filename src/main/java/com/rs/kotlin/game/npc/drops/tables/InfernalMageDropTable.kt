package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.Skills
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.dropTable

object InfernalMageDropTable {

    val table = dropTable(rolls = 1) {

        alwaysDrops {
            drop("item.bones")
        }

        charmDrops {
            gold(amount = 1, percent = 33.5)
            green(amount = 1, percent = 2.39)
            crimson(amount = 1, percent = 2.39)
            blue(amount = 1, percent = 0.479)
        }

        preRollDrops {
            drop(
                "item.mystic_boots_dark",
                numerator = 1,
                denominator = 512,
            )
            drop(
                "item.mystic_hat_dark",
                numerator = 1,
                denominator = 512,
            )
            drop(
                "item.lava_battlestaff",
                numerator = 1,
                denominator = 1000,
            )
        }

        mainDrops {
            drop("item.staff", numerator = 8, denominator = 128)
            drop("item.staff_of_fire", numerator = 1, denominator = 128)

            drop("item.earth_rune", amount = 10, numerator = 6, denominator = 128)
            drop("item.fire_rune", amount = 10, numerator = 6, denominator = 128)
            drop("item.earth_rune", amount = 36, numerator = 4, denominator = 128)
            drop("item.air_rune", amount = 10, numerator = 3, denominator = 128)
            drop("item.water_rune", amount = 10, numerator = 3, denominator = 128)
            drop("item.air_rune", amount = 18, numerator = 2, denominator = 128)
            drop("item.water_rune", amount = 18, numerator = 2, denominator = 128)
            drop("item.earth_rune", amount = 18, numerator = 2, denominator = 128)
            drop("item.fire_rune", amount = 18, numerator = 2, denominator = 128)

            drop("item.death_rune", amount = 7, numerator = 18, denominator = 128)
            drop("item.mind_rune", amount = 18, numerator = 2, denominator = 128)
            drop("item.body_rune", amount = 18, numerator = 2, denominator = 128)
            drop("item.blood_rune", amount = 4, numerator = 2, denominator = 128)

            drop("item.coins", amount = 1, numerator = 21, denominator = 128)
            drop("item.coins", amount = 2, numerator = 16, denominator = 128)
            drop("item.coins", amount = 4, numerator = 9, denominator = 128)
            drop("item.coins", amount = 29, numerator = 3, denominator = 128)
        }

    }.apply { name = "Infernal Mage" }
}
