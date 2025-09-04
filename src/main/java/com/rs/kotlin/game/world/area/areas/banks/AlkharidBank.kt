package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.Shape
import com.rs.kotlin.game.world.area.shape.Polygon

class AlkharidBank : Area(
    Polygon(
        arrayOf(
            WorldTile(3265, 3161, 0),
            WorldTile(3265, 3173, 0),
            WorldTile(3272, 3173, 0),
            WorldTile(3272, 3161, 0)
        )
    )
) {
    override fun update(): Area = this

    override fun name(): String = "Alkharid Bank"

    override fun member(): Boolean = false

    override fun environment(): Environment = Environment.SAFEZONE
}
