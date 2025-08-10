package com.rs.kotlin.game.player.combat.special

import com.rs.java.game.Entity
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.AttackStyle
import com.rs.kotlin.game.player.combat.CombatStyle
import com.rs.kotlin.game.player.combat.Weapon
import com.rs.kotlin.game.player.combat.range.RangedAmmo

data class SpecialContext(
    val attacker: Player,
    val defender: Entity,
    val weapon: Weapon,
    val ammo: RangedAmmo? = null,
    val combat: CombatStyle,
    val attackStyle: AttackStyle,
)
