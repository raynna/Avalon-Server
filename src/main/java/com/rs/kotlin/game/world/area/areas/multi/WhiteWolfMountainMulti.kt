package com.rs.kotlin.game.world.area.areas.multi

import com.rs.kotlin.game.world.area.Area

class WhiteWolfMountainMulti : Area(11318) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "White Wolf Mountain Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}
