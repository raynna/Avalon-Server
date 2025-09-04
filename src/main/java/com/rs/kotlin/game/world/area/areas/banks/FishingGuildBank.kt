package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class FishingGuildBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2583, 3420, 0),
            WorldTile(2583, 3424, 0),
            WorldTile(2587, 3424, 0),
            WorldTile(2587, 3420, 0),
            WorldTile(2583, 3420, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Fishing Guild Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
