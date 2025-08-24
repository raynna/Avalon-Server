package com.rs.kotlin.game.player.combat.special

import com.rs.core.thread.CoresManager
import com.rs.java.game.Entity
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.actions.combat.PlayerCombat
import com.rs.java.utils.Logger
import com.rs.kotlin.game.player.combat.Weapon
import com.rs.kotlin.game.player.combat.melee.MeleeStyle
import com.rs.kotlin.game.player.combat.range.RangedStyle
import java.util.*

sealed class SpecialAttack(
    val energyCost: Int,
    open val hybrid: Boolean = false,
    open val accuracyMultiplier: Double = 1.0,
    open val damageMultiplier: Double = 1.0
) {
    class Instant(
        energyCost: Int,
        val execute: (attacker: Player) -> Unit
    ) : SpecialAttack(energyCost)

    class Combat(
        energyCost: Int,
        val execute: (context: CombatContext) -> Unit,
        override val accuracyMultiplier: Double = 1.0,
        override val damageMultiplier: Double = 1.0,
        override val hybrid: Boolean = false
    ) : SpecialAttack(energyCost)

    class InstantCombat(
        energyCost: Int,
        val execute: (context: CombatContext) -> Unit,
        override val accuracyMultiplier: Double = 1.0,
        override val damageMultiplier: Double = 1.0
    ) : SpecialAttack(energyCost)


    companion object {
        @JvmStatic
        fun submitSpecialRequest(player: Player) {
            val weapon = Weapon.getWeapon(player.equipment.weaponId)
            val special = weapon.special ?: return

            when (special) {
                is Combat -> {}
                is Instant -> {
                    if (player.itemSwitch)
                        return
                    if (player.combatDefinitions.specialAttackPercentage < special.energyCost) {
                        player.message("You don't have enough special attack energy.")
                        return
                    }
                    special.execute(player)
                    player.combatDefinitions.decreaseSpecialAttack(special.energyCost)
                    return
                }
                is InstantCombat -> {
                    /*if (player.itemSwitch)
                        return*/
                    if (player.combatDefinitions.specialAttackPercentage < special.energyCost) {
                        player.message("You don't have enough special attack energy.")
                        return
                    }

                    val target = player.temporaryTarget ?: return

                    val style =
                        if (Weapon.isRangedWeapon(player)) RangedStyle(player, target) else MeleeStyle(player, target)
                    val combatContext = CombatContext(
                        combat = style,
                        attacker = player,
                        defender = target,
                        weapon = weapon,
                        weaponId = player.equipment.weaponId,
                        attackStyle = weapon.weaponStyle.styleSet.styleAt(player.combatDefinitions.attackStyle)!!,
                        attackBonusType = weapon.weaponStyle.styleSet.bonusAt(player.combatDefinitions.attackStyle)!!
                    )
                    //CoresManager.getSlowExecutor().execute {
                        try {
                            if (player.temporaryTarget == null) {
                                player.message("You don't have a target to perform this special on.")
                                return
                            }
                            if (player.isOutOfRange(target, style.getAttackDistance()) || player.shouldAdjustDiagonal(player, target, 0)) {
                                player.addQueuedSpecialAttack(combatContext, special)
                            } else {
                                player.addQueuedSpecialAttack(combatContext, special)
                                //special.execute(combatContext)
                                //player.combatDefinitions.decreaseSpecialAttack(special.energyCost)
                                //player.stopAll(false, true, true)
                            }
                        } catch (e: Throwable) {
                            Logger.handle(e)
                        }
                    //}
                    return
                }
            }
            player.combatDefinitions.switchUsingSpecialAttack()
        }
    }
}
