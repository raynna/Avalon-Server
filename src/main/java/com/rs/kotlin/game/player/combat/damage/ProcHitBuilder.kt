package com.rs.kotlin.game.player.combat.damage

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.kotlin.game.player.combat.special.CombatContext

class ProcHitBuilder(
    private val context: CombatContext,
    private val target: Entity
) {
    private var delayTicks: Int = 0
    private var damage: Int = 0
    private var look: Hit.HitLook = Hit.HitLook.REGULAR_DAMAGE
    private var source: Entity = context.attacker

    fun delay(ticks: Int) = apply { delayTicks = ticks }
    fun damage(amount: Int) = apply { damage = amount }
    fun look(hitLook: Hit.HitLook) = apply { look = hitLook }
    fun source(entity: Entity) = apply { source = entity }

    fun apply(): Hit {
        val hit = Hit(source, damage, look)
        context.combat.delayHits(PendingHit(hit, target, delayTicks))
        return hit
    }
}
