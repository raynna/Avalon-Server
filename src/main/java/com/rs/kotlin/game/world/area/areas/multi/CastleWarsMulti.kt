package com.rs.kotlin.game.world.area.areas.multi

import com.rs.kotlin.game.world.area.Area

class CastleWarsMulti : Area(9520) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Castle Wars Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}