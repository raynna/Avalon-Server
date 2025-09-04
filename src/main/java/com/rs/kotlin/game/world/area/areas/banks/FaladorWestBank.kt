package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class FaladorWestBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2943, 3368, 0),
            WorldTile(2943, 3373, 0),
            WorldTile(2947, 3373, 0),
            WorldTile(2947, 3369, 0),
            WorldTile(2949, 3369, 0),
            WorldTile(2949, 3368, 0),
            WorldTile(2943, 3368, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Falador West Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
