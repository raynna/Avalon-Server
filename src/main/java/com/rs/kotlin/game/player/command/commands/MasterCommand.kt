package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.java.game.player.Skills
import com.rs.kotlin.game.player.command.Command
import com.rs.kotlin.game.player.command.CommandArguments

class MasterCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Sets all combat stats to level 99"
    override val usage = "::master"

    override fun execute(player: Player, args: List<String>): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::master in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::master here.")
            return true
        }
        for (i in Skills.ATTACK..Skills.MAGIC) {
            player.skills[i] = 99
            player.skills.setXp(i, Skills.getXPForLevel(99).toDouble())
        }
        player.skills[Skills.SUMMONING] = 99
        player.skills.setXp(Skills.SUMMONING, Skills.getXPForLevel(99).toDouble())
        for (i in Skills.ATTACK..Skills.MAGIC)
            player.dialogueManager.startDialogue("LevelUp", i)
        player.dialogueManager.startDialogue("LevelUp", Skills.SUMMONING)
        player.skills.switchXPPopup(true)
        player.skills.switchXPPopup(true)
        player.appearence.generateAppearenceData()
        player.message("You set all your combat stats to level 99.")
        return true
    }
}
