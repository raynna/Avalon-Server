package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.player.Player

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

        player.message("[SwapDebug] swap() called. Target book=${book.name}")

        if (attrs[ATTR_ACTIVE] == true) {
            player.message("[SwapDebug] Swap already active.")
            player.message("You already have a spellbook swap active.")
            return false
        }

        val original = player.combatDefinitions.spellBook.toInt()

        player.message("[SwapDebug] Original book = $original")

        attrs[ATTR_ORIGINAL_BOOK] = original
        attrs[ATTR_ACTIVE] = true

        player.message("[SwapDebug] Attributes set: active=${attrs[ATTR_ACTIVE]} original=${attrs[ATTR_ORIGINAL_BOOK]}")

        player.combatDefinitions.setSpellBook(book.id)

        player.message("You temporarily switch your spellbook.")
        player.message("[SwapDebug] Spellbook switched to ${book.id}")

        return true
    }

    fun consumeSwap(player: Player) {
        val attrs = player.temporaryAttribute()

        player.message("[SwapDebug] consumeSwap() called")

        val active = attrs[ATTR_ACTIVE]

        player.message("[SwapDebug] active attribute = $active")

        if (active != true) {
            player.message("[SwapDebug] Swap not active. Returning.")
            return
        }

        val original = attrs[ATTR_ORIGINAL_BOOK] as? Int

        player.message("[SwapDebug] original attribute = $original")

        if (original == null) {
            player.message("[SwapDebug] original book missing. Returning.")
            return
        }

        player.combatDefinitions.setSpellBook(original)

        attrs.remove(ATTR_ACTIVE)
        attrs.remove(ATTR_ORIGINAL_BOOK)

        player.message("[SwapDebug] Spellbook restored to $original")
        player.message("Your spellbook returns to normal.")
    }
}
