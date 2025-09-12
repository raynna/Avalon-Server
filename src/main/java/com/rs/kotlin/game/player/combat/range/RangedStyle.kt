package com.rs.kotlin.game.player.combat.range

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.World
import com.rs.java.game.item.Item
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.NewPoison
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.special.CombatContext
import com.rs.kotlin.game.player.combat.special.rangedHit
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager
import kotlin.math.min

class RangedStyle(val attacker: Player, val defender: Entity) : CombatStyle {

    private fun getCurrentWeapon(): RangedWeapon {
        val weaponId = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1
        return RangeData.getWeaponByItemId(weaponId) ?: StandardRanged.getDefaultWeapon()
    }


    private fun getCurrentWeaponId(attacker: Player): Int {
        val weaponId = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1
        return when {
            weaponId != -1 -> weaponId
            else -> -1
        }
    }

    private fun getCurrentAmmo(): RangedAmmo? {
        val ammoItem = attacker.equipment.items[Equipment.SLOT_ARROWS.toInt()]
        if (ammoItem != null) {
            return RangeData.getAmmoByItemId(ammoItem.id);
        }
        return null
    }


    private fun getAttackStyle(currentWeapon: RangedWeapon): AttackStyle {
        val styleId = attacker.combatDefinitions.attackStyle
        return currentWeapon.weaponStyle.styleSet.styleAt(styleId) ?: AttackStyle.RAPID
    }

    private fun getAttackBonusType(currentWeapon: RangedWeapon): AttackBonusType {
        val styleId = attacker.combatDefinitions.attackStyle
        return currentWeapon.weaponStyle.styleSet.bonusAt(styleId) ?: AttackBonusType.RANGE
    }

    override fun getAttackSpeed(): Int {
        val currentWeapon = getCurrentWeapon()
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

    override fun getAttackDistance(): Int {
        val currentWeapon = getCurrentWeapon()
        val baseRange = currentWeapon.attackRange ?: 5
        return when (getAttackStyle(currentWeapon)) {
            AttackStyle.LONGRANGE -> (baseRange + 2).coerceAtMost(10)
            AttackStyle.ACCURATE, AttackStyle.RAPID -> baseRange.coerceAtMost(10)
            else -> baseRange.coerceAtMost(10)
        }
    }


    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        val currentWeapon = getCurrentWeapon()
        val currentAmmo = getCurrentAmmo()
        val ammoId = attacker.getEquipment().ammoId
        val ammoTier = currentAmmo?.ammoTier
        val ammoType = currentAmmo?.ammoType
        val ammoName = currentAmmo?.name
        val ammoLevelReq = currentAmmo?.levelRequired

        val weaponAmmoType = currentWeapon.ammoType
        val allowedAmmos = currentWeapon.allowedAmmoIds
        val maxTier = currentWeapon.maxAmmoTier
        val weaponName = currentWeapon.name
        if (currentAmmo == null && weaponAmmoType != AmmoType.THROWING && weaponAmmoType != AmmoType.DART && weaponAmmoType != AmmoType.JAVELIN && weaponAmmoType != AmmoType.NONE) {
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
            if (ammoLevelReq > attacker.skills.getLevelForXp(Skills.RANGE)) {
                attacker.message("You need a Ranged level of $ammoLevelReq to use ${ammoName}.")
                return false
            }
        }
        if (!isThrowing(currentWeapon) && weaponAmmoType != AmmoType.NONE) {
            if (weaponAmmoType != ammoType) {
                attacker.message("You can't use $ammoName with a $weaponName.")
                return false
            }
        }

        return true
    }


    override fun attack() {
        val currentWeapon = getCurrentWeapon();
        val currentWeaponId = getCurrentWeaponId(attacker);
        val attackStyle = getAttackStyle(currentWeapon)
        val attackBonusType = getAttackBonusType(currentWeapon)
        val currentAmmo = getCurrentAmmo()
        val combatContext = CombatContext(
            combat = this,
            attacker = attacker,
            defender = defender,
            attackStyle = attackStyle,
            attackBonusType = attackBonusType,
            weapon = currentWeapon,
            weaponId = currentWeaponId,
            ammo = currentAmmo,
            usingSpecial = false
        )
        if (executeSpecialAttack(attacker, defender)) {
            return
        }
        if (executeEffect(combatContext.copy(usingSpecial = false)))
            return
        attacker.animate(
            CombatAnimations.getAnimation(
                currentWeaponId,
                attackStyle,
                attacker.combatDefinitions.attackStyle
            )
        )
        attacker.playSound(
            CombatAnimations.getSound(
                currentWeaponId,
                attackStyle,
                attacker.combatDefinitions.attackStyle
            ), 1
        )
        if (!isThrowing(currentWeapon) && currentWeapon.ammoType != AmmoType.NONE) {
            if (currentAmmo != null) {
                if (currentAmmo.startGfx != null) {
                    attacker.gfx(currentAmmo.startGfx)
                }
            }
        }
        sendProjectile()
        if (executeAmmoEffect(combatContext)) {
            return
        }
        val hit = combatContext.rangedHit(delay = getHitDelay())
        if (attacker.isDeveloperMode)
            attacker.message(
                "Ranged Attack -> " +
                        "Weapon: ${currentWeapon.name}, " +
                        "WeaponType: ${currentWeapon.weaponStyle.name}, " +
                        "AmmoType: ${currentAmmo?.ammoType?.name}, " +
                        "Ammo: ${currentAmmo?.name}, " +
                        "Style: ${attackStyle}, " +
                        "StyleBonus: ${attackBonusType}, " +
                        "MaxHit: ${hit[0].damage}, " +
                        "Hit: ${hit[0].damage}"
            )

    }

