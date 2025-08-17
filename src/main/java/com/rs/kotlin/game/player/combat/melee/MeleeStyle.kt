package com.rs.kotlin.game.player.combat.melee

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.prayer.PrayerEffectHandler
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.damage.SoakDamage
import com.rs.kotlin.game.player.combat.special.CombatContext
import com.rs.kotlin.game.player.combat.special.meleeHit
import kotlin.math.min

class MeleeStyle(val attacker: Player, val defender: Entity) : CombatStyle {

    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        val currentWeapon = getCurrentWeapon(attacker)
        val currentWeaponId = getCurrentWeaponId(attacker)
        val attackStyle = getAttackStyle(currentWeapon)
        val attackBonusType = getAttackBonusType(currentWeapon)
        val combatContext = CombatContext(
            combat = this,
            attacker = this.attacker,
            defender = this.defender,
            weapon = currentWeapon,
            weaponId = currentWeaponId,
            attackStyle = attackStyle,
            attackBonusType = attackBonusType
        )
        //TODO
        return true
    }

    private fun getCurrentWeaponId(attacker: Player): Int {
        val weaponId = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1
        val gloves = attacker.equipment.getItem(Equipment.SLOT_HANDS.toInt())
        val hasGoliathGloves = attacker.combatDefinitions.hasGoliath(gloves)
        return when {
            weaponId != -1 -> weaponId
            hasGoliathGloves && weaponId == -1 -> StandardMelee.getGoliathWeapon().itemId[0]
            else -> -1
        }
    }


    private fun getCurrentWeapon(attacker: Player): Weapon {
        val weaponId = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1
        val gloves = attacker.equipment.getItem(Equipment.SLOT_HANDS.toInt())
        val hasGoliathGloves = attacker.combatDefinitions.hasGoliath(gloves)
        return when {
            weaponId != -1 && StandardMelee.getWeaponByItemId(weaponId) != null ->
                StandardMelee.getWeaponByItemId(weaponId)!!
            hasGoliathGloves -> StandardMelee.getGoliathWeapon()
            else -> StandardMelee.getDefaultWeapon()
        }
    }

    private fun getAttackStyle(currentWeapon: Weapon): AttackStyle {
        val attackStyleId = attacker.combatDefinitions.attackStyle
        return currentWeapon.weaponStyle.styleSet.styleAt(attackStyleId) ?: AttackStyle.ACCURATE
    }

    private fun getAttackBonusType(currentWeapon: Weapon): AttackBonusType {
        val attackStyleId = attacker.combatDefinitions.attackStyle
        return currentWeapon.weaponStyle.styleSet.bonusAt(attackStyleId) ?: AttackBonusType.CRUSH
    }

    override fun getAttackDistance(): Int {
        val currentWeapon = getCurrentWeapon(attacker) // unarmed
        return currentWeapon.attackRange ?: 0
    }

    override fun getAttackSpeed(): Int {
        val currentWeapon = getCurrentWeapon(attacker)
        val style = getAttackStyle(currentWeapon)
        var baseSpeed = 4
        if (currentWeapon.attackSpeed != -1) {
            baseSpeed = currentWeapon.attackSpeed!!
        } else {
            val definitions = ItemDefinitions.getItemDefinitions(attacker.equipment.weaponId);
            baseSpeed = definitions.attackSpeed
        }
        return baseSpeed + style.attackSpeedModifier
    }

    override fun getHitDelay(): Int {
        val currentWeapon = getCurrentWeapon(attacker) // unarmed
        return currentWeapon.attackDelay?:0
    }

    override fun attack() {
        val currentWeapon = getCurrentWeapon(attacker)
        val currentWeaponId = getCurrentWeaponId(attacker)
        val attackStyle = getAttackStyle(currentWeapon)
        val attackBonusType = getAttackBonusType(currentWeapon)
        val combatContext = CombatContext(
            combat = this,
            attacker = attacker,
            defender = defender,
            weapon = currentWeapon,
            weaponId = currentWeaponId,
            attackStyle = attackStyle,
            attackBonusType = attackBonusType,
        )
        if (executeSpecialAttack(attacker, defender)) {
            return
        }
        if (executeEffect(combatContext))
            return
        attacker.animate(CombatAnimations.getAnimation(currentWeaponId, attackStyle, attacker.combatDefinitions.attackStyle))
        val hit = combatContext.meleeHit(delay = getHitDelay())
        if (attacker.developerMode) {
            attacker.message("[Melee Attack] -> " +
                "Weapon: ${currentWeapon.name}, " +
                "WeaponStyle: ${attackStyle.name}, " +
                "WeaponBonusStyle: ${attackBonusType.name}, " +
                "WeaponType: ${currentWeapon.weaponStyle.name}, " +
                "MaxHit: ${hit[0].maxHit}, " +
                "Hit: ${hit[0].damage}")
        }
    }

    override fun delayHits(vararg hits: PendingHit) {
        val currentWeapon = getCurrentWeapon(attacker)
        val currentWeaponId = getCurrentWeaponId(attacker)
        val attackStyle = getAttackStyle(currentWeapon)
        var totalDamage = 0
        for (pending in hits) {
            val hit = pending.hit
            val target = pending.target
            PrayerEffectHandler.handleOffensiveEffects(attacker, target, hit)
            PrayerEffectHandler.handleProtectionEffects(attacker, target, hit)
            SoakDamage.handleAbsorb(attacker, target, hit)
            attacker.chargeManager.processOutgoingHit()
            if (target is Player) {//handling this onHit for magic & range
                target.animate(CombatAnimations.getBlockAnimation(target));
                target.chargeManager.processIncommingHit()
            }
            totalDamage += min(hit.damage, target.hitpoints)
            scheduleHit(pending.delay) {
                target.applyHit(hit)
                onHit(attacker, target, hit)
            }
        }
        attackStyle.xpMode.distributeXp(attacker, attackStyle, totalDamage);
    }

    override fun onHit(attacker: Player, defender: Entity, hit: Hit) {
        super.onHit(attacker, defender, hit)
    }

    override fun onStop(interrupted: Boolean) {
    }
}
