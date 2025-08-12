package com.rs.kotlin.game.player.combat.melee

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.prayer.PrayerEffectHandler
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.special.CombatContext
import com.rs.kotlin.game.player.combat.special.meleeHit

object MeleeStyle : CombatStyle {


    private var currentWeapon: Weapon = StandardMelee.getDefaultWeapon()
    private var attackStyle: AttackStyle = AttackStyle.ACCURATE
    private var attackBonusType: AttackBonusType = AttackBonusType.CRUSH

    private lateinit var combatContext: CombatContext
    private lateinit var attacker: Player
    private lateinit var defender: Entity

    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        this.attacker = attacker;
        this.defender = defender
        val weaponId = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1
        val gloves = attacker.equipment.getItem(Equipment.SLOT_HANDS.toInt()) ?: null
        val hasGoliathGloves = attacker.combatDefinitions.hasGoliath(gloves)

        currentWeapon = when {
            weaponId != -1 && StandardMelee.getWeaponByItemId(weaponId) != null ->
                StandardMelee.getWeaponByItemId(weaponId)!!
            hasGoliathGloves ->
                StandardMelee.getGoliathWeapon()
            else ->
                StandardMelee.getDefaultWeapon() // unarmed
        }
        attackStyle = getAttackStyle()
        attackBonusType = getAttackBonusType()
        combatContext = CombatContext(
            combat = this,
            attacker = MeleeStyle.attacker,
            defender = MeleeStyle.defender,
            weapon = currentWeapon,
            attackStyle = attackStyle,

        )
        return true
    }

    fun getAttackStyle(): AttackStyle {
        val attackStyleId = attacker.combatDefinitions.attackStyle
        return currentWeapon.weaponStyle.styleSet.styleAt(attackStyleId)?: AttackStyle.ACCURATE
    }

    fun getAttackBonusType(): AttackBonusType {
        val attackStyleId = attacker.combatDefinitions.attackStyle
        return currentWeapon.weaponStyle.styleSet.bonusAt(attackStyleId)?: AttackBonusType.CRUSH
    }

    override fun getAttackDistance(): Int {
        return currentWeapon.attackRange ?: 0
    }

    override fun getAttackSpeed(): Int {
        val baseSpeed = currentWeapon.attackSpeed ?: 4
        val style = getAttackStyle()
        return baseSpeed + style.attackSpeedModifier
    }
    override fun getHitDelay(): Int {
        return currentWeapon.attackDelay?:0
    }

    override fun attack() {
        if (attacker.combatDefinitions.isUsingSpecialAttack) {
            currentWeapon.special?.let { special ->
                val specialEnergy = attacker.combatDefinitions.specialAttackPercentage
                if (specialEnergy >= special.energyCost) {
                    val specialContext = combatContext.copy(usingSpecial = true)
                    special.execute(specialContext);
                    attacker.combatDefinitions.decreaseSpecialAttack(special.energyCost);
                    return
                } else {
                    attacker.message("You don't have enough special attack energy.")
                    attacker.combatDefinitions.switchUsingSpecialAttack()
                }
            }
        }
        currentWeapon.effect?.let { effect ->
            CombatAnimations.getAnimation(currentWeapon.itemId, attackStyle, attacker.combatDefinitions.attackStyle).let { attacker.animate(it) }
            effect.execute(combatContext)
            return
        }
        CombatAnimations.getAnimation(currentWeapon.itemId, attackStyle, attacker.combatDefinitions.attackStyle).let { attacker.animate(it) }
        val hit = combatContext.meleeHit()
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
