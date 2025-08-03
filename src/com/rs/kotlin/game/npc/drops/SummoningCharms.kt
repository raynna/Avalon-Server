package com.rs.kotlin.game.npc.drops

class SummoningCharms(private val dropTable: DropTable) {
    fun gold(amount: Int = 1, numerator: Int = 1, denominator: Int = 4) {
        with(dropTable) {
            drop("item.gold_charm", amount, numerator, denominator)
        }
    }

    fun green(amount: Int = 1, numerator: Int = 1, denominator: Int = 4) {
        with(dropTable) {
            drop("item.green_charm", amount, numerator, denominator)
        }
    }

    fun crimson(amount: Int = 1, numerator: Int = 1, denominator: Int = 4) {
        with(dropTable) {
            drop("item.crimson_charm", amount, numerator, denominator)
        }
    }

    fun blue(amount: Int = 1, numerator: Int = 1, denominator: Int = 4) {
        with(dropTable) {
            drop("item.blue_charm", amount, numerator, denominator)
        }
    }
}
