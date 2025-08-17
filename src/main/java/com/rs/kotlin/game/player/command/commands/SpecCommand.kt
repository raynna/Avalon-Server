package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager
import com.rs.kotlin.game.player.command.Command
import kotlin.math.floor

class SpecCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Restores your special attack"
    override val usage = "::spec"

    override fun execute(player: Player, args: List<String>): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::spec in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::spec here.")
            return true
        }
        if (player.tickManager.isActive(TickManager.Keys.LAST_ATTACKED_TICK)) {
            player.message("You can't use ::spec for another ${player.getTickToSeconds(player.tickManager.getTicksLeft(TickManager.Keys.LAST_ATTACKED_TICK))} seconds.")
            return true
        }
        player.getCombatDefinitions().resetSpecialAttack()
        player.message("Your special attack has been restored.")
        return true
    }
}
