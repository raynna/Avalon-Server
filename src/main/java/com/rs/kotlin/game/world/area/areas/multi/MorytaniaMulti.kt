package com.rs.kotlin.game.world.area.areas.multi

import com.rs.kotlin.game.world.area.Area

class MorytaniaMulti : Area(13876) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Morytania Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}
