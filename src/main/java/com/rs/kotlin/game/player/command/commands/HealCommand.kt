package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager
import com.rs.kotlin.game.player.command.Command
import kotlin.math.floor

class HealCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Restores your health, prayer & special attack"
    override val usage = "::heal"

    override fun execute(player: Player, args: List<String>): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::heal in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::heal here.")
            return true
        }
        if (player.tickManager.isActive(TickManager.Keys.LAST_ATTACKED_TICK)) {
            player.message("You can't use ::heal for another ${player.getTickToSeconds(player.tickManager.getTicksLeft(
                TickManager.Keys.LAST_ATTACKED_TICK))} seconds.")
            return true
        }
        player.prayer.restorePrayer(
            ((floor(player.skills.getLevelForXp(Skills.PRAYER) * 2.5) + 990).toInt() * player.auraManager.prayerPotsRestoreMultiplier).toInt()
        )
        if (player.poison.isPoisoned) player.poison.makePoisoned(0)
        player.newPoison.reset()
        player.setRunEnergy(100)
        player.heal(player.maxHitpoints)
        player.skills.restoreSkills()
        player.getCombatDefinitions().resetSpecialAttack()
        player.getAppearence().generateAppearenceData()
        val hitpointsModification = (player.maxHitpoints * 0.15).toInt()
        player.heal(hitpointsModification + 20, hitpointsModification)
        return true
    }
}
