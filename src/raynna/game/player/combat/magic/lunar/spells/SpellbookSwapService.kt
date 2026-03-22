package raynna.game.player.combat.magic.lunar.spells

import raynna.game.player.Player

object SpellbookSwapService {
    private const val ATTR_ORIGINAL_BOOK = "spellbook_swap_original"
    private const val ATTR_ACTIVE = "spellbook_swap_active"

    enum class Spellbook(
        val id: Int,
    ) {
        MODERN(0),
        ANCIENT(1),
    }

    fun swap(
        player: Player,
        book: Spellbook,
    ): Boolean {
        val attrs = player.temporaryAttribute()

        if (attrs[ATTR_ACTIVE] == true) {
            player.message("You already have a spellbook swap active.")
            return false
        }

        val original = player.combatDefinitions.spellBook.toInt()

        attrs[ATTR_ORIGINAL_BOOK] = original
        attrs[ATTR_ACTIVE] = true

        player.combatDefinitions.setSpellBook(book.id)

        player.message("You temporarily switch your spellbook.")

        return true
    }

    fun consumeSwap(player: Player) {
        val attrs = player.temporaryAttribute()

        val active = attrs[ATTR_ACTIVE]

        if (active != true) {
            return
        }

        val original = attrs[ATTR_ORIGINAL_BOOK] as? Int ?: return

        player.combatDefinitions.setSpellBook(original)

        attrs.remove(ATTR_ACTIVE)
        attrs.remove(ATTR_ORIGINAL_BOOK)

        player.message("Your spellbook returns to normal.")
    }
}
