package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.player.Player

data class CombatContext(
    val attacker: Player,
    val defender: Entity,
    val weapon: Weapon,
    val combat: CombatStyle,
    val attackStyle: AttackStyle,
)
