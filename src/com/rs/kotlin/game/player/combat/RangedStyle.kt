package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager

object RangedStyle : CombatStyle {
    override fun canAttack(attacker: Player, target: Entity): Boolean {
        // TODO: Check for arrows or bolts
        return true
    }

    override fun getAttackDelay(attacker: Player): Int = 4
    override fun getAttackDistance(attacker: Player): Int = 7

    override fun applyHit(attacker: Player, target: Entity, hit: Hit) {
        target.applyHit(hit)
    }

    override fun attack(attacker: Player, target: Entity) {
        attacker.animate(426) // Basic ranged animation
    }

    override fun onHit(attacker: Player, target: Entity) {
        val projectileId = Rscm.lookup("graphic.curses_leech_magic_projectile");
        ProjectileManager.send(Projectile.ARROW, projectileId, attacker, target);
    }

    override fun onStop(attacker: Player?, target: Entity?, interrupted: Boolean) {
        // Optional: reset animation, handle re-equip logic
    }
}
