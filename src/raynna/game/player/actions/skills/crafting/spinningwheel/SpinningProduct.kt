package raynna.game.player.actions.skills.crafting.spinningwheel

import raynna.game.player.actions.skills.crafting.ReqItem
import raynna.data.rscm.Rscm

class SpinningProduct(
    private val idRef: Any,
    val level: Int,
    val xp: Double,
    vararg val requirements: ReqItem,
) {
    fun getId(): Int = idRef as? Int ?: Rscm.lookup(idRef as String)
}
