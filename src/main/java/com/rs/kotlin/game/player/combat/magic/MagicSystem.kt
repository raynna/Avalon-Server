package com.rs.kotlin.game.player.combat.magic

import com.rs.kotlin.game.player.combat.magic.ancient.AncientMagicks
import com.rs.kotlin.game.player.combat.magic.modern.ModernMagicks

object MagicSystem {
    val runes = RuneDefinitions
    val spellbooks = listOf(AncientMagicks, ModernMagicks)
    val handler = SpellHandler
}
