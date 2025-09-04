package com.rs.kotlin.game.player.combat

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.ForceTalk
import com.rs.java.game.Hit
import com.rs.java.game.item.Item
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.prayer.PrayerEffectHandler
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.damage.SoakDamage
import com.rs.kotlin.game.player.combat.effects.EquipmentEffects
import com.rs.kotlin.game.player.combat.magic.MagicStyle
import com.rs.kotlin.game.player.combat.magic.special.ObliterationWeapon
import com.rs.kotlin.game.player.combat.melee.MeleeStyle
import com.rs.kotlin.game.player.combat.range.RangeData
import com.rs.kotlin.game.player.combat.range.RangedStyle
import com.rs.kotlin.game.player.combat.special.CombatContext
import com.rs.kotlin.game.player.combat.special.SpecialAttack

interface CombatStyle {
    fun canAttack(attacker: Player, defender: Entity): Boolean
    fun getAttackSpeed(): Int
    fun getHitDelay(): Int
    fun getAttackDistance(): Int
    fun attack()
    fun onStop(interrupted: Boolean)
    fun delayHits(vararg hits: PendingHit)

    fun outgoingHit(attacker: Player, defender: Entity, pending: PendingHit) {
        val hit = pending.hit
        PrayerEffectHandler.handleProtectionEffects(attacker, defender, hit)//protection prayers first
        EquipmentEffects.applyOutgoing(attacker, defender, hit, this)//divine, keris applied second
        if (defender is Player) {
            EquipmentEffects.applyIncoming(defender, hit, this)//divine, keris applied second
        }
        SoakDamage.handleAbsorb(attacker, defender, hit)//soak applied last, after all effect reductions
        attacker.chargeManager.processOutgoingHit()
        PrayerEffectHandler.handleOffensiveEffects(
            attacker,
            defender,
            hit
        )//boost prayers effects, like increase turmoil or leech prayers
        // last, due to soulsplit would heal confusing heals if not & rest is just increase prayer %
        if (defender is Player) {
            if (this is MeleeStyle) {
                defender.animate(CombatAnimations.getBlockAnimation(defender))
            }
            defender.chargeManager.processIncommingHit()
        }
        defender.handleIncommingHit(hit);
    }

    fun onHit(attacker: Player, defender: Entity, hit: Hit) {
        if (defender is Player) {
            if (this is RangedStyle || this is MagicStyle) {
                defender.animate(CombatAnimations.getBlockAnimation(defender))
            }
            defender.chargeManager.processHit(hit)
            if (defender.combatDefinitions.isAutoRelatie && !defender.newActionManager.hasActionWorking()) {
                defender.newActionManager.setAction(CombatAction(attacker));
            }
            handleRingOfRecoil(attacker, defender, hit)
            if (defender.hasVengeance() && hit.damage > 0) {
                val hitType = hit.look
                if (hitType != Hit.HitLook.MELEE_DAMAGE &&
                    hitType != Hit.HitLook.RANGE_DAMAGE &&
                    hitType != Hit.HitLook.MAGIC_DAMAGE
                ) return
                defender.setVengeance(false)
                defender.nextForceTalk = ForceTalk("Taste vengeance!")
                attacker.applyHit(Hit(defender, (hit.damage * 0.75).toInt(), Hit.HitLook.REGULAR_DAMAGE));
            }
        }
        if (defender is NPC) {
            if (!defender.isUnderCombat || defender.canBeAttackedByAutoRelatie()) {
                defender.setTarget(attacker)
            }
            if (defender.id == Rscm.lookup("npc.magic_dummy") || defender.id == Rscm.lookup("npc.melee_dummy")) {
                if (attacker.prayer.prayerPoints < attacker.skills.getLevelForXp(Skills.PRAYER) * 10) {
                    attacker.prayer.restorePrayer(attacker.skills.getLevelForXp(Skills.PRAYER) * 10)
                }
                if (attacker.combatDefinitions.specialAttackPercentage < 100)
                    attacker.combatDefinitions.increaseSpecialAttack(100)
            }
        }
        defender.handleHit(hit);
    }


    fun handleRingOfRecoil(attacker: Player, defender: Player, hit: Hit) {
        val RING_OF_RECOIL_ID = 2550
        val MIN_DAMAGE_FOR_RECOIL = 10
        val MAX_RECOIL_DAMAGE = 60
        val RECOIL_DAMAGE_PERCENT = 0.1

        val hitType = hit.look
        if (hitType != Hit.HitLook.MELEE_DAMAGE &&
            hitType != Hit.HitLook.RANGE_DAMAGE &&
            hitType != Hit.HitLook.MAGIC_DAMAGE
        ) return
        if (hit.damage < MIN_DAMAGE_FOR_RECOIL) return
        val ring = defender.equipment.getItem(Equipment.SLOT_RING.toInt())
        if (ring == null || !ring.isItem("item.ring_of_recoil")) return
        var remainingCharges = defender.recoilCharges
        if (remainingCharges <= 0) {
            defender.equipment.deleteItem(RING_OF_RECOIL_ID, 1)
            defender.recoilCharges = 500
            return
        }
        var recoilDamage = (hit.damage * RECOIL_DAMAGE_PERCENT).toInt()
        recoilDamage = recoilDamage.coerceAtMost(MAX_RECOIL_DAMAGE)
        recoilDamage = recoilDamage.coerceAtMost(remainingCharges)
        if (recoilDamage > 0) {
            attacker.applyHit(Hit(defender, recoilDamage, Hit.HitLook.REGULAR_DAMAGE))
            remainingCharges -= recoilDamage
            defender.recoilCharges = remainingCharges
            if (remainingCharges <= 0) {
                defender.equipment.deleteItem(RING_OF_RECOIL_ID, 1)
                defender.recoilCharges = 500
            }
        }
    }

