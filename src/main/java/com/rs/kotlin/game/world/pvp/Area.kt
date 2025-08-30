package com.rs.kotlin.game.world.pvp

import com.rs.java.game.WorldTile
import kotlin.math.max
import kotlin.math.min

interface Area {
    fun contains(tile: WorldTile): Boolean
}

data class CircleArea(val cx: Int, val cy: Int, val plane: Int, val radius: Int): Area {
    override fun contains(tile: WorldTile): Boolean {
        if (tile.plane != plane) return false
        val dx = (tile.x - cx).toDouble()
        val dy = (tile.y - cy).toDouble()
        return (dx*dx + dy*dy) <= radius * radius + 0.0001
    }
}
data class RectArea(val x1: Int, val y1: Int, val x2: Int, val y2: Int, val plane: Int): Area {
    override fun contains(tile: WorldTile): Boolean {
        if (tile.plane != plane) return false
        val minX = min(x1, x2)
        val maxX = max(x1, x2)
        val minY = min(y1, y2)
        val maxY = max(y1, y2)
        return tile.x in minX..maxX && tile.y in minY..maxY
    }
}