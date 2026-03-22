package raynna.game.world.area.areas.banks

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Polygon

class ArdougneSouthBank : Area(
    Polygon(
        arrayOf(
            WorldTile(2649, 3280, 0),
            WorldTile(2649, 3287, 0),
            WorldTile(2658, 3287, 0),
            WorldTile(2658, 3280, 0),
            WorldTile(2649, 3280, 0) // closed polygon
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Ardougne South Bank"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.SAFEZONE
    }
}
