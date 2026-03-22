package raynna.game.world.area.areas.banks

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Polygon

class VarrockEastBank : Area(
    Polygon(
        arrayOf(
            WorldTile(3250, 3416, 0), WorldTile(3250, 3423, 0),
            WorldTile(3257, 3423, 0), WorldTile(3257, 3416, 0)
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Varrock East Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
