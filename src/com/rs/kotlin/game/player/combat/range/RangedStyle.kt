package com.rs.kotlin.game.player.combat.range

import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.World
import com.rs.java.game.item.Item
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.prayer.PrayerEffectHandler
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.NewPoison
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager

object RangedStyle : CombatStyle {

    private lateinit var attacker: Player
    private lateinit var defender: Entity

    private var currentWeapon: RangedWeapon? = null
    private var currentAmmo: RangedAmmo? = null
    private var attackStyle: AttackStyle = AttackStyle.ACCURATE

    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        this.attacker = attacker
        this.defender = defender
        currentWeapon = RangeData.getWeaponByItemId(attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1)
        currentAmmo = RangeData.getAmmoByItemId(attacker.equipment.items[Equipment.SLOT_ARROWS.toInt()]?.id ?: -1)
        attackStyle = getAttackStyle()
        val ammoTier = currentAmmo?.ammoTier
        val ammoType = currentAmmo?.ammoType
        val ammoId = currentAmmo?.itemId
        val ammoName = currentAmmo?.name
        val ammoLevelReq = currentAmmo?.levelRequired

        val weaponAmmoType = currentWeapon?.ammoType
        val allowedAmmos = currentWeapon?.allowedAmmoIds
        val maxTier = currentWeapon?.maxAmmoTier
        val weaponName = currentWeapon?.name
        if (currentWeapon == null) {
            attacker.message("You need a ranged weapon to attack.")
            return false
        }

        if (currentAmmo == null && weaponAmmoType != AmmoType.THROWING && weaponAmmoType != AmmoType.DART) {
            attacker.message("You don't have any ammunition equipped.")
            return false
        }
        if (allowedAmmos != null) {
            if (!allowedAmmos.contains(ammoId)) {
                attacker.message("You cannot use $ammoName with a $weaponName.")
                return false
            }
        } else if (maxTier != null) {
            if (ammoTier == null || !maxTier.canUse(ammoTier)) {
                attacker.message("You cannot use $ammoName with a $weaponName.")
                return false
            }
        }

        if (ammoLevelReq != null) {
            if (ammoLevelReq > attacker.skills.getLevel(Skills.RANGE)) {
                attacker.message("You need a Ranged level of $ammoLevelReq to use ${ammoName}.")
                return false
            }
        }
        if (weaponAmmoType != AmmoType.THROWING && weaponAmmoType != AmmoType.DART) {
            if (weaponAmmoType != ammoType) {
                attacker.message("You can't use $ammoName with a $weaponName.")
                return false
            }
        }

