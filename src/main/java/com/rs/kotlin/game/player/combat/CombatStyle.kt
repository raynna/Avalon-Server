package com.rs.kotlin.game.player.combat

import com.rs.core.cache.defintions.ItemDefinitions
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
import com.rs.java.game.player.TickManager
import com.rs.java.game.player.prayer.PrayerEffectHandler
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.player.NewPoison
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.damage.SoakDamage
import com.rs.kotlin.game.player.combat.effects.EquipmentEffects
import com.rs.kotlin.game.player.combat.magic.MagicStyle
import com.rs.kotlin.game.player.combat.magic.special.NightmareStaff
import com.rs.kotlin.game.player.combat.magic.special.ObliterationWeapon
import com.rs.kotlin.game.player.combat.melee.MeleeStyle
import com.rs.kotlin.game.player.combat.range.AmmoType
import com.rs.kotlin.game.player.combat.range.RangeData
import com.rs.kotlin.game.player.combat.range.RangedStyle
import com.rs.kotlin.game.player.combat.range.RangedWeapon
import com.rs.kotlin.game.player.combat.special.CombatContext
import com.rs.kotlin.game.player.combat.special.SpecialAttack
import kotlin.math.ceil
import kotlin.math.max

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
            if (defender.combatDefinitions.isAutoRelatie) {
                if (defender.newActionManager.hasActionWorking()) return
                WorldTasksManager.schedule(object : WorldTask() {
                    override fun run() {
                        if (defender.isDead || attacker.isDead || defender.isLocked) return
                        val style = CombatAction.getCombatStyle(defender, attacker)
                        val retaliateDelay = (style.getAttackSpeed() + 1) / 2

                        val currentDelay = defender.newActionManager.getActionDelay()
                        val finalDelay = max(currentDelay, retaliateDelay)

                        defender.newActionManager.setAction(CombatAction(attacker))
                        if (defender.newActionManager.getActionDelay() > 0)
                            defender.newActionManager.setActionDelay(finalDelay)
                    }
                })
            }

        }
        defender.handleIncommingHit(hit);
    }

    fun onHit(attacker: Player, defender: Entity, hit: Hit) {
        if (hit.graphic != null) {
            defender.gfx(hit.graphic)
        }
        if (defender is Player) {
            if (this is RangedStyle || this is MagicStyle) {
                defender.animate(CombatAnimations.getBlockAnimation(defender))
            }
            defender.chargeManager.processHit(hit)

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
        val weaponName = ItemDefinitions.getItemDefinitions(attacker.getEquipment().weaponId).getName()
        val poisonSeverity = NewPoison.getPoisonSeverity(weaponName)

        if (poisonSeverity != -1) {
            defender.newPoison.roll(attacker, NewPoison.WeaponType.MELEE, poisonSeverity)
        }
        if (defender is NPC) {
            if (!defender.isUnderCombat || defender.canBeAttackedByAutoRelatie()) {
                defender.setTarget(attacker)
            }
            val combatDefinitions = defender.combatDefinitions
            if (combatDefinitions != null) {
                if (combatDefinitions.defendSound != -1) {
                    defender.playSound(combatDefinitions.defendSound, 1)
                }
                if (combatDefinitions.defenceAnim != -1) {
                    defender.animate(combatDefinitions.defenceAnim)
                }
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


    companion object {

        @JvmStatic
        fun handleRingOfRecoil(attacker: Entity, defender: Player, hit: Hit) {
            val RING_OF_RECOIL_ID = 2550
            val MAX_RECOIL_CHARGES = 500
            val MAX_RECOIL_DAMAGE = 40

            if (hit.damage <= 0) return

            if (hit.look !in listOf(
                    Hit.HitLook.MELEE_DAMAGE,
                    Hit.HitLook.RANGE_DAMAGE,
                    Hit.HitLook.MAGIC_DAMAGE
                )) return

            val ring = defender.equipment.getItem(Equipment.SLOT_RING.toInt())
            if (ring == null || ring.id != RING_OF_RECOIL_ID) return

            var remaining = defender.recoilCharges
            if (remaining !in 1..MAX_RECOIL_CHARGES) {
                defender.recoilCharges = MAX_RECOIL_CHARGES
                defender.message("Your ring of recoil has started degrading.")
                remaining = MAX_RECOIL_CHARGES
            }

            var recoil = ceil(hit.damage / 100.0).toInt() * 10
            recoil = recoil.coerceAtMost(MAX_RECOIL_DAMAGE)
            recoil = recoil.coerceAtMost(remaining)

            if (recoil <= 0) return

            attacker.applyHit(Hit(defender, recoil, Hit.HitLook.REFLECTED_DAMAGE))

            remaining -= recoil
            defender.recoilCharges = remaining

            if (remaining <= 0) {
                defender.equipment.deleteItem(RING_OF_RECOIL_ID, 1)
                defender.message("Your ring of recoil turned into dust.")
                defender.recoilCharges = MAX_RECOIL_CHARGES
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
        baseDamage: Int = -1,
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
                    CombatCalculations.calculateMagicMaxHit(attacker, defender, baseDamage, spellId, damageMultiplier)
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

        val weapon = combatContext.weapon

        if (weapon is RangedWeapon) {
            if (weapon.ammoType in listOf(
                    AmmoType.THROWING,
                    AmmoType.DART,
                    AmmoType.JAVELIN,
                    AmmoType.THROWNAXE
                )) return false
            if (weapon.ammoType != ammo.ammoType) return false
            if (weapon.allowedAmmoIds != null && !weapon.allowedAmmoIds.contains(ammo.itemId.first())) return false
        }
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
        if (special !is SpecialAttack.Combat && player.combatDefinitions.spellId > 0) {
            return false
        }
        var specialCost = when {
            NightmareStaff.hasWeapon(player) -> 55
            else -> special.energyCost
        }
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

                val spellActive = player.combatDefinitions.spellId > 0

                if (spellActive &&
                    !NightmareStaff.hasWeapon(player) &&
                    !ObliterationWeapon.hasWeapon(player)
                ) {
                    return false
                }

                val style = when {
                    NightmareStaff.hasWeapon(player) ||
                            ObliterationWeapon.hasWeapon(player) -> MagicStyle(player, target)

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

                if (NightmareStaff.hasWeapon(player)) {
                    NightmareStaff.special(combatContext)
                    player.combatDefinitions.decreaseSpecialAttack(specialCost)
                    return true
                }

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

    fun getDoubleHitDelays(distance: Int): Pair<Int, Int> {
        val firstDelay = 1 + (3 + distance) / 7
        val secondDelay = 1 + (2 + distance) / 4
        return firstDelay to secondDelay
    }

    fun getDarkBowHitDelays(distance: Int): Pair<Int, Int> {
        val firstDelay = 1 + (3 + distance) / 6
        val secondDelay = 1 + (2 + distance) / 3
        return firstDelay to secondDelay
    }

    fun getAutoRetaliateDelay(defender: Player, attacker: Entity): Int {
        val style = CombatAction.getCombatStyle(defender, attacker)
        val speed = style.getAttackSpeed()
        return (speed + 1) / 2
    }


}
