package raynna.game.world.area.areas.banks

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Polygon

class OoglogBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2556, 2836, 0),
            WorldTile(2556, 2841, 0),
            WorldTile(2559, 2841, 0),
            WorldTile(2559, 2836, 0),
            WorldTile(2556, 2836, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Ooglog Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
