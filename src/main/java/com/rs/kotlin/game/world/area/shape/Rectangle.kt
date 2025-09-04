package com.rs.kotlin.game.world.area.shape

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Shape

class Rectangle(
    private val sw: WorldTile,
    private val ne: WorldTile,
    private val debugLabel: String? = null
) : Shape() {

    init {
        areas(arrayOf(ne, sw)).type(ShapeType.RECTANGLE)
    }

    override fun inside(location: WorldTile): Boolean {
        val inPlane = location.plane == sw.plane && location.plane == ne.plane
        val inX = location.x in sw.x..ne.x
        val inY = location.y in sw.y..ne.y
        return inPlane && inX && inY
    }

    fun getSouthWest(): WorldTile = sw
    fun getNorthEast(): WorldTile = ne
}
