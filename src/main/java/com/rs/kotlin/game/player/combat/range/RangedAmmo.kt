package com.rs.kotlin.game.player.combat.range

import com.rs.java.game.Graphics
import com.rs.kotlin.game.player.combat.special.SpecialEffect

data class RangedAmmo(
    val itemId: List<Int>,
    val name: String,
    val levelRequired: Int,
    val poisonSeverity: Int = -1,
    val startGfx: Graphics? = null,
    val doubleGfx: Graphics? = null,
    var endGfx: Graphics? = null,
    val projectileId: Int,
    val dropOnGround: Boolean = true,
    val specialEffect: SpecialEffect? = null,
    val ammoType: AmmoType = AmmoType.ARROW,
    val ammoTier: AmmoTier? = null,
)