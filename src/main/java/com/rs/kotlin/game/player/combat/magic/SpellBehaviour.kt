package com.rs.kotlin.game.player.combat.magic

import com.rs.java.game.player.Player

fun interface SpellBehaviour {
    fun cast(
        player: Player,
        spell: Spell,
    ): Boolean
}
