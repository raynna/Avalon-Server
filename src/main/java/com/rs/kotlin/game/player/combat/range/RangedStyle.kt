package com.rs.kotlin.game.player.combat.range

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.game.item.Item
import com.rs.java.game.item.ground.GroundItems
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.NewPoison
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.range.special.SwiftGloves
import com.rs.kotlin.game.player.combat.special.CombatContext
import com.rs.kotlin.game.player.combat.special.rangedHit
import com.rs.kotlin.game.player.combat.special.rollRanged
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager
import kotlin.math.min

class RangedStyle(val attacker: Player, val defender: Entity) : CombatStyle {

    override fun getAttackSpeed(): Int {
        val currentWeapon = Weapon.getCurrentWeapon(attacker)
        val style = WeaponStyle.getWeaponStyle(attacker)

        var baseSpeed = if (currentWeapon.attackSpeed != -1) {
            currentWeapon.attackSpeed!!
        } else {
            val definitions = ItemDefinitions.getItemDefinitions(attacker.equipment.weaponId)
            definitions.attackSpeed
        }
        var finalSpeed = baseSpeed + style.attackSpeedModifier

        if (attacker.tickManager.isActive(TickManager.TickKeys.MIASMIC_EFFECT)) {
            finalSpeed = (finalSpeed * 2).coerceAtLeast(1)
        }
        return finalSpeed
    }

    override fun getAttackDistance(): Int {
        val currentWeapon = Weapon.getCurrentWeapon(attacker)
        val weaponStyle = WeaponStyle.getWeaponStyle(attacker);
        val baseRange = currentWeapon.attackRange ?: 5
        return when (weaponStyle) {
            AttackStyle.LONGRANGE -> (baseRange + 2).coerceAtMost(10)
            AttackStyle.ACCURATE, AttackStyle.RAPID -> baseRange.coerceAtMost(10)
            else -> baseRange.coerceAtMost(10)
        }
    }


    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        val currentWeapon = Weapon.getRangedWeapon(attacker) ?: return false
        val currentAmmo = RangeData.getCurrentAmmo(attacker)
        val ammoId = attacker.getEquipment().ammoId
        val ammoTier = currentAmmo?.ammoTier
        val ammoType = currentAmmo?.ammoType
        val ammoName = currentAmmo?.name
        val ammoLevelReq = currentAmmo?.levelRequired

        val weaponAmmoType = currentWeapon.ammoType
        val allowedAmmos = currentWeapon.allowedAmmoIds
        val maxTier = currentWeapon.maxAmmoTier
        val weaponName = currentWeapon.name
        if (currentAmmo == null && weaponAmmoType != AmmoType.THROWING && weaponAmmoType != AmmoType.DART && weaponAmmoType != AmmoType.JAVELIN && weaponAmmoType != AmmoType.MORRIGAN_THROWING && weaponAmmoType != AmmoType.NONE) {
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
        performRangedAttack()
    }


    private fun performRangedAttack() {
        val currentWeapon = Weapon.getRangedWeapon(attacker) ?: run {
            println("Combat error: trying to range without a ranged weapon")
            return
        }
        val currentWeaponId = Weapon.getCurrentWeaponId(attacker)
        val attackStyle = WeaponStyle.getWeaponStyle(attacker)
        val attackBonusType = WeaponStyle.getAttackBonusType(attacker)
        val currentAmmo =  RangeData.getCurrentAmmo(attacker)

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
            consumeAmmo()
            return
        }
        if (executeEffect(combatContext.copy(usingSpecial = false))) {
            consumeAmmo()
            return
        }

        attacker.animate(
            CombatUtils.getAnimation(
                currentWeaponId,
                attackStyle,
                attacker.combatDefinitions.attackStyle
            )
        )

