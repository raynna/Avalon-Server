package com.rs.kotlin.game.world.area.areas.multi

import com.rs.kotlin.game.world.area.Area

class PiscatorisColonyMulti : Area(9273) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Piscatoris Colony Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}
