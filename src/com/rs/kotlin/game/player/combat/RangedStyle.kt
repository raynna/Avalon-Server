package com.rs.kotlin.game.player.combat

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.World
import com.rs.java.game.WorldTile
import com.rs.java.game.item.Item
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.range.*
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager

object RangedStyle : CombatStyle {
    private var currentWeapon: RangedWeapon? = null
    private var currentAmmo: RangedAmmo? = null

    override fun onHit(attacker: Player, defender: Entity) {}

    override fun onStop(attacker: Player?, defender: Entity?, interrupted: Boolean) {}

    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        currentWeapon = RangeUtilities.getWeaponByItemId(attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1)
        currentAmmo = RangeUtilities.getAmmoByItemId(attacker.equipment.items[Equipment.SLOT_ARROWS.toInt()]?.id ?: -1)

        if (currentWeapon == null) {
            attacker.message("You need a ranged weapon to attack.")
            return false
        }

        if (currentAmmo == null) {
            attacker.message("You don't have any ammunition equipped.")
            return false
        }

        if (currentAmmo!!.levelRequired > attacker.skills.getLevel(Skills.RANGE)) {
            attacker.message("You need a Ranged level of ${currentAmmo!!.levelRequired} to use ${currentAmmo!!.name}.")
            return false
        }

        if (currentWeapon!!.ammoType != currentAmmo!!.ammoType) {
            attacker.message("You can't use ${currentAmmo!!.name} with a ${currentWeapon!!.name}.")
            return false
        }

        return true
    }

    override fun getAttackDelay(attacker: Player): Int {
        val baseSpeed = currentWeapon?.attackSpeed ?: 4
        val style = AttackStyle.fromOrdinal(attacker.combatDefinitions.attackStyle, currentWeapon!!.weaponType)
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
        attacker.animate(currentWeapon!!.animationId)
        if (currentAmmo!!.startGfx != -1) {
            attacker.gfx(currentAmmo!!.startGfx, 100)
        }

        // Calculate hit
        val attackStyle = attacker.combatDefinitions.attackStyle
        val hitSuccess = CombatCalculations.calculateRangedAccuracy(attacker, defender, currentWeapon!!.itemId, attackStyle)
        val maxHit = CombatCalculations.calculateRangedMaxHit(attacker, currentWeapon!!.itemId, currentAmmo!!.itemId)
        val hit = Hit(attacker, if (hitSuccess) Utils.random(maxHit) else 0, HitLook.RANGE_DAMAGE)

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
                "WeaponType: ${currentWeapon?.weaponType?.name}, " +
                "AmmoType: ${currentAmmo?.ammoType?.name}, " +
                "Ammo: ${currentAmmo?.name}, " +
                "Style: ${AttackStyle.fromOrdinal(attacker.combatDefinitions.attackStyle, currentWeapon!!.weaponType).name}, " +
                "MaxHit: $maxHit, " +
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

        if (currentWeapon!!.weaponType == WeaponStyle.CHINCHOMPA) {
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
                    EffectType.POISON -> defender.poison.makePoisoned(effect.damage)
                    EffectType.DRAGONFIRE -> {
                        val damage = effect.damage + Utils.random(5)
                        defender.applyHit(Hit(attacker, damage, HitLook.REGULAR_DAMAGE))
                    }
                    EffectType.LIFE_LEECH -> {
                        val heal = (defender.hitpoints * 0.25).toInt()
                        attacker.heal(heal)
                    }
                    EffectType.DEFENCE_REDUCTION -> {
                        //TODO defender.(Skills.DEFENCE, effect.duration ?: 5)
                    }
                    EffectType.BIND -> {
                        defender.setFreezeDelay(effect.duration?.toLong() ?: 5L)
                    }
                }
            }
        }
    }

    /**
     * var delay = when {
     *             distance <= 1 -> 1    // Point-blank range
     *             distance <= 3 -> 2    // Short range
     *             distance <= 6 -> 3    // Medium range
     *             else -> 4             // Long range (max 10 squares in RS)
     *         }
     */
    private fun getRangedHitDelay(attacker: Player, defender: Entity): Int {
        val distance = Utils.getDistance(attacker, defender)
        return when {
            distance <= 2 -> 1    // Point-blank range
            distance <= 4 -> 2    // Short range
                else -> 3
        }
    }
}