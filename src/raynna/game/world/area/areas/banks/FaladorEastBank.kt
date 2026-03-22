package raynna.game.world.area.areas.banks

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Polygon

class FaladorEastBank : Area(
    Polygon(
        arrayOf(
            WorldTile(3009, 3353, 0),
            WorldTile(3009, 3358, 0),
            WorldTile(3018, 3358, 0),
            WorldTile(3018, 3356, 0),
            WorldTile(3021, 3356, 0),
            WorldTile(3021, 3353, 0),
            WorldTile(3009, 3353, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Falador East Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
