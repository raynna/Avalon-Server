package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class NardahBank : Area(
    Polygon(
        arrayOf(
            WorldTile(3427, 2889, 0),
            WorldTile(3427, 2894, 0),
            WorldTile(3430, 2894, 0),
            WorldTile(3430, 2889, 0),
            WorldTile(3427, 2889, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Nardah Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
