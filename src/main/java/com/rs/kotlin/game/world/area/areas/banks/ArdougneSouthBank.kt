package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class ArdougneSouthBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2649, 3280, 0),
            WorldTile(2649, 3287, 0),
            WorldTile(2658, 3287, 0),
            WorldTile(2658, 3280, 0),
            WorldTile(2649, 3280, 0) // closed polygon
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Ardougne South Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
