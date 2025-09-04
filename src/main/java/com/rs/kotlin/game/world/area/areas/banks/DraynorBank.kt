package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class DraynorBank : Area(
    Polygon(
        arrayOf(
            WorldTile(3088, 3240, 0),
            WorldTile(3088, 3246, 0),
            WorldTile(3097, 3246, 0),
            WorldTile(3097, 3240, 0),
            WorldTile(3088, 3240, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Draynor Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