    override fun onHit(attacker: Player, defender: Entity, hit: Hit) {
        super.onHit(attacker, defender, hit)
        val currentAmmo = getCurrentAmmo()
        if (currentAmmo != null) {
            if (currentAmmo.endGfx != null) {
                defender.gfx(currentAmmo.endGfx);
            }
        }
        val weaponName = ItemDefinitions.getItemDefinitions(attacker.getEquipment().weaponId).getName()
        val ammoName = ItemDefinitions.getItemDefinitions(attacker.getEquipment().ammoId).getName()
        val poisonSeverity = maxOf(
            NewPoison.getPoisonSeverity(weaponName),
            NewPoison.getPoisonSeverity(ammoName)
        )

        if (poisonSeverity != -1) {
            defender.newPoison.roll(attacker, NewPoison.WeaponType.RANGED, poisonSeverity)
        }
    }

    override fun delayHits(vararg hits: PendingHit) {
        val currentWeapon = getCurrentWeapon()
        val attackStyle = getAttackStyle(currentWeapon)
        var totalDamage = 0
        for (pending in hits) {
            val hit = pending.hit
            val target = pending.target
            super.outgoingHit(attacker, target, pending)
            consumeAmmo()
            attacker.packets.sendSound(2702, 0, 1)
            totalDamage += min(hit.damage, target.hitpoints)
            scheduleHit(pending.delay) {
                target.applyHit(hit)
                onHit(attacker, target, hit)
            }
        }
        attackStyle.xpMode.distributeXp(attacker, attackStyle, totalDamage);
    }

    override fun onStop(interrupted: Boolean) {
    }

    private fun consumeAmmo(): Boolean {
        val currentWeapon = getCurrentWeapon()
        val currentAmmo = getCurrentAmmo()
        val weapon = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]
        val ammoItem = attacker.equipment.items[Equipment.SLOT_ARROWS.toInt()]
        val ammoType = currentWeapon.ammoType ?: currentAmmo?.ammoType

        if (weapon?.id == 11959) {
            attacker.equipment.deleteItem(weapon.id, 1)
            return true
        }

        if (attacker.equipment.items[Equipment.SLOT_CAPE.toInt()]?.id in listOf(10498, 10499, 20068)) {
            if (Utils.roll(3, 4)) {
                return true
            }
        }

        if (ammoType == AmmoType.THROWING || ammoType == AmmoType.DART || ammoType == AmmoType.JAVELIN || ammoType == AmmoType.THROWNAXE) {
            if (weapon != null) {
                attacker.equipment.deleteItem(weapon.id, 1)
                attacker.appearence.generateAppearenceData()
                return true
            }
        }

        if (currentWeapon.ammoType == AmmoType.NONE) {
            return true
        }

        if (ammoItem != null && currentAmmo != null) {
            if (currentWeapon.allowedAmmoIds != null && !currentWeapon.allowedAmmoIds.contains(ammoItem.id)) {
                return true
            }
            if (currentWeapon.maxAmmoTier != null && (currentAmmo.ammoTier == null || !currentWeapon.maxAmmoTier.canUse(
                    currentAmmo.ammoTier
                ))
            ) {
                return true
            }
            if (currentWeapon.ammoType != null && currentWeapon.ammoType != currentAmmo.ammoType) {
                return true
            }
            attacker.equipment.deleteItem(ammoItem.id, 1)
            attacker.appearence.generateAppearenceData()
            if (currentAmmo.dropOnGround) {
                dropAmmoOnGround()
            }
            return true
        }

        return false
    }


    private fun sendProjectile() {
        val currentWeapon = getCurrentWeapon()
        val currentAmmo = getCurrentAmmo()
        val projectileId = currentWeapon.projectileId ?: currentAmmo?.projectileId ?: 27//prioritize weapon first
        val type = currentWeapon.ammoType ?: currentAmmo?.ammoType//prioritize weapon first
        val projectileType = when (type) {
            AmmoType.ARROW -> Projectile.ARROW
            AmmoType.BOLT -> Projectile.BOLT
            AmmoType.DART -> Projectile.DART
            AmmoType.THROWING -> Projectile.THROWING_KNIFE
            AmmoType.JAVELIN -> Projectile.JAVELIN
            AmmoType.CHINCHOMPA -> Projectile.CHINCHOMPA
            AmmoType.THROWNAXE -> Projectile.THROWING_KNIFE
            AmmoType.NONE -> Projectile.ARROW
            null -> Projectile.BOLT
        }
        if (projectileId != -1) {
            ProjectileManager.send(
                projectileType,
                projectileId,
                attacker, defender
            )
        }
    }

    private fun isThrowing(weapon: RangedWeapon): Boolean {
        return weapon.ammoType == AmmoType.DART || weapon.ammoType == AmmoType.THROWING || weapon.ammoType == AmmoType.JAVELIN || weapon.ammoType == AmmoType.THROWNAXE
    }

    private fun dropAmmoOnGround() {
        val ammoId = attacker.getEquipment().ammoId
        if (!Utils.roll(1, 3))
            World.updateGroundItem(Item(ammoId, 1), defender.tile, attacker);
    }

    override fun getHitDelay(): Int {
        val distance = Utils.getDistance(attacker, defender)
        return 1 + (3 + distance) / 6
    }

}