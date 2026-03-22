package raynna.game.world.pvp

import raynna.game.player.Player
import raynna.game.world.area.Area
import raynna.game.world.area.AreaManager

object SafeZoneService {
    @JvmStatic
    fun isAtSafezone(player: Player): Boolean {
        return AreaManager.isInEnvironment(player, Area.Environment.SAFEZONE)
    }
}
