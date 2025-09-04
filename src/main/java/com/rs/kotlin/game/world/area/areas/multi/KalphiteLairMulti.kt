package com.rs.kotlin.game.world.area.areas.multi

import com.rs.kotlin.game.world.area.Area

class KalphiteLairMulti : Area(13972) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Kalphite Lair Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}
