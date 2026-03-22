package raynna.game.player.combat.magic

import raynna.game.player.combat.magic.ancient.AncientMagicks
import raynna.game.player.combat.magic.modern.ModernMagicks

object MagicSystem {
    val runes = RuneDefinitions
    val spellbooks = listOf(AncientMagicks, ModernMagicks)
    val handler = SpellHandler
}
