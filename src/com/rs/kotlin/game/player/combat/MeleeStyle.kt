package com.rs.kotlin.game.player.combat

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.player.Player

object MeleeStyle : CombatStyle {
    override fun canAttack(attacker: Player, target: Entity): Boolean {
        return true
    }

    override fun getAttackDistance(attacker: Player): Int = 0

    override fun getAttackDelay(attacker: Player): Int = 3

    override fun applyHit(attacker: Player, target: Entity, hit: Hit) {
        val damage = 10
        target.applyHit(hit);
    }

    override fun attack(attacker: Player, target: Entity) {
        attacker.animate(422)
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                val hit = Hit(target, 10, HitLook.MELEE_DAMAGE)
                applyHit(attacker, target, hit)
                onHit(attacker, target)
            }
        }, 0)
    }

    override fun onHit(attacker: Player, target: Entity) {
        //target.playGraphic(Graphic.BLOOD_SPLASH)
    }

    override fun onStop(attacker: Player?, target: Entity?, interrupted: Boolean) {
        // Maybe clear facing or animation
    }
}
