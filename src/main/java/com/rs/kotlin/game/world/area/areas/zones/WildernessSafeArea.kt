package com.rs.kotlin.game.world.area.areas.zones

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Rectangle


class WildernessSafeArea : Area(arrayOf(9033),
    Rectangle(
        WorldTile(2944, 3520, 0),
        WorldTile(3327, 3524, 0)
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Wilderness Safe Areas"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.WILDERNESS_SAFE
    }
}