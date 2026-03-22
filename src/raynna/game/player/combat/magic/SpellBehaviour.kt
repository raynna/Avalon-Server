package raynna.game.player.combat.magic

import raynna.game.player.Player

fun interface SpellBehaviour {
    fun cast(
        player: Player,
        spell: Spell,
    ): Boolean
}
