package raynna.game.world.area.areas.banks

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Polygon

class DuelArenaBank : Area(
    Polygon(
        arrayOf(
            WorldTile(3380, 3267, 0),
            WorldTile(3380, 3273, 0),
            WorldTile(3385, 3273, 0),
            WorldTile(3385, 3267, 0)
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Duel arena Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
