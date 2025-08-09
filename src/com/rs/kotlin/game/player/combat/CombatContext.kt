package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.range.RangedAmmo

data class CombatContext(
    val attacker: Player,
    val defender: Entity,
    val weapon: Weapon,
    val ammo: RangedAmmo? = null,
    val combat: CombatStyle,
    val attackStyle: AttackStyle,
)
