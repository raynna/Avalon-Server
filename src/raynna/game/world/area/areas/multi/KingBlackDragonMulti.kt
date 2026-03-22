package raynna.game.world.area.areas.multi

import raynna.game.world.area.Area

class KingBlackDragonMulti : Area(9033) {
    override fun update(): Area {
        return this
    }

    override fun name(): String {
        return "King Black Dragon Multi"
    }

    override fun member(): Boolean {
        return false
    }

    override fun environment(): Environment {
        return Environment.MULTI
    }
}
