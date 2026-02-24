package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.java.game.player.content.collectionlog.CategoryType
import com.rs.kotlin.game.player.command.Command

class ResetCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Reset various player data (dev tool)"
    override val usage =
        "::reset collog [all|bosses|slayer|clues|minigames|others] [tab...]\n" +
            "::reset kc | ::reset bank | ::reset inv | ::reset equip | ::reset attrs"

    override fun execute(
        player: Player,
        args: List<String>,
        trigger: String,
    ): Boolean {
        if (args.isEmpty()) {
            player.message("Usage:\n$usage")
            return true
        }

        return when (args[0].lowercase()) {
            "clog", "collog", "collectionlog" -> {
                resetCollectionLog(player, args.drop(1))
            }

            "kc", "killcount", "killcounts" -> {
                resetKillcounts(player)
            }

            "bank" -> {
                resetBank(player)
            }

            "inv", "inventory" -> {
                resetInventory(player)
            }

            "equip", "equipment" -> {
                resetEquipment(player)
            }

            "attrs", "attributes", "tempattrs" -> {
                resetTempAttributes(player)
            }

            "all" -> {
                resetCollectionLog(player, emptyList())
                resetKillcounts(player)
                resetTempAttributes(player)
                player.message("Reset: all (collog + kc + temp attrs).")
                true
            }

            else -> {
                player.message("Unknown reset target: ${args[0]}")
                player.message("Usage:\n$usage")
                true
            }
        }
    }

    private fun resetCollectionLog(
        player: Player,
        args: List<String>,
    ): Boolean {
        val log = player.collectionLog

        if (args.isEmpty() || args[0].equals("all", true)) {
            log.resetAll()
            player.message("Collection log reset: ALL categories.")
            return true
        }

        val category = parseCategory(args[0])
        if (category == null) {
            player.message("Unknown category '${args[0]}'. Use: bosses, slayer, clues, minigames, others, all")
            return true
        }

        if (args.size == 1) {
            log.resetCategory(category)
            player.message("Collection log reset: category ${category.name.lowercase()}.")
            return true
        }

        val tabName = args.drop(1).joinToString(" ").trim()
        val ok = log.resetTab(category, tabName)
        if (ok) {
            player.message("Collection log reset: ${category.name.lowercase()} -> '$tabName'")
        } else {
            player.message("Collection log tab not found in ${category.name.lowercase()}: '$tabName'")
        }
        return true
    }

    private fun parseCategory(raw: String): CategoryType? =
        when (raw.lowercase()) {
            "boss", "bosses" -> CategoryType.BOSSES
            "slayer", "slayers" -> CategoryType.SLAYER
            "clue", "clues" -> CategoryType.CLUES
            "minigame", "minigames" -> CategoryType.MINIGAMES
            "other", "others" -> CategoryType.OTHERS
            else -> null
        }

    private fun resetKillcounts(player: Player): Boolean {
        try {
            player.killcount.clear()
            player.message("Killcounts reset.")
        } catch (e: Throwable) {
            player.message("Killcount reset not wired yet (no clear/reset method found).")
        }
        return true
    }

    private fun resetBank(player: Player): Boolean {
        try {
            player.bank.reset()
            player.message("Bank cleared.")
        } catch (e: Throwable) {
            player.message("Bank reset not wired yet (no clear/reset method found).")
        }
        return true
    }

    private fun resetInventory(player: Player): Boolean {
        try {
            player.inventory.reset() // or clear()
            player.message("Inventory cleared.")
        } catch (e: Throwable) {
            player.message("Inventory reset not wired yet (no reset/clear method found).")
        }
        return true
    }

    private fun resetEquipment(player: Player): Boolean {
        try {
            player.equipment.reset()
            player.appearance.generateAppearenceData()
            player.message("Equipment cleared.")
        } catch (e: Throwable) {
            player.message("Equipment reset not wired yet (no reset/clear method found).")
        }
        return true
    }

    private fun resetTempAttributes(player: Player): Boolean {
        try {
            player.temporaryAttribute().clear() // if it's a Map
            player.message("Temporary attributes cleared.")
        } catch (e: Throwable) {
            player.message("Temp attributes reset not wired yet (no clear method found).")
        }
        return true
    }
}
