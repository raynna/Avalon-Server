package raynna.game.world.area.areas.multi

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Rectangle

class BarbarianVillageMulti : Area(
    arrayOf(12341),  // North of falador party room
    Rectangle(WorldTile(3048, 3392, 0), WorldTile(3055, 3407, 0)),  //west of barbarian village
    Rectangle(WorldTile(3056, 3408, 0), WorldTile(3071, 3447, 0))
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Barbarian Village Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}