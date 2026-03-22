package raynna.game.player.combat.special

data class SpecialEffect(
    val chance: Int = 0,
    val interruptAttack: Boolean = false,
    val execute: (CombatContext) -> EffectResult = { EffectResult.CONTINUE },
)
