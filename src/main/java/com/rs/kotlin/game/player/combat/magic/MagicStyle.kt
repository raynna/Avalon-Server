package com.rs.kotlin.game.player.combat.magic

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.game.item.Item
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.NewPoison
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.magic.special.GreaterRunicStaffWeapon
import com.rs.kotlin.game.player.combat.magic.special.PolyporeStaff
import com.rs.kotlin.game.player.combat.range.RangeData
import com.rs.kotlin.game.player.combat.range.RangedWeapon
import com.rs.kotlin.game.player.combat.range.StandardRanged
import com.rs.kotlin.game.player.combat.special.CombatContext
import com.rs.kotlin.game.player.combat.special.getMultiAttackTargets
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager
import com.rs.kotlin.game.world.projectile.ProjectileRegistry
import com.rs.kotlin.game.world.projectile.ProjectileRequest
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class MagicStyle(
    val attacker: Player,
    val defender: Entity,
) : CombatStyle {
    private val NO_SPELL = 65535
    private val DEFAULT_SPELL = 65536
    private val MIN_SPELL_ID = 256

    private val SPLASH_GRAPHIC = 85

    override fun canAttack(
        attacker: Player,
        defender: Entity,
    ): Boolean {
        val combatDefs = attacker.combatDefinitions
        var spellId = combatDefs.spellId
        val spellBookId = combatDefs.getSpellBook()
        if (spellId >= MIN_SPELL_ID) {
            spellId -= MIN_SPELL_ID
        }
        val currentSpell =
            when (spellBookId) {
                AncientMagicks.id -> AncientMagicks.getSpell(spellId)
                ModernMagicks.id -> ModernMagicks.getSpell(spellId)
                else -> null
            }

        if (currentSpell != null) {
            val weaponId = attacker.equipment.weaponId
            currentSpell.staff?.let { staffReq ->
                if (staffReq.anyOf.isNotEmpty() &&
                    attacker.equipment.weaponId !in staffReq.anyOf
                ) {
                    attacker.message(
                        "You don't have the correct staff to cast ${currentSpell.name}.",
                    )
                    return false
                }
            }
            currentSpell.itemRequirement?.let { req ->
                if (req.anyOf.isNotEmpty() &&
                    req.anyOf.none { attacker.equipment.containsOneItem(it) }
                ) {
                    attacker.message(
                        "You don't have the required item to cast ${currentSpell.name}.",
                    )
                    return false
                }
                if (req.allOf.isNotEmpty() &&
                    !req.allOf.all { attacker.equipment.containsOneItem(it) }
                ) {
                    attacker.message(
                        "You don't have all required items to cast ${currentSpell.name}.",
                    )
                    return false
                }
            }
            if (spellBookId == ModernMagicks.id && currentSpell.id == 86) {
                if (defender is NPC) {
                    attacker.message("You can't cast teleport block on a npc.")
                    return false
                }
                if (defender is Player) {
                    if (defender.isTeleportBlocked || defender.isTeleportBlockImmune) {
                        attacker.message("${defender.displayName} is already affected by this spell.")
                        return false
                    }
                }
            }
            if (spellBookId == ModernMagicks.id && currentSpell.bind != -1) {
                if (defender.isFrozen || defender.isFreezeImmune) {
                    attacker.message("Your target is already affected by this spell.")
                    return false
                }
            }
            if (!SpellHandler.checkAndRemoveRunes(attacker, currentSpell)) {
                return false
            }
        }
        return true
    }

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

    override fun getAttackSpeed(): Int =
        when (attacker.combatDefinitions.getSpellBook()) {
            Spellbook.MODERN_ID -> {
                val currentSpell = attacker.temporaryAttributes()["CASTED_SPELL"] as? Spell
                val weaponId = getCurrentWeaponId(attacker)

                when {
                    weaponId == Item.getId("item.armadyl_battlestaff") && currentSpell?.id == 99 -> 4
                    weaponId == Item.getId("item.harmonised_nightmare_staff") -> 4
                    else -> 5
                }
            }

            else -> {
                5
            }
        }

    override fun getHitDelay(): Int {
        val distance = Utils.getDistance(attacker, defender)
        return min(4, max(1, 1 + (1 + distance) / 3))
    }

    override fun getAttackDistance(): Int =
        when (attacker.combatDefinitions.spellId) {
            1000 -> 8
            else -> 10
        }

    override fun attack() {
        if (executeSpecialAttack(attacker, defender)) {
            return
        }
        var spellId = attacker.combatDefinitions.spellId
        val manual = isManualCast(spellId)
        if (manual) {
            spellId -= MIN_SPELL_ID
        }

        var currentSpell =
            when (attacker.combatDefinitions.getSpellBook()) {
                AncientMagicks.id -> AncientMagicks.getSpell(spellId)
                ModernMagicks.id -> ModernMagicks.getSpell(spellId)
                else -> null
            }

        if (currentSpell == null) {
            val hasPolypore = PolyporeStaff.hasWeapon(attacker)
            if (hasPolypore) {
                PolyporeStaff.cast(this, attacker, defender)
                return
            }
            val hasRunicStaff = GreaterRunicStaffWeapon.hasWeapon(attacker)
            if (hasRunicStaff && GreaterRunicStaffWeapon.getSpellId(attacker) != -1) {
                spellId = GreaterRunicStaffWeapon.getSpellId(attacker)
                currentSpell =
                    when (attacker.combatDefinitions.getSpellBook()) {
                        AncientMagicks.id -> AncientMagicks.getSpell(spellId)
                        ModernMagicks.id -> ModernMagicks.getSpell(spellId)
                        else -> null
                    }
                if (currentSpell == null) {
                    attacker.message("Your greater runic staff has a spell from another magic book.")
                    return
                }
            }
            if (currentSpell == null) {
                attacker.message("Invalid spell ID: $spellId")
                return
            }
            GreaterRunicStaffWeapon.consumeCharge(attacker, 1)
        }

        when (attacker.combatDefinitions.getSpellBook()) {
            AncientMagicks.id -> handleAncientMagic(attacker, defender, currentSpell, manual)
            ModernMagicks.id -> handleModernMagic(attacker, defender, currentSpell, manual)
            else -> attacker.message("Unknown spellbook")
        }
    }

    override fun delayHits(vararg hits: PendingHit) {
        var totalDamage = 0
        for (pending in hits) {
            val hit = pending.hit
            val target = pending.target
            super.outgoingHit(attacker, target, pending)
            totalDamage += min(hit.damage, target.hitpoints)
            scheduleHit(pending.delay) {
                if (hit.damage > 0) {
                    target.applyHit(hit)
                }
                pending.onApply?.invoke()
                onHit(attacker, target, hit)
            }
        }
        addMagicExperience(totalDamage)
    }

    override fun onHit(
        attacker: Player,
        defender: Entity,
        hit: Hit,
    ) {
        super.onHit(attacker, defender, hit)
        var currentSpell = attacker.temporaryAttributes().remove("CASTED_SPELL") as? Spell

        if (defender is Player && GreaterRunicStaffWeapon.hasWeapon(defender)) {
            val spellId = GreaterRunicStaffWeapon.getSpellId(attacker)
            currentSpell =
                when (attacker.combatDefinitions.getSpellBook()) {
                    AncientMagicks.id -> AncientMagicks.getSpell(spellId)
                    ModernMagicks.id -> ModernMagicks.getSpell(spellId)
                    else -> null
                }
            if (Utils.roll(1, 15)) {
                hit.damage = -2
                attacker.message("Your ${currentSpell?.name} is resisted by ${defender.displayName}'s greater runic staff.")
                defender.message("Your greater runic staff resists ${attacker.displayName}'s ${currentSpell?.name}.")
            } else if (Utils.roll(1, 4)) {
                defender.runicStaff.chargeCombat(1)
                attacker.message("${defender.displayName} absorbs your ${currentSpell?.name} into their greater runic staff!")
                defender.message("You absorb ${attacker.displayName}'s ${currentSpell?.name} into your greater runic staff.")
            }
        }
        if (currentSpell != null) {
            if (hit.damage == 0) {
                defender.gfx(SPLASH_GRAPHIC, 100)
                defender.playSound(227, 1)
                return
            }
            if (currentSpell.name.contains("teleport block", ignoreCase = true)) {
                val blockTime = defender.temporaryAttributes().remove("TELEBLOCK_QUEUE") as? Int
                if (blockTime != null && blockTime != -1) {
                    defender.teleportBlock(blockTime)
                }
            }
            if (currentSpell.element == ElementType.Smoke) {
                defender.newPoison.roll(attacker, NewPoison.WeaponType.SMOKE_SPELL, -1)
            }
            if (currentSpell.endGraphic.id != -1 && currentSpell.projectileId == -1 && currentSpell.projectileIds.isEmpty()) {
                defender.gfx(currentSpell.endGraphic)
            }
            currentSpell.hitSound.takeIf { it != -1 }?.let { defender.playSound(it, 1) }
        }
    }

    override fun onStop(interrupted: Boolean) {
    }

    private fun handleModernMagic(
        attacker: Player,
        defender: Entity,
        spell: Spell,
        manual: Boolean,
    ) {
        val hit = hitRoll(CombatType.MAGIC, attacker, defender).spell(spell.id).roll()
        val splash = hit.damage == 0
        val endGraphic = if (!splash) spell.endGraphic else Graphics(-1)

        if (hit.damage > 0 && spell.bind != -1) {
            if (!defender.isFreezeImmune) {
                defender.addFreezeDelay(spell.bind, true)
                if (defender is Player) {
                    defender.setFrozenBy(attacker)
                }
            } else {
                attacker.message(
                    "That ${if (defender is NPC) "npc" else "player"} is already affected by this spell.",
                )
                return
            }
        }

        spell.animationId.takeIf { it != -1 }?.let { attacker.animate(it) }
        spell.graphicId.takeIf { it.id != -1 }?.let { attacker.gfx(it) }
        spell.attackSound.takeIf { it != -1 }?.let { attacker.playSound(it, 1) }

        var impactTicks = 0

        if (spell.projectileIds.isNotEmpty()) {
            val heightDifferences = listOf(10, 0, -10)
            spell.projectileIds.zip(heightDifferences).forEach { (projectileId, heightDiff) ->
                impactTicks =
                    ProjectileManager.send(
                        projectile = spell.projectileType,
                        gfxId = projectileId,
                        attacker = attacker,
                        defender = defender,
                        startHeightOffset = heightDiff,
                        hitGraphic = endGraphic,
                    )
            }
        } else if (spell.projectileId != -1) {
            impactTicks =
                if (endGraphic.id != -1) {
                    ProjectileManager.send(
                        projectile = spell.projectileType,
                        gfxId = spell.projectileId,
                        attacker = attacker,
                        defender = defender,
                        hitGraphic = endGraphic,
                    )
                } else {
                    ProjectileManager.send(
                        projectile = spell.projectileType,
                        gfxId = spell.projectileId,
                        attacker = attacker,
                        defender = defender,
                    )
                }
        }

        if (spell.id == 86 && hit.damage > 0) {
            var seconds = 300
            if (defender is Player) {
                if (defender.prayer.hasProtectFromMagic()) {
                    seconds /= 2
                }
                defender.temporaryAttributes()["TELEBLOCK_QUEUE"] = seconds
                attacker.message("Time to tb in seconds $seconds")
            }
        }

        attacker.temporaryAttributes()["CASTED_SPELL"] = spell

        delayHits(
            PendingHit(
                hit,
                defender,
                if (spell.chargeBoost) 1 else (impactTicks).coerceAtLeast(0),
            ),
        )
        handleSecondHit(attacker, defender, spell, impactTicks)

        if (manual) {
            WorldTasksManager.schedule(
                object : WorldTask() {
                    override fun run() {
                        attacker.actionManager.forceStop()
                        attacker.resetWalkSteps()
                        attacker.setNextFaceEntity(null)
                    }
                },
            )
        }

        if (attacker.isDeveloperMode) {
            attacker.message(
                "Magic Attack -> " +
                    "Spell: ${spell.name}, " +
                    "SpellType: ${spell.type}, " +
                    "BaseDamage: ${spell.damage}, " +
                    "MaxHit: ${hit.maxHit}, " +
                    "Hit: ${hit.damage}",
            )
        }
    }

    private fun supportsDualCast(attacker: Player): Boolean {
        val weaponId = attacker.equipment.weaponId
        return weaponId in
            listOf(
                Item.getId("item.staff_of_light"),
            )
    }

    private fun handleSecondHit(
        attacker: Player,
        defender: Entity,
        spell: Spell,
        firstImpactTicks: Int,
    ) {
        if (!supportsDualCast(attacker)) return

        /*
         * Roll second hit independently
         */
        val secondHit =
            hitRoll(CombatType.MAGIC, attacker, defender)
                .spell(spell.id)
                .roll()

        val splash = secondHit.damage == 0
        val endGraphic = if (!splash) spell.endGraphic else Graphics(-1)
        var secondImpact = 0
        val baseProjectile = spell.projectileType
        val baseType = ProjectileRegistry.get(baseProjectile) ?: return
        val slowType = baseType.copy(multiplier = baseType.multiplier * 2)
        if (spell.projectileIds.isNotEmpty()) {
            val heightDifferences = listOf(10, 0, -10)

            spell.projectileIds.zip(heightDifferences).forEach { (projectileId, heightDiff) ->

                val request =
                    ProjectileRequest(
                        projectile = baseProjectile,
                        gfxId = projectileId,
                        attacker = attacker,
                        defender = defender,
                        startHeightOffset = heightDiff,
                        hitGraphic = endGraphic,
                        projectileType = slowType,
                    )

                secondImpact = ProjectileManager.send(request)
            }
        } else if (spell.projectileId != -1) {
            val request =
                ProjectileRequest(
                    projectile = baseProjectile,
                    gfxId = spell.projectileId,
                    attacker = attacker,
                    defender = defender,
                    hitGraphic = endGraphic,
                    projectileType = slowType,
                )

            secondImpact = ProjectileManager.send(request)
        }

        /*
         * Delay slightly after first hit
         */
        val delay = (secondImpact).coerceAtLeast(0)

        delayHits(
            PendingHit(
                secondHit,
                defender,
                delay,
            ),
        )
    }

    private fun handleAncientMagic(
        attacker: Player,
        defender: Entity,
        spell: Spell,
        manual: Boolean,
    ) {
        val combatContext =
            CombatContext(
                combat = this,
                attacker = attacker,
                defender = defender,
                weapon = getCurrentWeapon(),
                weaponId = getCurrentWeaponId(attacker),
                attackStyle = AttackStyle.ACCURATE,
                attackBonusType = AttackBonusType.CRUSH,
            )
        val targets =
            if (spell.multi) {
                combatContext.getMultiAttackTargets(
                    maxDistance = 1,
                    maxTargets = 9,
                )
            } else {
                listOf(defender)
            }
        spell.animationId.takeIf { it != -1 }?.let { attacker.animate(it) }
        spell.graphicId.takeIf { it.id != -1 }?.let { attacker.gfx(it) }
        spell.attackSound.takeIf { it != -1 }?.let { attacker.playSound(it, 1) }
        for (t in targets) {
            val hit = hitRoll(CombatType.MAGIC, attacker, t).spell(spell.id).roll()
            val splash = hit.damage == 0
            var endGraphic = if (!splash) spell.endGraphic else Graphics(-1)
            if (hit.damage > 0) {
                if (spell.id == 23 && (defender.isFreezeImmune || defender.isFrozen || defender.size >= 2)) {
                    endGraphic = Graphics(1677, 100)
                }
                if (spell.miasmic) {
                    if (!t.tickManager.isActive(TickManager.TickKeys.MIASMIC_EFFECT)) {
                        t.tickManager.addSeconds(TickManager.TickKeys.MIASMIC_EFFECT, 12) // just to make sure all of them give 12 seconds, 48 is way to op
                    }
                }
                if (spell.bind != -1 && !t.isFreezeImmune) {
                    t.addFreezeDelay(spell.bind, false)
                    if (t is Player) {
                        t.setFrozenBy(attacker)
                    }
                }
                if (spell.element == ElementType.Blood) {
                    attacker.heal(hit.damage / 5)
                }
            }
            if (spell.id == 23) {
                ProjectileManager.send(Projectile.ICE_BARRAGE, 368, attacker, t)
            }
            if (spell.projectileId != -1) {
                ProjectileManager.send(
                    spell.projectileType,
                    spell.projectileId,
                    attacker = attacker,
                    defender = t,
                    hitGraphic = endGraphic,
                )
            }
            attacker.temporaryAttributes()["CASTED_SPELL"] = spell.copy(endGraphic = endGraphic)
            delayHits(PendingHit(hit, t, getHitDelay()))
        }
        if (manual) {
            attacker.combatDefinitions.resetSpells(false)
            attacker.actionManager.forceStop()
            attacker.resetWalkSteps()
            attacker.setNextFaceEntity(null)
        }
    }

    private fun addMagicExperience(totalDamage: Int) {
        var spellId = attacker.combatDefinitions.spellId
        val manual = isManualCast(spellId)
        if (manual) {
            attacker.combatDefinitions.resetSpells(false)
            spellId -= MIN_SPELL_ID
        }
        var currentSpell =
            when (attacker.combatDefinitions.getSpellBook()) {
                AncientMagicks.id -> AncientMagicks.getSpell(spellId)
                ModernMagicks.id -> ModernMagicks.getSpell(spellId)
                else -> null
            }
        val hasRunicStaff = GreaterRunicStaffWeapon.hasWeapon(attacker)
        if (hasRunicStaff && GreaterRunicStaffWeapon.getSpellId(attacker) != -1) {
            spellId = GreaterRunicStaffWeapon.getSpellId(attacker)
            currentSpell =
                when (attacker.combatDefinitions.getSpellBook()) {
                    AncientMagicks.id -> AncientMagicks.getSpell(spellId)
                    ModernMagicks.id -> ModernMagicks.getSpell(spellId)
                    else -> null
                }
            if (currentSpell == null) return
        }
        val isOneXpPerHit = attacker.toggles("ONEXPPERHIT", false)
        val isOneXHits = attacker.varsManager.getBitValue(1485) == 1
        if (isOneXpPerHit) {
            val xp = if (isOneXHits) ceil(totalDamage / 10.0) else totalDamage
            attacker.skills.addXp(Skills.HITPOINTS, xp.toDouble())
            return
        }
        val spellXp = currentSpell?.xp?.toInt() ?: 0
        val dmg = totalDamage

        var magicDmgXp: Int
        var defXp = 0
        var hpXp = (dmg * 4) / 30

        if (attacker.getCombatDefinitions().isDefensiveCasting) {
            magicDmgXp = (dmg * 4) / 30
            defXp = (dmg * 3) / 30
        } else {
            magicDmgXp = (dmg * 6) / 30
        }

        if (defender is Player) {
            val multiplier = XpMode.pvpXpMultiplier(defender.skills.combatLevel)
            magicDmgXp = XpMode.applyMultiplierFloor(magicDmgXp, multiplier)
            defXp = XpMode.applyMultiplierFloor(defXp, multiplier)
            hpXp = XpMode.applyMultiplierFloor(hpXp, multiplier)
        }

        attacker.skills.addXp(Skills.MAGIC, (spellXp + magicDmgXp).toDouble())

        if (defXp > 0) {
            attacker.skills.addXp(Skills.DEFENCE, defXp.toDouble())
        }

        attacker.skills.addXp(Skills.HITPOINTS, hpXp.toDouble())
    }

    private fun isManualCast(spellId: Int): Boolean = spellId != NO_SPELL && spellId != DEFAULT_SPELL && spellId >= MIN_SPELL_ID
}
