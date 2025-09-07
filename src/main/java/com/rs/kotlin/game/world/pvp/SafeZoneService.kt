package com.rs.kotlin.game.world.pvp

import com.rs.java.game.player.Player
import com.rs.kotlin.game.world.area.Area
import com.rs.kotlin.game.world.area.AreaManager

object SafeZoneService {
    @JvmStatic
    fun isAtSafezone(player: Player): Boolean {
        return AreaManager.isInEnvironment(player, Area.Environment.SAFEZONE)
    }
}
