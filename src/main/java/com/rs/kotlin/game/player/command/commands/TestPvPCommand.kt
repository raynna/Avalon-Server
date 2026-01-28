package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command
import com.rs.kotlin.game.player.command.CommandArguments
import com.rs.kotlin.game.world.activity.pvpgame.*
import com.rs.kotlin.game.world.activity.pvpgame.tournament.TournamentScheduler

class TestPvPCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Test PvP games (Tournament or LMS)"
    override val usage = "::pvptest <tournament|lms|end> [opponentName]"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        // Debug: print raw args
        player.message("DEBUG: trigger=$trigger args=${args.joinToString(",")}")

        val cmdArgs = CommandArguments(args)
        player.message("DEBUG: cmdArgs.first=${cmdArgs.first()} cmdArgs.last=${cmdArgs.last()}")

        val sub = cmdArgs.getString(0).lowercase()
        player.message("DEBUG: subcommand='$sub'")

        if (sub.isEmpty()) {
            player.message("Usage: $usage")
            return true
        }

        when (sub) {
            "create" -> {
                TournamentScheduler.startNewTournament()
                player.message("Created a tournament.")
            }

            "join" -> {
                val instance = TournamentScheduler.getInstance()
                if (instance == null) {
                    player.message("No active tournament right now.")
                } else {
                    instance.addPlayer(player) // ✅ teleports + adds to lobby
                    player.message("Joined current tournament lobby.")
                }
            }

            "end" -> {
                val field = PvPGameManager::class.java.getDeclaredField("activeGames")
                field.isAccessible = true
                val active = ArrayList(field.get(PvPGameManager) as MutableList<PvPGame>)
                player.message("DEBUG: Found ${active.size} active games to clean up.")
                active.forEach { it.cleanup(null) }
                TournamentScheduler.endTournament()
                player.message("All active PvP games and tournament cleaned up.")
            }

            else -> {
                player.message("Invalid game type. Use ::pvp <create|join|end>")
            }
        }


        return true
    }
}
