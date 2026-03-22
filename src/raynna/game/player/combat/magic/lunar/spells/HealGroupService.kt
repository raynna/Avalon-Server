package raynna.game.player.combat.magic.lunar.spells

import raynna.core.thread.CoresManager
import raynna.game.Animation
import raynna.game.Graphics
import raynna.game.Hit
import raynna.game.Hit.HitLook
import raynna.game.World
import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.game.player.TickManager
import raynna.game.player.actions.combat.Combat
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
