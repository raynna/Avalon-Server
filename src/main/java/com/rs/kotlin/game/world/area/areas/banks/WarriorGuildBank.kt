package com.rs.kotlin.game.world.area.areas.banks

import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.shape.Polygon

class WarriorGuildBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2843, 3533, 0),
            WorldTile(2843, 3537, 0),
            WorldTile(2841, 3537, 0),
            WorldTile(2841, 3540, 0),
            WorldTile(2848, 3540, 0),
            WorldTile(2848, 3533, 0),
            WorldTile(2843, 3533, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Warrior Guild Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
