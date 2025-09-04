package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class LumbridgeCastle : Area(
    Polygon(
        arrayOf(
            WorldTile(3229, 3217, 0),  //done
            WorldTile(3230, 3216, 0),  //done
            WorldTile(3230, 3212, 0),  //done
            WorldTile(3226, 3212, 0),
            WorldTile(3226, 3208, 0),
            WorldTile(3221, 3203, 0),
            WorldTile(3214, 3203, 0),
            WorldTile(3212, 3201, 0),
            WorldTile(3204, 3201, 0),
            WorldTile(3201, 3204, 0),
            WorldTile(3201, 3216, 0),
            WorldTile(3197, 3216, 0),
            WorldTile(3197, 3221, 0),
            WorldTile(3201, 3221, 0),
            WorldTile(3201, 3233, 0),
            WorldTile(3204, 3236, 0),
            WorldTile(3212, 3236, 0),
            WorldTile(3214, 3234, 0),
            WorldTile(3220, 3234, 0),
            WorldTile(3226, 3228, 0),
            WorldTile(3226, 3225, 0),
            WorldTile(3230, 3225, 0),
            WorldTile(3230, 3221, 0),
            WorldTile(3229, 3220, 0),
            WorldTile(3229, 3217, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Lumbridge Castle & Yard"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
