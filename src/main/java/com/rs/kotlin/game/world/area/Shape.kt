package com.rs.kotlin.game.world.area

import com.rs.java.game.WorldTile

abstract class Shape {

    private var areas: Array<WorldTile>? = null
    private var type: ShapeType? = null

    abstract fun inside(location: WorldTile): Boolean

    fun areas(): Array<WorldTile>? = areas
    fun areas(areas: Array<WorldTile>): Shape {
        this.areas = areas
        return this
    }

    fun type(): ShapeType? = type
    fun type(type: ShapeType): Shape {
        this.type = type
        return this
    }

    enum class ShapeType {
        RECTANGLE, POLYGON
    }
}
