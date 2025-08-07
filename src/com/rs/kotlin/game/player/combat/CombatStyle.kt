package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.player.Player

interface CombatStyle {
    fun canAttack(attacker: Player, defender: Entity): Boolean
    fun getAttackDelay(attacker: Player): Int
    fun getAttackDistance(attacker: Player): Int
    fun applyHit(attacker: Player, defender: Entity, hit: Hit)
    fun attack(attacker: Player, defender: Entity)
    fun onHit(attacker: Player, defender: Entity)
    fun onStop(attacker: Player?, defender: Entity?, interrupted: Boolean)
}
