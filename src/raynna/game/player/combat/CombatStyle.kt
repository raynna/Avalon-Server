package raynna.game.player.combat

import raynna.core.tasks.WorldTask
import raynna.core.tasks.WorldTasksManager
import raynna.data.rscm.Rscm
import raynna.game.Entity
import raynna.game.ForceTalk
import raynna.game.Hit
import raynna.game.item.Item
import raynna.game.npc.NPC
import raynna.game.player.Equipment
import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.game.player.TickManager
import raynna.game.player.bot.PlayerBotManager
import raynna.game.player.combat.damage.HitRoller
import raynna.game.player.combat.damage.PendingHit
import raynna.game.player.combat.damage.SoakDamage
import raynna.game.player.combat.effects.EquipmentEffects
import raynna.game.player.combat.magic.special.NightmareStaff
import raynna.game.player.combat.magic.special.ObliterationWeapon
import raynna.game.player.combat.melee.MeleeStyle
import raynna.game.player.combat.range.AmmoType
import raynna.game.player.combat.range.RangeData
import raynna.game.player.combat.range.RangedWeapon
import raynna.game.player.combat.special.CombatContext
import raynna.game.player.combat.special.EffectResult
import raynna.game.player.combat.special.SpecialAttack
import raynna.game.player.prayer.PrayerEffectHandler
import raynna.game.world.task.WorldTasks
import kotlin.math.ceil
import kotlin.math.max

interface CombatStyle {
    fun canAttack(
        attacker: Player,
        defender: Entity,
    ): Boolean

    fun getAttackSpeed(): Int

    fun getHitDelay(): Int

    fun getAttackDistance(): Int

    fun attack()

    fun onStop(interrupted: Boolean)

    fun delayHits(vararg hits: PendingHit)

    fun outgoingHit(
        attacker: Player,
        defender: Entity,
        pending: PendingHit,
    ) {
        val hit = pending.hit
        if (defender is Player) {
            if (this is MeleeStyle) {
                defender.animate(CombatUtils.getBlockAnimation(defender))
                defender.playSound(CombatUtils.getBlockSound(defender), 20, 1)
            }
            defender.chargeManager.processIncommingHit()
            if (defender.combatDefinitions.isAutoRelatie) {
                if (defender.actionManager.action == null) {
                    WorldTasksManager.schedule(
                        object : WorldTask() {
                            override fun run() {
                                if (defender.isDead || attacker.isDead || defender.isLocked) return
                                val style = CombatAction.getCombatStyle(defender, attacker)
                                val retaliateDelay = (style.getAttackSpeed() + 1) / 2

                                val currentDelay = defender.actionManager.actionDelay
                                val finalDelay = max(currentDelay, retaliateDelay)

                                defender.actionManager.setAction(CombatAction(attacker))
                                if (defender.actionManager.actionDelay > 0) {
                                    defender.actionManager.actionDelay = finalDelay
                                }
                            }
                        },
                    )
                }
            }
        }
        PrayerEffectHandler.handleProtectionReduction(attacker, defender, hit) // protection prayers first
        EquipmentEffects.applyOutgoing(attacker, defender, hit, this) // divine, keris applied second
        if (defender is Player) {
            EquipmentEffects.applyIncoming(defender, hit, this) // divine, keris applied second
        }
        SoakDamage.handleAbsorb(attacker, defender, hit) // soak applied last, after all effect reductions
        attacker.chargeManager.processOutgoingHit()
        PrayerEffectHandler.handleOffensiveEffects(
            attacker,
            defender,
            hit,
        )
        defender.handleIncommingHit(hit)
    }

