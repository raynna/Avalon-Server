package com.rs.kotlin.game.world.area.areas.multi

import com.rs.kotlin.game.world.area.Area

class GodwarsMulti : Area(11345, 11346, 11347, 11601, 11602, 11603) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Godwars Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}
