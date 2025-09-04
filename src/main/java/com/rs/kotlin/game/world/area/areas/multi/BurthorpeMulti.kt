package com.rs.kotlin.game.world.area.areas.multi

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Rectangle

class BurthorpeMulti : Area(
    Rectangle(WorldTile(2880, 3520, 0), WorldTile(2903, 3543, 0))

) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Burthorpe Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}