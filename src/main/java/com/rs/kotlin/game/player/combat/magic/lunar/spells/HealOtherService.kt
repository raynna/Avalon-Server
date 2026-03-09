package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.Animation
import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils

object HealOtherService {
    fun cast(
        player: Player,
        target: Entity,
    ): Boolean {
        val other = target as? Player ?: return false

        player.faceEntity(other)

        if (!other.isAcceptAid) {
            player.message("${other.displayName} doesn't have aid on.")
            return false
        }

        if (!other.isAtMultiArea) {
            player.message("You can only cast this spell in a multi-area.")
            return false
        }

        if (other.hitpoints == other.maxHitpoints) {
            player.message("${other.displayName} has already full hitpoints.")
            return false
        }

        player.animate(Animation(4411))
        other.gfx(Graphics(744, 0, 100))

        other.message("You have been healed by player ${player.displayName}.")

        val damage = (player.hitpoints * 0.75).toInt()

        player.applyHit(Hit(player, damage, HitLook.REGULAR_DAMAGE))
        other.heal(damage)

        return true
    }
}
