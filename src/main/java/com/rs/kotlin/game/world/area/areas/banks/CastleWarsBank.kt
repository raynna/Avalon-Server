package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class CastleWarsBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2440, 3082, 0),
            WorldTile(2438, 3084, 0),
            WorldTile(2439, 3085, 0),
            WorldTile(2439, 3087, 0),
            WorldTile(2438, 3088, 0),
            WorldTile(2438, 3091, 0),
            WorldTile(2439, 3092, 0),
            WorldTile(2439, 3094, 0),
            WorldTile(2438, 3095, 0),
            WorldTile(2440, 3097, 0),
            WorldTile(2444, 3097, 0),
            WorldTile(2445, 3098, 0),
            WorldTile(2447, 3098, 0),
            WorldTile(2447, 3095, 0),
            WorldTile(2445, 3095, 0),
            WorldTile(2445, 3092, 0),
            WorldTile(2446, 3091, 0),
            WorldTile(2446, 3088, 0),
            WorldTile(2445, 3087, 0),
            WorldTile(2445, 3082, 0),
            WorldTile(2440, 3082, 0)
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Castle Wars Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
