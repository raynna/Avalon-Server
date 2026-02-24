package com.rs.kotlin.game.player.command.commands

import com.rs.core.cache.defintions.AnimationDefinitions
import com.rs.java.game.Animation
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command
import java.util.Arrays

class AnimationCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Executes an animation"
    override val usage = "::animation <id>"

    override fun execute(
        player: Player,
        args: List<String>,
        trigger: String,
    ): Boolean {
        if (args.isEmpty()) {
            player.message("Usage: $usage")
            return true
        }
        val animationId = args[0].toInt()
        val definitions = AnimationDefinitions.getAnimationDefinitions(animationId)
        if (animationId == -1) { // just to be able to reset animation
            player.animate(Animation(-1))
            player.message("Reset current animation.")
            return true
        }
        if (definitions == null || definitions.anIntArray2153 == null || definitions.anIntArray2153.isEmpty()) {
            player.message("Unknown or invalid animation ID: $animationId")
            println("Unknown animation ID: $animationId")
            return true
        }
        if (definitions.handledSounds != null) {
            println("sounds ${definitions.handledSounds.contentDeepToString()}")
            // player.message("sounds: " + definitions.handledSounds.contentDeepToString())
        }
        player.animate(animationId)
        println("Animation ID: $animationId")
        player.message("Animation $animationId has been executed.")
        return true
    }
}
