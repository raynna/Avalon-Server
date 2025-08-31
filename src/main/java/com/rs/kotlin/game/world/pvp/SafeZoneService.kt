package com.rs.kotlin.game.world.pvp

import com.rs.java.game.area.Area
import com.rs.java.game.area.AreaManager
import com.rs.java.game.player.Player

object SafeZoneService {
    @JvmStatic
    fun isAtSafezone(player: Player): Boolean {
        val area = AreaManager.get(player.tile)
        return area != null && area.environment() == Area.Environment.SAFEZONE;
    }
}
