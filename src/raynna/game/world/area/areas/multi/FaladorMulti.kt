package raynna.game.world.area.areas.multi

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Polygon
import raynna.game.world.area.shape.Rectangle

class FaladorMulti : Area( // Regions
    arrayOf(11829, 11828, 11827),  // West of farm
    Rectangle(WorldTile(3008, 3304, 0), WorldTile(3015, 3327, 0)),  // Pigpen
    Rectangle(WorldTile(3014, 3303, 0), WorldTile(3020, 3311, 0)),  //Cow pen
    Polygon(
        arrayOf(
            WorldTile(3014, 3303, 0),
            WorldTile(3021, 3312, 0),
            WorldTile(3022, 3313, 0),
            WorldTile(3042, 3313, 0),
            WorldTile(3043, 3312, 0),
            WorldTile(3043, 3299, 0),
            WorldTile(3041, 3297, 0),
            WorldTile(3014, 3303, 0),
        )
    )
) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "Falador Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}