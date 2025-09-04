package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class MageBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2527, 4710, 0),
            WorldTile(2527, 4725, 0),
            WorldTile(2548, 4725, 0),
            WorldTile(2548, 4710, 0),
            WorldTile(2527, 4710, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Mage Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
