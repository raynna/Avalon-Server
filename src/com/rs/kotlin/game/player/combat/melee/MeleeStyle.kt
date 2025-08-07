package com.rs.kotlin.game.player.combat.melee

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.*

object MeleeStyle : CombatStyle {

    private var currentWeapon: Weapon? = null;

    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        val weaponId = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1
        currentWeapon = StandardMelee.getWeaponByItemId(weaponId) ?: StandardMelee.getDefaultWeapon()
        return true
    }

    fun getAttackStyle(attacker: Player): AttackStyle {
        val style = AttackStyle.fromOrdinal(attacker.combatDefinitions.attackStyle, currentWeapon!!.weaponStyle)
        return style
    }

    override fun getAttackDistance(attacker: Player): Int {
        return currentWeapon?.attackRange ?: 0
    }

    override fun getAttackDelay(attacker: Player): Int {
        val baseSpeed = currentWeapon?.attackSpeed ?: 4
        val style = AttackStyle.fromOrdinal(attacker.combatDefinitions.attackStyle, currentWeapon!!.weaponStyle)
        return baseSpeed + style.attackSpeedModifier
    }
    override fun applyHit(attacker: Player, defender: Entity, hit: Hit) {
        val attackDelay = currentWeapon!!.attackDelay ?: 0
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                defender.applyHit(hit);
                onHit(attacker, defender);
            }
        }, attackDelay)
    }

    override fun attack(attacker: Player, defender: Entity) {
        val attackStyle = getAttackStyle(attacker);
        CombatAnimations.getAnimation(currentWeapon!!.itemId, attackStyle)?.let { attacker.animate(it) }
        val hitSuccess = CombatCalculations.calculateMeleeAccuracy(attacker, defender, currentWeapon!!, attackStyle)
        val hit = CombatCalculations.calculateMeleeMaxHit(attacker, attackStyle)
        if (!hitSuccess) hit.damage = 0
        applyHit(attacker, defender, hit)
        /*attacker.message("Melee Attack -> " +
                "Weapon: ${currentWeapon?.name}, " +
                "WeaponStyle: ${attackStyle.name}, " +
                "WeaponBonusStyle: ${currentWeapon?.weaponStyle?.getAttackBonusType(attackStyle)}, " +
                "WeaponType: ${currentWeapon?.weaponStyle?.name}, " +
                "Style: ${AttackStyle.fromOrdinal(attacker.combatDefinitions.attackStyle, currentWeapon!!.weaponStyle).name}, " +
                "MaxHit: ${hit.maxHit}, " +
                "Hit: ${hit.damage}")*/
    }

    override fun onHit(attacker: Player, defender: Entity) {
        //target.playGraphic(Graphic.BLOOD_SPLASH)
    }

    override fun onStop(attacker: Player?, defender: Entity?, interrupted: Boolean) {
        // Maybe clear facing or animation
    }
}
