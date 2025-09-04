package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class YanilleBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2609, 3088, 0),
            WorldTile(2609, 3097, 0),
            WorldTile(2616, 3097, 0),
            WorldTile(2616, 3088, 0),
            WorldTile(2609, 3088, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Yanille Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
