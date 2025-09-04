package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class CatherbyBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2806, 3438, 0),
            WorldTile(2806, 3441, 0),
            WorldTile(2812, 3441, 0),
            WorldTile(2812, 3438, 0),
            WorldTile(2806, 3438, 0) // closing point
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Catherby Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
