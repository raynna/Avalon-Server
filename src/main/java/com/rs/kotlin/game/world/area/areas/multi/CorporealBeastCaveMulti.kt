package com.rs.kotlin.game.world.area.areas.multi

import com.rs.kotlin.game.world.area.Area

class CorporealBeastCaveMulti : Area(11844, 11588) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Corporeal Beast Cave Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}
