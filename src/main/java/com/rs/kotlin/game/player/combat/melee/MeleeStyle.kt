package com.rs.kotlin.game.player.combat.melee

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.item.Item
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.TickManager
import com.rs.java.game.player.prayer.PrayerEffectHandler
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.damage.SoakDamage
import com.rs.kotlin.game.player.combat.special.CombatContext
import com.rs.kotlin.game.player.combat.special.meleeHit
import kotlin.math.min

class MeleeStyle(val attacker: Player, val defender: Entity) : CombatStyle {

    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        if (defender is NPC) {
            if (defender.name.contains("aviansie", ignoreCase = true) || defender.id == NPC.getNpc("npc.kree_arra_lv580")) {
                attacker.message("You can't use melee on flying enemies.")
                return false
            }
        }
        return true
    }

    override fun getAttackDistance(): Int {
        val currentWeapon = Weapon.getCurrentWeapon(attacker) // unarmed
        return currentWeapon.attackRange ?: 0
    }

    override fun getAttackSpeed(): Int {
        val currentWeapon = Weapon.getCurrentWeapon(attacker)
        val style = WeaponStyle.getWeaponStyle(attacker)
        var baseSpeed = 4
        if (currentWeapon.attackSpeed != -1) {
            baseSpeed = currentWeapon.attackSpeed!!
        } else {
            val definitions = ItemDefinitions.getItemDefinitions(attacker.equipment.weaponId);
            baseSpeed = definitions.attackSpeed
        }
        if (attacker.equipment.weaponId == Item.getId("item.auspicious_katana"))
            baseSpeed = 4;
        var finalSpeed = baseSpeed + style.attackSpeedModifier

        if (attacker.tickManager.isActive(TickManager.TickKeys.MIASMIC_EFFECT)) {
            finalSpeed = (finalSpeed * 2).coerceAtLeast(1)
        }
        return finalSpeed
    }

    override fun getHitDelay(): Int {
        val currentWeapon = Weapon.getCurrentWeapon(attacker)
        return currentWeapon.attackDelay?:0
    }

    override fun attack() {
        val currentWeapon = Weapon.getCurrentWeapon(attacker)
        val currentWeaponId = Weapon.getCurrentWeaponId(attacker)
        val attackStyle = WeaponStyle.getWeaponStyle(attacker)
        val attackBonusType = WeaponStyle.getAttackBonusType(attacker)
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
        attacker.animate(CombatAnimations.getAnimation(attacker))
        attacker.playSound(CombatAnimations.getSound(attacker), 1)
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
        val attackStyle = WeaponStyle.getWeaponStyle(attacker)
        var totalDamage = 0
        for (pending in hits) {
            val target = pending.target
            super.outgoingHit(attacker, target, pending)
            val hit = pending.hit
            if (target is Player) {
                if (target.hasStaffOfLightActive()) {
                    hit.damage = (hit.damage * 0.5).toInt()
                    target.gfx(2320)
                }
            }
            totalDamage += min(hit.damage, target.hitpoints)
            scheduleHit(pending.delay) {
                target.applyHit(hit)
                pending.onApply?.invoke()
                onHit(attacker, target, hit)
            }
        }
        attackStyle.xpMode.distributeXp(attacker, defender, attackStyle, totalDamage);
    }

    override fun onStop(interrupted: Boolean) {
    }
}
