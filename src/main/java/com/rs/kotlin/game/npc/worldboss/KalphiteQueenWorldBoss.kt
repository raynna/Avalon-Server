package com.rs.kotlin.game.npc.worldboss

import com.rs.java.game.Animation
import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.WorldTile
import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager

class KalphiteQueenWorldBoss(
    tile: WorldTile,
    idleTimeoutMs: Long,
    gracePeriodMs: Long,
    handler: RandomWorldBossHandler,
) : WorldBossNPC(1158, tile, idleTimeoutMs, gracePeriodMs, handler) {

    private var isSecondForm = (id == 1160)

    override fun sendDeath(source: Entity?) {
        if (!isSecondForm && id == 1158) {
            // Phase 1 -> transform into Phase 2
            resetWalkSteps()
            combat?.removeTarget()
            animate(-1)

            WorldTasksManager.schedule(object : WorldTask() {
                var loop = 0
                override fun run() {
                    if (loop == 0) {
                        animate(Animation(combatDefinitions.deathAnim))
                    } else if (loop >= 2) {
                        setCantInteract(true)
                        transformIntoNPC(1160)
                        gfx(Graphics(1055))
                        animate(Animation(6270))
                        isSecondForm = true
                        WorldTasksManager.schedule(object : WorldTask() {
                            override fun run() {
                                reset()
                                setCantInteract(false)
                                setBonuses()
                            }
                        }, 5)
                        stop()
                    }
                    loop++
                }
            }, 0, 1)

        } else {
            resetWalkSteps()
            combat?.removeTarget()
            animate(-1)

            WorldTasksManager.schedule(object : WorldTask() {
                var loop = 0
                override fun run() {
                    if (loop == 0) {
                        animate(Animation(6233))
                    } else if (loop >= 2) {
                        try {
                            handler.onBossDeath(this@KalphiteQueenWorldBoss)
                        } finally {
                            super@KalphiteQueenWorldBoss.sendDeath(source)
                        }
                        stop()
                    }
                    loop++
                }
            }, 0, 1)
        }
    }
}
