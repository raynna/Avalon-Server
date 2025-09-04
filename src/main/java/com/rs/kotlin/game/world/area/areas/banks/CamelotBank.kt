package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class CamelotBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2724, 3487, 0),
            WorldTile(2724, 3490, 0),
            WorldTile(2721, 3490, 0),
            WorldTile(2721, 3494, 0),
            WorldTile(2719, 3494, 0),
            WorldTile(2719, 3496, 0),
            WorldTile(2721, 3496, 0),
            WorldTile(2721, 3497, 0),
            WorldTile(2730, 3497, 0),
            WorldTile(2730, 3490, 0),
            WorldTile(2727, 3490, 0),
            WorldTile(2727, 3487, 0),
            WorldTile(2724, 3487, 0) // closing point
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Camelot Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
