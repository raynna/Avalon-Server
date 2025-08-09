package com.rs.kotlin.game.player.combat.range

import com.rs.kotlin.game.player.combat.SpecialAttack
import com.rs.kotlin.game.player.combat.SpecialEffect

data class RangedAmmo(
    val itemId: Int,
    val name: String,
    val levelRequired: Int,
    val damageBonus: Int,
    val startGfx: Int = -1,
    val projectileId: Int,
    val dropOnGround: Boolean = true,
    val specialEffect: SpecialEffect? = null,
    val ammoType: AmmoType = AmmoType.ARROW,
    val ammoTier: AmmoTier? = null,
)