package raynna.game.world.area.areas.multi

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Rectangle

class BurthorpeMulti : Area(
    Rectangle(WorldTile(2880, 3520, 0), WorldTile(2903, 3543, 0))

) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Burthorpe Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}