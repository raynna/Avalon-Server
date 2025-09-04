package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class PortPhasmatysBank : Area(
    Polygon(
        arrayOf(
            WorldTile(3686, 3461, 0),
            WorldTile(3686, 3471, 0),
            WorldTile(3699, 3471, 0),
            WorldTile(3699, 3461, 0),
            WorldTile(3686, 3461, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Port Phasmatys Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
