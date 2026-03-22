package raynna.game.world.util

import raynna.util.Utils

sealed class RollableInt {
    abstract fun roll(): Int

    data class Fixed(val value: Int) : RollableInt() {
        override fun roll() = value
    }

    data class Range(val min: Int, val max: Int) : RollableInt() {
        override fun roll() = Utils.random(min, max)
    }
}
