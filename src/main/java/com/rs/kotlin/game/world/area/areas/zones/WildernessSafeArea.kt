package com.rs.kotlin.game.world.area.areas.zones

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon
import com.rs.kotlin.game.world.area.shape.Rectangle

class WildernessSafeArea :
    Area(
        arrayOf(9033),
        // whole wilderness ditch
        Rectangle(
            WorldTile(2944, 3520, 0),
            WorldTile(3327, 3524, 0),
        ),
        // north of blackknight fortress
        Polygon(
            arrayOf(
                WorldTile(3032, 3525, 0),
                WorldTile(2997, 3525, 0),
                WorldTile(2997, 3533, 0),
                WorldTile(2999, 3533, 0),
                WorldTile(3001, 3534, 0),
                WorldTile(3007, 3543, 0),
                WorldTile(3017, 3543, 0),
                WorldTile(3021, 3543, 0),
                WorldTile(3025, 3535, 0),
                WorldTile(3032, 3525, 0),
            ),
        ),
        // edgeville dungeon safezones
        Rectangle(
            WorldTile(3079, 9915, 0),
            WorldTile(3095, 9930, 0),
        ),
        Rectangle(
            WorldTile(3130, 9918, 0),
            WorldTile(3133, 9923, 0),
        ),
    ) {
    override fun update(): Area = this

    override fun name(): String = "Wilderness Safe Areas"

    override fun member(): Boolean = false

    override fun environment(): Environment = Environment.WILDERNESS_SAFE
}
