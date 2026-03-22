package raynna.game.npc.worldboss

import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.Entity
import raynna.game.Hit
import raynna.game.WorldTile
import raynna.game.player.Player
import java.util.*

class WorldCorporealBeast(
    tile: WorldTile,
    idleTimeoutMs: Long,
    gracePeriodMs: Long,
    handler: RandomWorldBossHandler
) : WorldBossNPC(8133, tile, idleTimeoutMs, gracePeriodMs, handler) {

    private var core: WorldDarkEnergyCore? = null

    fun spawnDarkEnergyCore() {
        if (core != null) return
        core = WorldDarkEnergyCore(this)
    }

    fun removeDarkEnergyCore() {
        if (core == null) return
        core!!.finish()
        core = null
    }

    override fun handleHit(hit: Hit) {
        if (hit.source is Player) {
            val target = hit.source as Player
            if (!ItemDefinitions.getItemDefinitions(target.getEquipment().weaponId).getName()
                    .lowercase(Locale.getDefault())
                    .contains(" spear")
            ) {
                hit.damage /= 2
                target.packets.sendGameMessage("You cannot deal full damage without a spear weapon.")
            }
        }
        super.handleHit(hit)
    }

    override fun processNPC() {
        super.processNPC()
        if (isDead) return
        val maxhp = maxHitpoints
        if (maxhp > hitpoints && possibleTargets.isEmpty()) hitpoints = maxhp
    }

    override fun sendDeath(source: Entity?) {
        super.sendDeath(source)
        core?.sendDeath(source)
    }
}