        return true
    }

    fun getAttackStyle(): AttackStyle {
        val style = AttackStyle.fromOrdinal(attacker.combatDefinitions.attackStyle, currentWeapon!!.weaponStyle)
        return style
    }

    override fun getAttackSpeed(): Int {
        val baseSpeed = currentWeapon?.attackSpeed ?: 4
        val style = AttackStyle.fromOrdinal(attacker.combatDefinitions.attackStyle, currentWeapon!!.weaponStyle)
        return baseSpeed + style.attackSpeedModifier
    }

    override fun getAttackDistance(): Int {
        return currentWeapon?.attackRange ?: 5
    }

    override fun attack() {
        val attackStyle = getAttackStyle()
        val hit = registerHit(attacker, defender, CombatType.RANGED, weapon = currentWeapon!!, attackStyle = attackStyle)
        if (attacker.combatDefinitions.isUsingSpecialAttack) {
            currentWeapon?.specialAttack?.let { special ->
                val specialEnergy = attacker.combatDefinitions.specialAttackPercentage
                if (specialEnergy >= special.energyCost) {
                    val context = CombatContext(
                        combat = this,
                        attacker = attacker,
                        defender = defender,
                        weapon = currentWeapon!!,
                        attackStyle = getAttackStyle()
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
        CombatAnimations.getAnimation(currentWeapon!!.itemId, attackStyle)?.let { attacker.animate(it) }
        if (currentAmmo!!.startGfx != null) {
            attacker.gfx(currentAmmo?.startGfx)
        }
        sendProjectile()
        handleSpecialEffects()
        delayHits(PendingHit(hit, getHitDelay()))
        attacker.message("Ranged Attack -> " +
                "Weapon: ${currentWeapon?.name}, " +
                "WeaponType: ${currentWeapon?.weaponStyle?.name}, " +
                "AmmoType: ${currentAmmo?.ammoType?.name}, " +
                "Ammo: ${currentAmmo?.name}, " +
                "Style: ${AttackStyle.fromOrdinal(attacker.combatDefinitions.attackStyle, currentWeapon!!.weaponStyle).name}, " +
                "MaxHit: ${hit.maxHit}, " +
                "Hit: ${hit.damage}")

    }

    override fun onHit(hit: Hit) {
        if (currentAmmo != null) {
            if (currentAmmo?.endGfx != null) {
                defender.gfx(currentAmmo?.endGfx);
            }
            if (currentAmmo!!.dropOnGround) {
                dropAmmoOnGround()
            }
            //mainhand poison
            if (currentWeapon?.poisonSeverity != -1) {
                currentWeapon?.poisonSeverity?.let {
                    defender.newPoison.roll(attacker, NewPoison.WeaponType.RANGED, it)
                }
            }
            //ammopoison
            if (currentAmmo?.ammoType == currentWeapon?.ammoType) {
                if (currentAmmo?.poisonSeverity != -1) {
                    currentAmmo?.poisonSeverity?.let {
                        defender.newPoison.roll(attacker, NewPoison.WeaponType.RANGED, it
                        )
                    };
                }
            }
        }
    }

    override fun delayHits(vararg hits: PendingHit) {
        var totalDamage = 0
        for (pending in hits) {
            val hit = pending.hit
            PrayerEffectHandler.handleOffensiveEffects(attacker, defender, hit);
            PrayerEffectHandler.handleProtectionEffects(attacker, defender, hit);
            consumeAmmo()
            totalDamage += hit.damage;
            scheduleHit(pending.delay) {
                defender.applyHit(hit)
                onHit(hit)
            }
        }
        attackStyle.xpMode.distributeXp(attacker, totalDamage);
    }

    override fun onStop(interrupted: Boolean) {
    }

    private fun consumeAmmo(): Boolean {
        val weapon = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]
        val ammoItem = attacker.equipment.items[Equipment.SLOT_ARROWS.toInt()]
        val ammoType = currentWeapon?.ammoType ?: currentAmmo?.ammoType

        if (weapon?.id == 11959) {
            attacker.equipment.deleteItem(weapon.id, 1)
            return true
        }

        if (attacker.equipment.items[Equipment.SLOT_CAPE.toInt()]?.id in listOf(10498, 10499, 20068)) {
            if (Utils.roll(3,4)) {
                return true
            }
        }

        if (ammoType == AmmoType.THROWING || ammoType == AmmoType.DART) {
            if (weapon != null) {
                attacker.equipment.deleteItem(weapon.id, 1)
                return true
            }
        }

        if (ammoItem != null) {
            attacker.equipment.deleteItem(ammoItem.id, 1)
            return true
        }

        return false
    }


    private fun sendProjectile() {
        val projectileId = currentWeapon?.projectileId?: currentAmmo?.projectileId?:27//prioritize weapon first
        val type = currentWeapon?.ammoType ?: currentAmmo?.ammoType//prioritize weapon first
        val projectileType = when (type) {
            AmmoType.ARROW -> Projectile.ARROW
            AmmoType.BOLT -> Projectile.BOLT
            AmmoType.DART -> Projectile.DART
            AmmoType.THROWING -> Projectile.THROWING_KNIFE
            AmmoType.JAVELIN -> Projectile.JAVELIN
            AmmoType.CHINCHOMPA -> Projectile.CHINCHOMPA
            AmmoType.THROWNAXE -> Projectile.THROWING_KNIFE
            null -> Projectile.BOLT
        }
        if (projectileId != -1) {
            ProjectileManager.send(
                projectileType,
                projectileId,
                attacker, defender)
        }
    }

    private fun dropAmmoOnGround() {
        if (!Utils.roll(1, 3))
            World.updateGroundItem(Item(currentAmmo!!.itemId, 1), defender.tile, attacker);
    }

    private fun handleSpecialEffects() {
        currentAmmo?.specialEffect?.let { special ->
                val context = CombatContext(
                    combat = this,
                    attacker = attacker,
                    defender = defender,
                    weapon = currentWeapon!!,
                    attackStyle = attackStyle,
                    ammo = currentAmmo
                )
                special.execute(context)
        }
    }

    override fun getHitDelay(): Int {
        val distance = Utils.getDistance(attacker, defender)
        return when {
            distance <= 2 -> 1//TODO UNSURE OF CORRECT VALUES
            distance <= 4 -> 2
                else -> 3
        }
    }
}