package raynna.game.player.combat.magic.lunar.spells

import raynna.game.Animation
import raynna.game.Entity
import raynna.game.Graphics
import raynna.game.player.Player
import raynna.game.player.Skills

object EnergyTransferService {
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

        if (other.combatDefinitions.specialAttackPercentage == 100) {
            player.message("${other.displayName} has full special attack.")
            return false
        }

        if (player.combatDefinitions.specialAttackPercentage == 0) {
            player.message("You don't have any special attack left.")
            return false
        }

        if (!other.isAtMultiArea) {
            player.message("You can only cast this spell in a multi-area.")
            return false
        }

        var amount = 100 - other.combatDefinitions.specialAttackPercentage

        if (amount > player.combatDefinitions.specialAttackPercentage) {
            amount = player.combatDefinitions.specialAttackPercentage
        }

        player.combatDefinitions.decreaseSpecialAttack(amount)
        other.combatDefinitions.increaseSpecialAttack(amount)

        player.animate(Animation(4411))
        other.gfx(Graphics(744, 0, 100))

        other.message("You got an energy transfer from player ${player.displayName}.")
        other.poison.reset()

        return true
    }
}
