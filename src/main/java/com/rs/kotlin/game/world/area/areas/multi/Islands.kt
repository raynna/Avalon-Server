package com.rs.kotlin.game.world.area.areas.multi

import com.rs.kotlin.game.world.area.Area

class Islands : Area(9276, 9532, 8763, 8253, 8509, 8252, 8508) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Isles Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}
