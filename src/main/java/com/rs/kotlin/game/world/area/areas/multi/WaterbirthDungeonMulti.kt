package com.rs.kotlin.game.world.area.areas.multi

import com.rs.kotlin.game.world.area.Area

class WaterbirthDungeonMulti : Area(9886, 10142, 7236, 7492, 7748, 11589) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Waterbirth Dungeon Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}
