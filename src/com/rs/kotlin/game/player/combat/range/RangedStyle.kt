package com.rs.kotlin.game.player.combat.range

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.World
import com.rs.java.game.item.Item
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.AttackStyle
import com.rs.kotlin.game.player.combat.CombatCalculations
import com.rs.kotlin.game.player.combat.CombatStyle
import com.rs.kotlin.game.player.combat.WeaponStyle
import com.rs.kotlin.game.player.combat.melee.MeleeStyle
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager

object RangedStyle : CombatStyle {
    private var currentWeapon: RangedWeapon? = null
    private var currentAmmo: RangedAmmo? = null

    override fun onHit(attacker: Player, defender: Entity) {}

    override fun onStop(attacker: Player?, defender: Entity?, interrupted: Boolean) {}

    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        currentWeapon = RangeData.getWeaponByItemId(attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1)
        currentAmmo = RangeData.getAmmoByItemId(attacker.equipment.items[Equipment.SLOT_ARROWS.toInt()]?.id ?: -1)

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

        if (currentAmmo == null) {
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

        if (weaponAmmoType != ammoType) {
            attacker.message("You can't use $ammoName with a $weaponName.")
            return false
        }

        return true
    }

    fun getAttackStyle(attacker: Player): AttackStyle {
        val style = AttackStyle.fromOrdinal(attacker.combatDefinitions.attackStyle, currentWeapon!!.weaponStyle)
        return style
    }

    override fun getAttackDelay(attacker: Player): Int {
        val baseSpeed = currentWeapon?.attackSpeed ?: 4
        val style = AttackStyle.fromOrdinal(attacker.combatDefinitions.attackStyle, currentWeapon!!.weaponStyle)
        return baseSpeed + style.attackSpeedModifier
    }

    override fun getAttackDistance(attacker: Player): Int {
        return currentWeapon?.attackRange ?: 5
    }

    override fun applyHit(attacker: Player, defender: Entity, hit: Hit) {
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                defender.applyHit(hit)
                if (hit.damage > 0) {
                    handleSpecialEffects(attacker, defender)
                }
            }
        }, getRangedHitDelay(attacker, defender))
    }

    override fun attack(attacker: Player, defender: Entity) {
        if (!consumeAmmo(attacker)) {
            attacker.message("You've run out of ammunition!")
            return
        }

        // Play attack animation
        currentWeapon!!.animationId?.let { attacker.animate(it) }
        if (currentAmmo!!.startGfx != -1) {
            attacker.gfx(currentAmmo!!.startGfx, 100)
        }

        // Calculate hit
        val attackStyle = getAttackStyle(attacker)
        val hitSuccess =
            CombatCalculations.calculateRangedAccuracy(attacker, defender, currentWeapon!!, attackStyle)
        val hit = CombatCalculations.calculateRangedMaxHit(attacker, attackStyle)

        // Add XP
        attacker.skills.addXp(Skills.RANGE, (hit.damage * 0.4).toDouble())

        // Send projectile
        sendProjectile(attacker, defender, hitSuccess)

        // Apply hit with delay
        applyHit(attacker, defender, hit)

        // Drop ammo if needed
        if (hitSuccess && currentAmmo!!.dropOnGround) {
            dropAmmoOnGround(attacker, defender)
        }
        // Debug log for ranged attack context
        attacker.message("Ranged Attack -> " +
                "Weapon: ${currentWeapon?.name}, " +
                "WeaponType: ${currentWeapon?.weaponStyle?.name}, " +
                "AmmoType: ${currentAmmo?.ammoType?.name}, " +
                "Ammo: ${currentAmmo?.name}, " +
                "Style: ${AttackStyle.fromOrdinal(attacker.combatDefinitions.attackStyle, currentWeapon!!.weaponStyle).name}, " +
                "MaxHit: ${hit.maxHit}, " +
                "Hit: ${hit.damage}")

    }

    private fun consumeAmmo(player: Player): Boolean {
        val ammo = player.equipment.getItems()[Equipment.SLOT_ARROWS.toInt()] ?: return false

        // Special case for chinchompas which are the weapon
        if (ammo.id == 11959) {
            player.equipment.deleteItem(ammo.id, 1)
            return true
        }

        // Don't consume bolts with certain crossbows
        if (currentAmmo!!.ammoType == AmmoType.BOLT &&
            player.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id in listOf(9183, 9185)) {
            return true
        }

        // Don't consume ammo if wearing Ava's device with chance
        if (player.equipment.items[Equipment.SLOT_CAPE.toInt()]?.id in listOf(10498, 10499)) {
            if (Utils.random(4) != 0) { // 75% chance to save ammo
                return true
            }
        }

        player.equipment.deleteItem(ammo.id, 1)
        return true
    }

    private fun sendProjectile(attacker: Player, defender: Entity, hitSuccess: Boolean) {
        val projectileId = if (hitSuccess) currentAmmo!!.projectileId else -1
        val projectileType = when (currentAmmo!!.ammoType) {
            AmmoType.ARROW -> Projectile.ARROW
            AmmoType.BOLT -> Projectile.BOLT
            AmmoType.DART -> Projectile.DART
            AmmoType.KNIFE -> Projectile.THROWING_KNIFE
            AmmoType.JAVELIN -> Projectile.JAVELIN
            AmmoType.CHINCHOMPA -> Projectile.CHINCHOMPA
            AmmoType.THROWNAXE -> Projectile.THROWING_KNIFE
        }

        if (currentWeapon!!.weaponStyle == WeaponStyle.CHINCHOMPA) {
            //TODO ProjectileManager.sendMulti(projectileType, projectileId, attacker, defender)
        } else {
            ProjectileManager.send(
                projectileType,
                projectileId,
                attacker,
                defender
            )
        }
    }

    private fun dropAmmoOnGround(attacker: Player, defender: Entity) {
        if (!Utils.roll(1, 3))
            World.updateGroundItem(Item(currentAmmo!!.itemId, 1), defender.tile, attacker);
    }

    private fun handleSpecialEffects(attacker: Player, defender: Entity) {
        currentAmmo?.specialEffect?.let { effect: SpecialEffect ->
            if (Utils.randomDouble() < effect.chance) {
                when (effect.type) {
                    EffectType.DRAGONFIRE -> {//TODO DRAGONFIRE
                        val damage = effect.damage + Utils.random(5)
                        defender.applyHit(Hit(attacker, damage, HitLook.REGULAR_DAMAGE))
                    }
                }
            }
        }
    }

    private fun getRangedHitDelay(attacker: Player, defender: Entity): Int {
        val distance = Utils.getDistance(attacker, defender)
        return when {
            distance <= 2 -> 1//TODO UNSURE OF CORRECT VALUES
            distance <= 4 -> 2
                else -> 3
        }
    }
}