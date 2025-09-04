package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class EdgevilleBank : Area(
    Polygon(
        arrayOf(
            WorldTile(3098, 3499, 0), WorldTile(3098, 3488, 0),
            WorldTile(3091, 3488, 0), WorldTile(3091, 3499, 0)
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Edgeville Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
