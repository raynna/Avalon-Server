package com.rs.kotlin.game.player.combat.special

import com.rs.core.thread.WorldThread
import com.rs.java.game.player.Player
import com.rs.java.game.player.TickManager.TickKeys
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.CombatAction
import com.rs.kotlin.game.player.combat.Weapon
import com.rs.kotlin.game.player.combat.melee.MeleeStyle
import com.rs.kotlin.game.player.combat.range.RangedStyle

sealed class SpecialAttack(
    val energyCost: Int,
    open val hybrid: Boolean = false,
    open val accuracyMultiplier: Double = 1.0,
    open val damageMultiplier: Double = 1.0,
) {
    class Instant(
        energyCost: Int,
        val execute: (attacker: Player) -> Unit,
    ) : SpecialAttack(energyCost)

    class Combat(
        energyCost: Int,
        val execute: (context: CombatContext) -> Unit,
    ) : SpecialAttack(energyCost)

    class InstantCombat(
        energyCost: Int,
        val execute: (context: CombatContext) -> Unit,
    ) : SpecialAttack(energyCost)

    class InstantRangeCombat(
        energyCost: Int,
        val execute: (context: CombatContext) -> Unit,
    ) : SpecialAttack(energyCost)

    companion object {
        /**
         * Call this on REAL spec click (packet).
         */
        @JvmStatic
        fun submitSpecialRequest(player: Player) {
            submitSpecialRequest(player, fromEquip = false)
        }

        /**
         * Call this when weapon equip finishes AND you want instant specs to fire if spec is already ON.
         * IMPORTANT: This must NOT toggle spec.
         */
        @JvmStatic
        fun submitSpecialRequestFromEquip(player: Player) {
            submitSpecialRequest(player, fromEquip = true)
        }

        private fun submitSpecialRequest(
            player: Player,
            fromEquip: Boolean,
        ) {
            val weaponId =
                if (player.itemSwitch && player.predictedWeaponSwitch != -1) {
                    player.predictedWeaponSwitch
                } else {
                    player.equipment.weaponId
                }

            val weapon = Weapon.getWeapon(weaponId)
            val special =
                weapon.special ?: return

            if (!fromEquip) {
                player.lastSpecClickTick = WorldThread.getCycleIndex()
            }

            if (fromEquip && !player.combatDefinitions.isUsingSpecialAttack) {
                return
            }

            if (player.combatDefinitions.specialAttackPercentage < special.energyCost) {
                if (!fromEquip) player.message("You don't have enough special attack energy.")
                return
            }

            when (special) {
                is Combat -> {
                    if (fromEquip) return
                    player.combatDefinitions.switchUsingSpecialAttack()
                    return
                }

                is Instant -> {
                    if (fromEquip) return
                    player.combatDefinitions.switchUsingSpecialAttack()
                    player.queuedInstantSpecial = special
                    return
                }

                is InstantRangeCombat -> {
                    val timerActive = player.tickManager.isActive(TickKeys.GRANITE_MAUL_TIMER)
                    if (!fromEquip) {
                        if (!timerActive) {
                            if (!player.combatDefinitions.isUsingSpecialAttack) {
                                player.message(
                                    "Warning: Since the weapon's special is an instant attack, it will be wasted when used on a first strike.",
                                )
                            }
                            player.combatDefinitions.switchUsingSpecialAttack()
                            return
                        }
                        player.combatDefinitions.switchUsingSpecialAttack()
                        val usingSpec = player.combatDefinitions.isUsingSpecialAttack

                        if (!usingSpec) {
                            player.clearActiveInstantSpecial()
                            return
                        }
                    }

                    val target = player.temporaryTarget ?: return

                    val style =
                        if (Weapon.isRangedWeapon(player)) {
                            RangedStyle(player, target)
                        } else {
                            MeleeStyle(player, target)
                        }
                    val ctx =
                        CombatContext(
                            combat = style,
                            attacker = player,
                            defender = target,
                            weapon = weapon,
                            weaponId = weaponId,
                            attackStyle = weapon.weaponStyle.styleSet.styleAt(player.combatDefinitions.attackStyle)!!,
                            attackBonusType = weapon.weaponStyle.styleSet.bonusAt(player.combatDefinitions.attackStyle)!!,
                        )
                    player.setActiveInstantSpecial(ctx, special)
                    player.actionManager.setAction(CombatAction(target))
                    return
                }

                is InstantCombat -> {
                    val timerActive = player.tickManager.isActive(TickKeys.GRANITE_MAUL_TIMER)
                    if (!fromEquip) {
                        if (!timerActive) {
                            if (!player.combatDefinitions.isUsingSpecialAttack) {
                                player.message(
                                    "Warning: Since the maul's special is an instant attack, it will be wasted when used on a first strike.",
                                )
                            }
                            player.combatDefinitions.switchUsingSpecialAttack()
                            return
                        }
                    }

                    val target = player.temporaryTarget ?: return

                    val style =
                        if (Weapon.isRangedWeapon(player)) {
                            RangedStyle(player, target)
                        } else {
                            MeleeStyle(player, target)
                        }

                    val ctx =
                        CombatContext(
                            combat = style,
                            attacker = player,
                            defender = target,
                            weapon = weapon,
                            weaponId = weaponId,
                            attackStyle = weapon.weaponStyle.styleSet.styleAt(player.combatDefinitions.attackStyle)!!,
                            attackBonusType = weapon.weaponStyle.styleSet.bonusAt(player.combatDefinitions.attackStyle)!!,
                        )

                    val queuedCount = player.queuedInstantCombats.size

                    if (queuedCount >= 2) {
                        return
                    }
                    player.addQueuedSpecialAttack(ctx, special)

                    if (timerActive) {
                    }

                    return
                }
            }
        }
    }
}
