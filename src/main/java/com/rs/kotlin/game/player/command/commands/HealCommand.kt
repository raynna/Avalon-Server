package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager
import com.rs.kotlin.game.player.command.Command

class HealCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Restores your health, prayer & special attack"
    override val usage = "::heal"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::heal in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::heal here.")
            return true
        }
        if (player.tickManager.isActive(TickManager.TickKeys.LAST_ATTACKED_TICK)) {
            player.message("You can't use ::heal for another ${player.getTickToSeconds(player.tickManager.getTicksLeft(
                TickManager.TickKeys.LAST_ATTACKED_TICK))} seconds.")
            return true
        }
        player.prayer.restorePrayer(player.skills.getLevelForXp(Skills.PRAYER) * 10)
        if (player.poison.isPoisoned) player.poison.reset()
        if (player.newPoison.isPoisoned()) player.newPoison.reset()
        player.setRunEnergy(100)
        player.heal(player.maxHitpoints)
        player.skills.restoreSkills()
        player.getAppearance().generateAppearenceData()
        player.skills[Skills.SUMMONING] = player.skills.getLevelForXp(Skills.SUMMONING)
        player.skills.refresh(Skills.SUMMONING)
        player.getCombatDefinitions().resetSpecialAttack()
        player.animate(Animation(8502))
        player.gfx(Graphics(1308))
        return true
    }
}
