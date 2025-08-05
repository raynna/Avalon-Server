package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.player.Player

interface CombatStyle {
    fun canAttack(attacker: Player, target: Entity): Boolean
    fun getAttackDelay(attacker: Player): Int
    fun getAttackDistance(attacker: Player): Int
    fun applyHit(attacker: Player, target: Entity, hit: Hit)
    fun attack(attacker: Player, target: Entity)
    fun onHit(attacker: Player, target: Entity)
    fun onStop(attacker: Player?, target: Entity?, interrupted: Boolean)
}
