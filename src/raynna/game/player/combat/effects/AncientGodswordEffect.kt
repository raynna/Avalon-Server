package raynna.game.player.combat.effects

import raynna.core.thread.WorldThread
import raynna.game.Entity
import raynna.game.Hit
import raynna.game.player.Player
import raynna.game.player.content.Tint
import raynna.game.player.PendingEffect
import kotlin.math.min

class AncientGodswordEffect(
    attacker: Entity,
    defender: Entity,
    val hitDamage: Int,
    startTick: Int,
    durationTicks: Int = 8
) : PendingEffect(attacker, defender, startTick, durationTicks) {

    companion object {
        private const val BLOOD_GFX = 376
        private const val MAX_DAMAGE = 250
        private const val MAX_NPC_HEAL = 250
        private const val MAX_PLAYER_HEAL = 150

        private const val HUE = 0
        private const val SATURATION = 6
        private const val LIGHTNESS = 28
        private const val STRENGTH = 112
    }

    override fun onTick(currentTick: Int): Boolean {
        val ticksPassed = currentTick - startTick
        return ticksPassed >= durationTicks
    }

    override fun apply() {
        val tint = Tint(0, durationTicks * 30, HUE, SATURATION, LIGHTNESS, STRENGTH);
        defender.tint = tint
    }

    override fun onExecute() {
        if (!attacker.withinDistance(defender.tile, 5)) {
            return
        }
        val healCap = if (defender is Player) MAX_PLAYER_HEAL else MAX_NPC_HEAL
        val maxHeal = min(
            (defender.maxHitpoints * 0.15).toInt(),
            healCap
        )
        val heal = min(hitDamage, maxHeal)

        attacker.heal(heal)
        defender.applyHit(Hit(attacker, MAX_DAMAGE, Hit.HitLook.REGULAR_DAMAGE))
        defender.gfx(BLOOD_GFX)
    }

    override fun onCancel() {
    }
}