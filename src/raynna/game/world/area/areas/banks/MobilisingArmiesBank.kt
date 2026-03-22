package raynna.game.world.area.areas.banks

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Polygon
class MobilisingArmiesBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2440, 2840, 0),
            WorldTile(2440, 2843, 0),
            WorldTile(2405, 2843, 0),
            WorldTile(2405, 2840, 0),
            WorldTile(2440, 2840, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Mobilising Armies Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
