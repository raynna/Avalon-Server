package raynna.game.world.area.areas.banks

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Polygon

class EdgevilleBank : Area(
    Polygon(
        arrayOf(
            WorldTile(3098, 3499, 0), WorldTile(3098, 3488, 0),
            WorldTile(3091, 3488, 0), WorldTile(3091, 3499, 0)
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Edgeville Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
