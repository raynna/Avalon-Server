package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.core.thread.CoresManager
import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.World
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager
import com.rs.java.game.player.actions.combat.Combat
import java.util.concurrent.TimeUnit

object HealGroupService {
    fun cast(player: Player): Boolean {
        if (player.tickManager.isActive(TickManager.TickKeys.HEAL_GROUP_COOLDOWN)) {
            player.message("You can only cast this every 20 seconds.")
            return false
        }

        if (!player.isAtMultiArea) {
            player.message("You need to be in a multi area for this spell.")
            return false
        }

        var affected = 0

        for (other in World.getPlayers()) {
            if (other == null || other === player) {
                continue
            }

            if (other.withinDistance(player, 4) &&
                other.isAcceptAid &&
                other.isAtMultiArea
            ) {
                affected++

                other.message("Your health has been healed.")
                other.gfx(Graphics(745, 0, 100))
            }
        }

        if (affected == 0) {
            player.message("There is nobody nearby to heal.")
            return false
        }

        val healAmount = (player.hitpoints * 0.75 / affected).toInt()

        for (other in World.getPlayers()) {
            if (other == null || other === player) {
                continue
            }

            if (other.withinDistance(player, 4) &&
                other.isAcceptAid &&
                other.isAtMultiArea
            ) {
                other.heal(healAmount)
            }
        }

        player.gfx(Graphics(745, 0, 100))
        player.animate(Animation(4411))

        player.message("The spell affected $affected player(s).")

        player.tickManager.addSeconds(
            TickManager.TickKeys.HEAL_GROUP_COOLDOWN,
            20,
        )

        // Damage caster after short delay
        CoresManager.getSlowExecutor().schedule({
            val damage = (player.hitpoints * 0.75).toInt()

            player.applyHit(Hit(player, damage, HitLook.REGULAR_DAMAGE))
            player.animate(Animation(Combat.getDefenceEmote(player)))
        }, 1200, TimeUnit.MILLISECONDS)

        return true
    }
}
