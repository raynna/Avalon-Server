package com.rs.kotlin.game.world.area.areas.multi

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Rectangle

class WildernessMulti : Area(
    arrayOf(
        12599,
        12855,
        13111,
        12600,
        12856,
        13112,
        12857,
        13113,
        12858,
        13114,
        12859,
        13115,
        12860,
        13116,
        13372,
        12861,
        13117,
        13373,
        12088,
        12089
    ),  // North of moss giants
    Rectangle(WorldTile(3136, 3840, 0), WorldTile(3199, 3903, 0)),  // Small bit of north-east lava maze
    Rectangle(WorldTile(3112, 3873, 0), WorldTile(3135, 3903, 0)),  // North of lava maze
    Rectangle(WorldTile(3072, 3879, 0), WorldTile(3135, 3903, 0)),  // KBD lair
    Rectangle(WorldTile(3008, 3856, 0), WorldTile(3048, 3903, 0)),  // Wilderness agility
    Rectangle(WorldTile(2984, 3912, 0), WorldTile(3007, 3928, 0), "Wilderness Agility"),  // Chaos altar
    Rectangle(WorldTile(2946, 3816, 0), WorldTile(2959, 3830, 0))
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Wilderness Multi Areas"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}