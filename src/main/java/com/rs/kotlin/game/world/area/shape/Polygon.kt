package com.rs.kotlin.game.world.area.shape

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Shape

class Polygon(pointsTiles: Array<WorldTile>) : Shape() {

    private var sides: Int = 0
    private var points: Array<IntArray>

    init {
        sides(pointsTiles.size).areas(pointsTiles).type(ShapeType.POLYGON)

        points = Array(sides) { IntArray(2) }
        for (i in 0 until sides) {
            points[i][0] = pointsTiles[i].x
            points[i][1] = pointsTiles[i].y
        }
    }

    override fun inside(location: WorldTile): Boolean {
        val x = location.x
        val y = location.y

        // Edge check
        for (i in 0 until sides) {
            val j = if (i == 0) sides - 1 else i - 1
            val xi = points[i][0]; val yi = points[i][1]
            val xj = points[j][0]; val yj = points[j][1]

            if (y in minOf(yi, yj)..maxOf(yi, yj) && yj != yi) {
                val xOnEdge = xi + (xj - xi).toDouble() * (y - yi) / (yj - yi)
                if (kotlin.math.abs(x - xOnEdge) < 1e-9) return true
            }
            if (x in minOf(xi, xj)..maxOf(xi, xj) && xj != xi) {
                val yOnEdge = yi + (yj - yi).toDouble() * (x - xi) / (xj - xi)
                if (kotlin.math.abs(y - yOnEdge) < 1e-9) return true
            }
        }

        // Ray casting
        var inside = false
        for (i in 0 until sides) {
            val j = if (i == 0) sides - 1 else i - 1
            val xi = points[i][0]; val yi = points[i][1]
            val xj = points[j][0]; val yj = points[j][1]

            val intersect = (yi > y) != (yj > y) &&
                    (x <= (xj - xi).toDouble() * (y - yi) / (yj - yi) + xi)
            if (intersect) inside = !inside
        }
        return inside
    }

    fun sides(): Int = sides
    fun sides(sides: Int): Shape {
        this.sides = sides
        return this
    }

    fun points(): Array<IntArray> = points
    fun points(points: Array<IntArray>): Shape {
        this.points = points
        return this
    }
}