    fun onHit(
        attacker: Player,
        defender: Entity,
        hit: Hit,
    ) {
        if (hit.graphic != null) {
            defender.gfx(hit.graphic)
        }
        defender.handleHit(hit)
        PrayerEffectHandler.handleDeflect(attacker, defender, hit)
        if (defender is Player) {
            /*if (this is RangedStyle || this is MagicStyle) {
                defender.animate(CombatUtils.getBlockAnimation(defender))
                defender.playSound(CombatUtils.getBlockSound(defender), 1)
            }*/
            defender.chargeManager.processHit(hit)

            handleRingOfRecoil(attacker, defender, hit)
            if (defender.hasVengeance() && hit.damage > 0) {
                val hitType = hit.look
                if (hitType != Hit.HitLook.MELEE_DAMAGE &&
                    hitType != Hit.HitLook.RANGE_DAMAGE &&
                    hitType != Hit.HitLook.MAGIC_DAMAGE
                ) {
                    return
                }
                defender.setVengeance(false)
                defender.nextForceTalk = ForceTalk("Taste vengeance!")
                attacker.applyHit(Hit(defender, (hit.damage * 0.75).toInt(), Hit.HitLook.REGULAR_DAMAGE))
            }
        }
        if (defender is NPC) {
            val currentTarget = defender.combat.target

            if (currentTarget != null && currentTarget != attacker) {
                val lastAttackTickActive =
                    currentTarget.tickManager
                        .isActive(TickManager.TickKeys.LAST_ATTACK_TICK)

                if (!lastAttackTickActive) {
                    defender.setTarget(attacker)
                }
            } else {
                defender.setTarget(attacker)
            }
        }
        if (defender is NPC) {
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
                if (attacker.prayer.prayerPoints < attacker.skills.getRealLevel(Skills.PRAYER) * 10) {
                    attacker.prayer.restorePrayer(attacker.skills.getRealLevel(Skills.PRAYER) * 10)
                }
                if (attacker.combatDefinitions.specialAttackPercentage < 100) {
                    attacker.combatDefinitions.increaseSpecialAttack(100)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun handleRingOfRecoil(
            attacker: Entity,
            defender: Player,
            hit: Hit,
        ) {
            val RING_OF_RECOIL_ID = 2550
            val MAX_RECOIL_CHARGES = 500
            val MAX_RECOIL_DAMAGE = 40

            if (hit.damage <= 0) return

            if (hit.look !in
                listOf(
                    Hit.HitLook.MELEE_DAMAGE,
                    Hit.HitLook.RANGE_DAMAGE,
                    Hit.HitLook.MAGIC_DAMAGE,
                )
            ) {
                return
            }

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

    fun scheduleHit(
        delay: Int,
        action: () -> Unit,
    ) {
        WorldTasks.submit(delay) { action() }
    }

    fun getHitChance(
        attacker: Player,
        defender: Entity,
        accuracyMultiplier: Double = 1.0,
        combatContext: CombatContext? = null,
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

    fun hitRoll(
        damage: Int,
        attacker: Player,
        defender: Entity,
        combatType: CombatType,
        hitLook: Hit.HitLook? = null,
    ): Hit {
        val resolvedHitLook =
            hitLook ?: when (combatType) {
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

    fun hitRoll(
        type: CombatType,
        attacker: Player,
        target: Entity,
    ): HitRoller = HitRoller(attacker, target, type)

    fun executeEffect(context: CombatContext): EffectResult {
        val effect = context.weapon.effect ?: return EffectResult.CONTINUE

        val result = effect.execute(context)

        if (result == EffectResult.COMPLETE) {
            context.attacker.animate(
                CombatUtils.getAnimation(
                    context.weaponId,
                    context.attackStyle,
                    context.attacker.combatDefinitions.attackStyle,
                ),
            )
        }
        return result
    }

    fun executeAmmoEffect(combatContext: CombatContext): EffectResult {
        val ammo = combatContext.ammo ?: return EffectResult.CONTINUE
        val weapon = combatContext.weapon

        if (weapon is RangedWeapon) {
            if (weapon.ammoType in
                listOf(
                    AmmoType.THROWING,
                    AmmoType.DART,
                    AmmoType.JAVELIN,
                    AmmoType.THROWNAXE,
                )
            ) {
                return EffectResult.CONTINUE
            }

            if (weapon.ammoType != ammo.ammoType) return EffectResult.CONTINUE

            if (weapon.allowedAmmoIds != null &&
                !weapon.allowedAmmoIds.contains(ammo.itemId.first())
            ) {
                return EffectResult.CONTINUE
            }
        }

        val effect = ammo.specialEffect ?: return EffectResult.CONTINUE

        return effect.execute(combatContext)
    }

    fun isRangedWeapon(player: Player): Boolean {
        val weaponId = player.equipment.getWeaponId()
        val ranged = RangeData.getWeaponByItemId(weaponId)
        return ranged != null
    }

    fun executeSpecialAttack(context: CombatContext): Boolean {
        val player = context.attacker
        val target = context.defender
        val weapon = context.weapon
        val ammo = context.ammo

        val special = weapon.special ?: return false

        if (!player.combatDefinitions.isUsingSpecialAttack) {
            return false
        }

        if (special !is SpecialAttack.Combat && player.combatDefinitions.spellId > 0) {
            return false
        }

        var specialCost =
            when {
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
                special.execute(context)
            }

            is SpecialAttack.Combat -> {
                val spellActive = player.combatDefinitions.spellId > 0

                if (spellActive &&
                    !NightmareStaff.hasWeapon(player) &&
                    !ObliterationWeapon.hasWeapon(player)
                ) {
                    return false
                }

                if (NightmareStaff.hasWeapon(player)) {
                    NightmareStaff.special(context)
                    player.combatDefinitions.decreaseSpecialAttack(specialCost)
                    return true
                }

                special.execute(context)
            }

            is SpecialAttack.InstantRangeCombat -> {
                special.execute(context)
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

    fun getAutoRetaliateDelay(
        defender: Player,
        attacker: Entity,
    ): Int {
        val style = CombatAction.getCombatStyle(defender, attacker)
        val speed = style.getAttackSpeed()
        return (speed + 1) / 2
    }
}
