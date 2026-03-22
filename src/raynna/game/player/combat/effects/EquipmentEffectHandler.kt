package raynna.game.player.combat.effects

import raynna.game.Hit
import raynna.game.player.Player
import raynna.game.player.combat.AttackStyle
import raynna.game.player.combat.CombatType

object EquipmentEffectHandler {

    fun handleOutgoingHit(
        attacker: Player,
        defender: Any,
        hit: Hit,
        combatType: CombatType,
        @Suppress("UNUSED_PARAMETER") attackStyle: AttackStyle,
        @Suppress("UNUSED_PARAMETER") weaponId: Int
    ) {
        EquipmentEffects.applyOutgoing(attacker, defender, hit, combatType)
    }

    fun handleIncomingHit(
        defender: Player,
        hit: Hit,
        combatType: CombatType,
        @Suppress("UNUSED_PARAMETER") attackStyle: AttackStyle,
        @Suppress("UNUSED_PARAMETER") weaponId: Int
    ) {
        EquipmentEffects.applyIncoming(defender, hit, combatType)
    }
}
