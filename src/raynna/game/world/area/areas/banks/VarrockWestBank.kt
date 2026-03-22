package raynna.game.world.area.areas.banks

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Polygon

class VarrockWestBank : Area(
    Polygon(
        arrayOf(
            WorldTile(3179, 3432, 0), WorldTile(3179, 3446, 0),
            WorldTile(3194, 3446, 0), WorldTile(3194, 3432, 0)
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Varrock West Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
