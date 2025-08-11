package com.rs.kotlin.game.player.combat.melee

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.prayer.PrayerEffectHandler
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.special.SpecialContext

object MeleeStyle : CombatStyle {

    private var currentWeapon: Weapon? = null
    private var attackStyle: AttackStyle = AttackStyle.ACCURATE
    private var attackBonusType: AttackBonusType = AttackBonusType.CRUSH

    private lateinit var attacker: Player
    lateinit var defender: Entity

    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        this.attacker = attacker;
        this.defender = defender
        val weaponId = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1
        currentWeapon = StandardMelee.getWeaponByItemId(weaponId) ?: StandardMelee.getDefaultWeapon()
        attackStyle = getAttackStyle()
        attackBonusType = getAttackBonusType()
        if (defender is NPC) {
            attacker.message("Attacker: ${attacker.displayName}, Defender: ${defender.name}, weaponId: $weaponId, currentWeapon: ${currentWeapon!!.name}")
        }
        return true
    }

    fun getAttackStyle(): AttackStyle {
        val attackStyleId = attacker.combatDefinitions.attackStyle
        return currentWeapon?.weaponStyle?.styleSet?.styleAt(attackStyleId)?: AttackStyle.ACCURATE
    }

    fun getAttackBonusType(): AttackBonusType {
        val attackStyleId = attacker.combatDefinitions.attackStyle
        return currentWeapon?.weaponStyle?.styleSet?.bonusAt(attackStyleId)?: AttackBonusType.CRUSH
    }

    override fun getAttackDistance(): Int {
        return currentWeapon?.attackRange ?: 0
    }

    override fun getAttackSpeed(): Int {
        val baseSpeed = currentWeapon?.attackSpeed ?: 4
        val style = getAttackStyle()
        return baseSpeed + style.attackSpeedModifier
    }

    override fun getHitDelay(): Int {
        return currentWeapon!!.attackDelay?:0
    }

    override fun attack() {
        val hit = registerHit(attacker = attacker,  defender = defender, weapon = currentWeapon, combatType = CombatType.MELEE, attackStyle = attackStyle)
        if (attacker.combatDefinitions.isUsingSpecialAttack) {
            currentWeapon?.specialAttack?.let { special ->
                val specialEnergy = attacker.combatDefinitions.specialAttackPercentage
                if (specialEnergy >= special.energyCost) {
                    val context = SpecialContext(
                        combat = this,
                        attacker = attacker,
                        defender = defender,
                        weapon = currentWeapon!!,
                        attackStyle = attackStyle
                    )
                    special.execute(context)
                    attacker.combatDefinitions.decreaseSpecialAttack(special.energyCost);
                    return
                } else {
                    attacker.message("You don't have enough special attack energy.")
                    attacker.combatDefinitions.switchUsingSpecialAttack()
                }
            }
        }
        CombatAnimations.getAnimation(currentWeapon!!.itemId, attackStyle, attacker.combatDefinitions.attackStyle).let { attacker.animate(it) }
        delayHits(PendingHit(hit, 0))
        attacker.message("[Melee Attack] -> " +
                "Weapon: ${currentWeapon?.name}, " +
                "WeaponStyle: ${attackStyle.name}, " +
                "WeaponBonusStyle: ${attackBonusType.name}, " +
                "WeaponType: ${currentWeapon?.weaponStyle?.name}, " +
                "MaxHit: ${hit.maxHit}, " +
                "Hit: ${hit.damage}")
    }

    override fun delayHits(vararg hits: PendingHit) {
        var totalDamage = 0
        for (pending in hits) {
            val hit = pending.hit
            PrayerEffectHandler.handleOffensiveEffects(attacker, defender, hit);
            PrayerEffectHandler.handleProtectionEffects(attacker, defender, hit);
            totalDamage += hit.damage;
            scheduleHit(pending.delay) {
                defender.applyHit(hit)
                onHit(hit)
            }
        }
        attackStyle.xpMode.distributeXp(attacker, attackStyle, totalDamage);
    }

    override fun onHit(hit: Hit) {
    }

    override fun onStop(interrupted: Boolean) {
    }
}
