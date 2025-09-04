package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class CanifisBank : Area(
    Polygon(
        arrayOf(
            WorldTile(3509, 3474, 0),
            WorldTile(3509, 3478, 0),
            WorldTile(3508, 3479, 0),
            WorldTile(3508, 3482, 0),
            WorldTile(3509, 3483, 0),
            WorldTile(3516, 3483, 0),
            WorldTile(3516, 3478, 0),
            WorldTile(3512, 3474, 0),
            WorldTile(3509, 3474, 0) // closing polygon
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Canifis Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
