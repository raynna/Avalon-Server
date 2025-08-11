package com.rs.kotlin.game.player.combat.magic

object MagicSystem {
    val runes = RuneDefinitions
    val spellbooks = listOf(AncientMagicks, ModernMagicks)
    val handler = SpellHandler

}