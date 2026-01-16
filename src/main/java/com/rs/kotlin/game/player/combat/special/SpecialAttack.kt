package com.rs.kotlin.game.player.combat.special

import com.rs.core.thread.WorldThread
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.Weapon
import com.rs.kotlin.game.player.combat.melee.MeleeStyle
import com.rs.kotlin.game.player.combat.range.RangedStyle

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

    class InstantRangeCombat(
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
                is InstantRangeCombat -> {
                    if (player.combatDefinitions.specialAttackPercentage < special.energyCost) {
                        player.message("You don't have enough special attack energy.")
                        return
                    }

                    // Toggle special attack without executing
                    player.combatDefinitions.switchUsingSpecialAttack()

                    if (player.combatDefinitions.isUsingSpecialAttack) {
                        val target = player.temporaryTarget ?: return
                        val style = if (Weapon.isRangedWeapon(player)) RangedStyle(player, target) else MeleeStyle(player, target)
                        val combatContext = CombatContext(
                            combat = style,
                            attacker = player,
                            defender = target,
                            weapon = weapon,
                            weaponId = player.equipment.weaponId,
                            attackStyle = weapon.weaponStyle.styleSet.styleAt(player.combatDefinitions.attackStyle)!!,
                            attackBonusType = weapon.weaponStyle.styleSet.bonusAt(player.combatDefinitions.attackStyle)!!
                        )
                        player.setActiveInstantSpecial(combatContext, special)
                        return
                    } else {
                        player.clearActiveInstantSpecial()
                        return
                    }
                }
                is InstantCombat -> {
                    if (player.newActionManager.getActionDelay() == 0) {
                        if (!player.combatDefinitions.usingSpecialAttack)
                            player.message("Warning: Since the maul's special is an instant attack, it will be wasted when used on a first strike.")
                        player.combatDefinitions.switchUsingSpecialAttack()
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
                    player.addQueuedSpecialAttack(combatContext, special)
                    return
                }
            }
            player.combatDefinitions.switchUsingSpecialAttack()
            player.lastSpecClickTick = WorldThread.getCycleIndex()
        }
    }
}
