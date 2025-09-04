package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class OoglogBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2556, 2836, 0),
            WorldTile(2556, 2841, 0),
            WorldTile(2559, 2841, 0),
            WorldTile(2559, 2836, 0),
            WorldTile(2556, 2836, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Ooglog Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
