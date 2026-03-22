package raynna.game.world.activity.pvpgame.tournament

import raynna.game.player.content.presets.Preset

data class TournamentPreset(
    val preset: Preset,
    val rules: TournamentRules
)
