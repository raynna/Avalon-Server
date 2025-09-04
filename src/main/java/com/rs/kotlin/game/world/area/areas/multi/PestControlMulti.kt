package com.rs.kotlin.game.world.area.areas.multi

import com.rs.kotlin.game.world.area.Area

class PestControlMulti : Area(10536) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Pest Control Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}