    fun scheduleHit(delay: Int, action: () -> Unit) {
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                action()
            }
        }, delay)
    }

    fun getHitChance(
        attacker: Player,
        defender: Entity,
        accuracyMultiplier: Double = 1.0,
        combatContext: CombatContext? = null
    ): Double {
        var finalMultiplier = accuracyMultiplier

        if (combatContext?.usingSpecial == true) {
            val special = combatContext.weapon.special
            if (special is SpecialAttack.Combat ||
                special is SpecialAttack.InstantCombat ||
                special is SpecialAttack.InstantRangeCombat
            ) {
                finalMultiplier *= special.accuracyMultiplier ?: 1.0
            }
        }

        return CombatCalculations.getHitChance(attacker, defender, this, finalMultiplier)
    }


    fun addHit(
        damage: Int,
        attacker: Player,
        defender: Entity,
        combatType: CombatType,
        hitLook: Hit.HitLook? = null
    ): Hit {
        val resolvedHitLook = hitLook ?: when (combatType) {
            CombatType.MELEE -> Hit.HitLook.MELEE_DAMAGE
            CombatType.RANGED -> Hit.HitLook.RANGE_DAMAGE
            CombatType.MAGIC -> Hit.HitLook.MAGIC_DAMAGE
        }
        val hit = Hit(attacker, damage, resolvedHitLook)
        if (hit.isCriticalHit && !hit.isCombatLook) {
            hit.critical = false
        }
        return hit
    }

    fun registerDamage(
        attacker: Player,
        defender: Entity,
        combatType: CombatType,
        attackStyle: AttackStyle = AttackStyle.ACCURATE,
        weapon: Weapon? = null,
        spellId: Int = -1,
        damageMultiplier: Double = 1.0,
        hitLook: Hit.HitLook? = null
    ): Hit {
        val resolvedHitLook = hitLook ?: when (combatType) {
            CombatType.MELEE -> Hit.HitLook.MELEE_DAMAGE
            CombatType.RANGED -> Hit.HitLook.RANGE_DAMAGE
            CombatType.MAGIC -> Hit.HitLook.MAGIC_DAMAGE
        }
        val hit = when (combatType) {
            CombatType.MELEE -> CombatCalculations.calculateMeleeMaxHit(attacker, defender, damageMultiplier)
            CombatType.RANGED -> CombatCalculations.calculateRangedMaxHit(attacker, defender, damageMultiplier)
            CombatType.MAGIC -> {
                requireNotNull(spellId) { "Spell required for magic attack" }
                CombatCalculations.calculateMagicMaxHit(attacker, defender, spellId)
            }
        }
        hit.look = resolvedHitLook
        if (hit.isCriticalHit && !hit.isCombatLook) {
            hit.critical = false
        }
        return hit
    }

    fun registerHit(
        attacker: Player,
        defender: Entity,
        combatType: CombatType,
        attackStyle: AttackStyle = AttackStyle.ACCURATE,
        weapon: Weapon? = null,
        spellId: Int = -1,
        accuracyMultiplier: Double = 1.0,
        damageMultiplier: Double = 1.0,
        hitLook: Hit.HitLook? = null
    ): Hit {
        val resolvedHitLook = hitLook ?: when (combatType) {
            CombatType.MELEE -> Hit.HitLook.MELEE_DAMAGE
            CombatType.RANGED -> Hit.HitLook.RANGE_DAMAGE
            CombatType.MAGIC -> Hit.HitLook.MAGIC_DAMAGE
        }

        val landed = when (combatType) {
            CombatType.MELEE -> {
                requireNotNull(weapon) { "Weapon required for melee attack" }
                CombatCalculations.calculateMeleeAccuracy(attacker, defender, accuracyMultiplier)
            }

            CombatType.RANGED -> {
                requireNotNull(weapon) { "Weapon required for ranged attack" }
                CombatCalculations.calculateRangedAccuracy(attacker, defender, accuracyMultiplier)
            }

            CombatType.MAGIC -> CombatCalculations.calculateMagicAccuracy(attacker, defender, accuracyMultiplier)
        }

        val hit = if (landed) {
            when (combatType) {
                CombatType.MELEE -> CombatCalculations.calculateMeleeMaxHit(attacker, defender, damageMultiplier)
                CombatType.RANGED -> CombatCalculations.calculateRangedMaxHit(attacker, defender, damageMultiplier)
                CombatType.MAGIC -> {
                    CombatCalculations.calculateMagicMaxHit(attacker, defender, spellId, damageMultiplier)
                }
            }
        } else {
            Hit(attacker, 0, resolvedHitLook)
        }
        hit.look = resolvedHitLook
        if (!landed) {
            hit.landed = false
        }
        if (hit.isCriticalHit && !hit.isCombatLook) {
            hit.critical = false
        }
        return hit
    }

    fun executeEffect(combatContext: CombatContext): Boolean {
        combatContext.weapon.effect?.let { effect ->
            CombatAnimations.getAnimation(
                combatContext.weaponId,
                combatContext.attackStyle,
                combatContext.attacker.combatDefinitions.attackStyle
            ).let { combatContext.attacker.animate(it) }
            effect.execute(combatContext)
            return true;
        }
        return false;
    }

    fun executeAmmoEffect(combatContext: CombatContext): Boolean {
        val ammo = combatContext.ammo ?: return false
        val effect = ammo.specialEffect ?: return false
        return effect.execute(combatContext)
    }

    fun isRangedWeapon(player: Player): Boolean {
        val weaponId = player.equipment.getWeaponId()
        val ranged = RangeData.getWeaponByItemId(weaponId);
        return ranged != null
    }


    fun executeSpecialAttack(player: Player, target: Entity? = null): Boolean {
        val weapon = Weapon.getWeapon(player.equipment.weaponId) ?: return false
        val ammo = RangeData.getAmmoByItemId(player.equipment.ammoId)
        val special = weapon.special ?: return false
        if (!player.combatDefinitions.isUsingSpecialAttack)
            return false

        // Deduct energy, apply Ring of Vigour, etc.
        var specialCost = special.energyCost
        if (player.getEquipment().containsOneItem(Item.getId("item.ring_of_vigour"))) {
            specialCost = (specialCost * 0.9).toInt()
        }

        if (player.combatDefinitions.specialAttackPercentage < specialCost) {
            player.message("You don't have enough special attack energy.")
            player.combatDefinitions.switchUsingSpecialAttack()
            return false
        }

        when (special) {
            is SpecialAttack.Instant -> {
                special.execute(player)
            }

            is SpecialAttack.InstantCombat -> {
                val actualTarget = target ?: player.temporaryTarget ?: return false
                val style =
                    if (isRangedWeapon(player)) RangedStyle(player, actualTarget) else MeleeStyle(player, actualTarget)
                val combatContext = CombatContext(
                    combat = style,
                    attacker = player,
                    defender = actualTarget,
                    weapon = weapon,
                    weaponId = player.equipment.weaponId,
                    attackStyle = weapon.weaponStyle.styleSet.styleAt(player.combatDefinitions.attackStyle)!!,
                    attackBonusType = weapon.weaponStyle.styleSet.bonusAt(player.combatDefinitions.attackStyle)!!,
                    usingSpecial = true,
                )
                special.execute(combatContext)
            }

            is SpecialAttack.Combat -> {
                if (target == null) return false
                val style = when {
                    ObliterationWeapon.hasWeapon(player) -> MagicStyle(player, target);
                    isRangedWeapon(player) -> RangedStyle(player, target)
                    else -> MeleeStyle(player, target)
                }

                val combatContext = CombatContext(
                    combat = style,
                    attacker = player,
                    defender = target,
                    weapon = weapon,
                    ammo = ammo,
                    weaponId = player.equipment.weaponId,
                    attackStyle = weapon.weaponStyle.styleSet.styleAt(player.combatDefinitions.attackStyle)!!,
                    attackBonusType = weapon.weaponStyle.styleSet.bonusAt(player.combatDefinitions.attackStyle)!!,
                    usingSpecial = true,
                )

                special.execute(combatContext)
            }

            is SpecialAttack.InstantRangeCombat -> {
                val actualTarget = target ?: player.temporaryTarget ?: return false
                val style =
                    if (isRangedWeapon(player)) RangedStyle(player, actualTarget) else MeleeStyle(player, actualTarget)
                val combatContext = CombatContext(
                    combat = style,
                    attacker = player,
                    defender = actualTarget,
                    weapon = weapon,
                    weaponId = player.equipment.weaponId,
                    attackStyle = weapon.weaponStyle.styleSet.styleAt(player.combatDefinitions.attackStyle)!!,
                    attackBonusType = weapon.weaponStyle.styleSet.bonusAt(player.combatDefinitions.attackStyle)!!,
                    usingSpecial = true,
                )
                special.execute(combatContext)
            }
        }

        player.combatDefinitions.decreaseSpecialAttack(specialCost)
        return true
    }

    fun getDarkBowHitDelays(distance: Int): Pair<Int, Int> {
        val firstDelay = 1 + (3 + distance) / 6
        val secondDelay = 1 + (2 + distance) / 3
        return firstDelay to secondDelay
    }


}
