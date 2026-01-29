package com.rs.kotlin.game.world.activity.pvpgame.tournament

import com.rs.java.game.player.content.presets.Preset

data class TournamentPreset(
    val preset: Preset,
    val rules: TournamentRules
)
