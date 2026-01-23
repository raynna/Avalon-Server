package com.rs.kotlin.game.world.util

import com.rs.java.utils.Utils

sealed class RollableInt {
    abstract fun roll(): Int

    data class Fixed(val value: Int) : RollableInt() {
        override fun roll() = value
    }

    data class Range(val min: Int, val max: Int) : RollableInt() {
        override fun roll() = Utils.random(min, max)
    }
}
