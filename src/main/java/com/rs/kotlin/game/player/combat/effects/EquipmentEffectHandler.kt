package com.rs.kotlin.game.player.combat.effects

import com.rs.java.game.Hit
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.AttackStyle
import com.rs.kotlin.game.player.combat.CombatType

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