        attacker.playSound(
            CombatUtils.getSound(
                currentWeaponId,
                attackStyle,
                attacker.combatDefinitions.attackStyle
            ), 1
        )
        if (currentWeapon.projectileId != null) {
            attacker.gfx(currentWeapon.startGfx)
        } else if ((currentAmmo != null) && (currentAmmo.startGfx != null)) {
           attacker.gfx(currentAmmo.startGfx)
        }
        val impactTicks = sendProjectile()
        if (executeAmmoEffect(combatContext)) return
        combatContext.rangedHit(delay = (impactTicks - 1).coerceAtLeast(0))
        consumeAmmo()
    }

    private fun applySwiftGlovesToPendingHits(hits: MutableList<PendingHit>) {
        if (!SwiftGloves.swiftGlovesProc(attacker)) return
        if (hits.isEmpty()) return

        val first = hits.first()
        val firstHit = first.hit

        val qualifies = firstHit.damage >= (firstHit.maxHit * 0.66).toInt() || firstHit.damage == 0
        if (!qualifies) return
        consumeAmmo()
        sendSwiftProjectile()
        val weapon = Weapon.getRangedWeapon(attacker) ?: return
        val ammo = RangeData.getCurrentAmmo(attacker)
        val swiftHit = CombatContext(
            combat = this,
            attacker = attacker,
            defender = defender,
            attackStyle = WeaponStyle.getWeaponStyle(attacker),
            attackBonusType = WeaponStyle.getAttackBonusType(attacker),
            weapon = weapon,
            weaponId = attacker.equipment.weaponId,
            ammo =  ammo,
            usingSpecial = false
        ).rollRanged()

        hits += PendingHit(
            hit = swiftHit,
            target = first.target,
            delay = first.delay
        )

        if (firstHit.isCriticalHit) {
            defender.lock(3)
            defender.gfx(Graphics(181, 0, 96))
        }

        val type = weapon.ammoType ?: ammo?.ammoType

        val message = when (type) {
            AmmoType.BOLT -> "You fired an extra bolt!"
            AmmoType.ARROW -> "You fired an extra arrow!"
            AmmoType.DART -> "You threw an extra dart!"
            AmmoType.JAVELIN -> "You hurled an extra javelin!"
            AmmoType.THROWING -> "You threw an extra knife!"
            else -> "You took an extra shot!"
        }

        attacker.packets.sendGameMessage(message)
    }

    override fun onHit(attacker: Player, defender: Entity, hit: Hit) {
        super.onHit(attacker, defender, hit)
        val currentAmmo = RangeData.getCurrentAmmo(attacker)
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
        val modified = hits.toMutableList()
        applySwiftGlovesToPendingHits(modified)

        val attackStyle = WeaponStyle.getWeaponStyle(attacker)
        var totalDamage = 0

        for (pending in modified) {
            val hit = pending.hit
            val target = pending.target

            super.outgoingHit(attacker, target, pending)

            totalDamage += min(hit.damage, target.hitpoints)

            scheduleHit(pending.delay) {
                target.applyHit(hit)
                pending.onApply?.invoke()
                onHit(attacker, target, hit)
            }
        }

        attackStyle.xpMode.distributeXp(attacker, defender, attackStyle, totalDamage)
    }


    override fun onStop(interrupted: Boolean) {
    }

    private fun consumeAmmo(): Boolean {
        val currentWeapon = Weapon.getRangedWeapon(attacker) ?: return false
        val currentAmmo = RangeData.getCurrentAmmo(attacker)
        val weapon = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]
        val ammoItem = attacker.equipment.items[Equipment.SLOT_ARROWS.toInt()]
        val ammoType = currentWeapon.ammoType ?: currentAmmo?.ammoType

        if (attacker.equipment.items[Equipment.SLOT_CAPE.toInt()]?.id in listOf(10498, 10499, 20068, 24635, 20769, 20771)) {
            if (Utils.roll(3, 4)) {
                return true
            }
        }

        if (ammoType == AmmoType.THROWING || ammoType == AmmoType.DART|| ammoType == AmmoType.MORRIGAN_THROWING || ammoType == AmmoType.JAVELIN || ammoType == AmmoType.THROWNAXE) {
            if (weapon != null) {
                attacker.equipment.decreaseItem(Equipment.SLOT_WEAPON.toInt(), 1)
                attacker.appearance.generateAppearenceData()
                if (!Utils.roll(1, 3))
                    GroundItems.updateGroundItem(Item(weapon.id, 1), defender.tile, attacker);
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
            attacker.equipment.decreaseItem(Equipment.SLOT_ARROWS.toInt(), 1)
            attacker.appearance.generateAppearenceData()
            if (currentAmmo.dropOnGround) {
                dropAmmoOnGround()
            }
            return true
        }

        return false
    }

    fun sendSwiftProjectile() {
        sendProjectile(heightOffset = 5)
    }

    private fun sendProjectile(heightOffset: Int = 0): Int {
        val currentWeapon = Weapon.getRangedWeapon(attacker) ?: return -1
        val currentAmmo = RangeData.getCurrentAmmo(attacker)
        val projectileId = currentWeapon.projectileId ?: currentAmmo?.projectileId ?: 27
        val type = currentWeapon.ammoType ?: currentAmmo?.ammoType

        val projectileType = when (type) {
            AmmoType.ARROW -> Projectile.ARROW
            AmmoType.BOLT -> Projectile.BOLT
            AmmoType.DART -> Projectile.DART
            AmmoType.MORRIGAN_THROWING -> Projectile.MORRIGAN_THROWING_AXE
            AmmoType.THROWING -> Projectile.THROWING_KNIFE
            AmmoType.JAVELIN -> Projectile.JAVELIN
            AmmoType.CHINCHOMPA -> Projectile.CHINCHOMPA
            AmmoType.THROWNAXE -> Projectile.THROWING_KNIFE
            AmmoType.CANNON -> Projectile.HAND_CANNON
            else -> Projectile.ARROW
        }

        return ProjectileManager.send(
            projectileType,
            projectileId,
            attacker,
            defender,
            startHeightOffset = heightOffset
        ) {
            defender.animate(CombatUtils.getBlockAnimation(defender as Player))
        }
    }



    private fun isThrowing(weapon: RangedWeapon): Boolean {
        return weapon.ammoType == AmmoType.DART || weapon.ammoType == AmmoType.THROWING || weapon.ammoType == AmmoType.MORRIGAN_THROWING || weapon.ammoType == AmmoType.JAVELIN || weapon.ammoType == AmmoType.THROWNAXE
    }

    private fun dropAmmoOnGround() {
        val ammoId = attacker.getEquipment().ammoId
        if (!Utils.roll(1, 3))
            GroundItems.updateGroundItem(Item(ammoId, 1), defender.tile, attacker);
    }

    override fun getHitDelay(): Int {
        val distance = Utils.getDistance(attacker, defender)
        return (3 + distance) / 6
    }

}