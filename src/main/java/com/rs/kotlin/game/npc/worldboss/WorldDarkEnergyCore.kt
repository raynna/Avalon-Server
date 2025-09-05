package com.rs.kotlin.game.npc.worldboss

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.World
import com.rs.java.game.WorldTile
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.utils.Utils

class WorldDarkEnergyCore(private val beast: WorldCorporealBeast) :
    NPC(8127, beast, -1, true, true) {

    private var target: Entity? = null
    private var changeTarget = 2
    private var delay = 0

    init {
        isForceMultiArea = true
    }

    override fun processNPC() {
        if (isDead || hasFinished()) return

        if (delay > 0) {
            delay--
            return
        }

        if (changeTarget > 0) {
            if (changeTarget == 1) {
                val possibleTargets = beast.possibleTargets
                if (possibleTargets.isEmpty()) {
                    finish()
                    beast.removeDarkEnergyCore()
                    return
                }
                target = possibleTargets.random()
                World.sendElementalProjectile(this, target, 1828)
                WorldTasksManager.schedule(object : WorldTask() {
                    override fun run() {
                        setNextWorldTile(WorldTile(target))
                    }
                }, 1)
            }
            changeTarget--
            return
        }

        if (target == null || !target!!.withinDistance(beast, 1) || target!!.plane != plane) {
            changeTarget = 5
            return
        }

        val damage = Utils.random(50) + 50
        target!!.applyHit(Hit(this, Utils.random(1, 131), HitLook.REGULAR_DAMAGE))
        beast.applyHit(Hit(this, damage, HitLook.HEALED_DAMAGE))
        delay = if (poison.isPoisoned) 20 else 2

        if (target is Player) {
            (target as Player).packets.sendGameMessage(
                "The dark core creature steals some life from you for its master."
            )
        }
    }

    override fun sendDeath(source: Entity?) {
        super.sendDeath(source)
        beast.removeDarkEnergyCore()
    }
}
