package com.rs.kotlin.game.player.command.commands

import com.rs.core.cache.defintions.AnimationDefinitions
import com.rs.java.game.Animation
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class AnimationCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Executes an animation"
    override val usage = "::animation <id>"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (args.isEmpty()) {
            player.message("Usage: $usage")
            return true
        }
        val animationId = args[0].toInt()
        val definitions = AnimationDefinitions.getAnimationDefinitions(animationId)
        if (definitions == null || definitions.anIntArray2153 == null || definitions.anIntArray2153.isEmpty()) {
            player.message("Unknown or invalid animation ID: $animationId")
            return false
        }
        player.animate(Animation(animationId))
        player.message("Animation $animationId has been executed.")
        return true
    }
}
