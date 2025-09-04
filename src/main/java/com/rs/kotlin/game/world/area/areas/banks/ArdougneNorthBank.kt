package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class ArdougneNorthBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2612, 3330, 0),
            WorldTile(2612, 3335, 0),
            WorldTile(2621, 3335, 0),
            WorldTile(2621, 3330, 0),
            WorldTile(2612, 3330, 0) // closed polygon
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Ardougne North Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
