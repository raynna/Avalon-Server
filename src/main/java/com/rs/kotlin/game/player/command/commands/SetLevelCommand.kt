package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.java.game.player.Skills
import com.rs.kotlin.game.player.command.Command
import com.rs.kotlin.game.player.command.CommandArguments

class SetLevelCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Set a level for a skill"
    override val usage = "::setlevel <id> <level>"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::setlevel in this mode.")
            return true
        }
        if (!player.canUseCommand()) {
            player.message("You can't use ::setlevel here.")
            return true
        }
        if (player.getEquipment().wearingArmour()) {
            player.message("You can't wear any armour when using ::setlevel.")
            return true
        }
        val cmdArgs = CommandArguments(args)

        val skill = cmdArgs.getInt(0)
        var level = cmdArgs.getInt(1)
        if (skill < Skills.ATTACK || (skill > Skills.MAGIC && skill != Skills.SUMMONING)) {
            player.message("You are only able to change skills between 1-6 and 23.")
            return true
        }
        if (level > 99)
            level = 99
        if (level < 0)
            level = 0

        val previousLevel = player.skills.getLevelForXp(skill)
        player.skills[skill] = level
        player.skills.setXp(skill, Skills.getXPForLevel(level).toDouble())
        if (previousLevel < level) {
            player.dialogueManager.startDialogue("LevelUp", skill)
        }
        player.skills.switchXPPopup(true)
        player.skills.switchXPPopup(true)
        player.appearence.generateAppearenceData()
        player.message("You have set your ${(Skills.SKILL_NAME[skill])} to level $level.")
        return true
    }
}
