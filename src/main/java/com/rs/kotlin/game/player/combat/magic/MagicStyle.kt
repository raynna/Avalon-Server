package com.rs.kotlin.game.player.combat.magic

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.game.WorldTile
import com.rs.java.game.item.Item
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager
import com.rs.java.game.player.actions.combat.PlayerCombat
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.magic.special.GreaterRunicStaffWeapon
import com.rs.kotlin.game.player.combat.magic.special.PolyporeStaff
import com.rs.kotlin.game.player.combat.range.RangeData
import com.rs.kotlin.game.player.combat.range.RangedWeapon
import com.rs.kotlin.game.player.combat.range.StandardRanged
import com.rs.kotlin.game.player.combat.special.CombatContext
import com.rs.kotlin.game.player.combat.special.getMultiAttackTargets
import com.rs.kotlin.game.world.projectile.ProjectileManager
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class MagicStyle(val attacker: Player, val defender: Entity) : CombatStyle {

    private val NO_SPELL = 65535
    private val DEFAULT_SPELL = 65536
    private val MIN_SPELL_ID = 256

    private val SPLASH_GRAPHIC = 85


    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        val combatDefs = attacker.combatDefinitions
        var spellId = combatDefs.spellId
        val spellBookId = combatDefs.getSpellBook()
        if (spellId >= MIN_SPELL_ID) {
            spellId -= MIN_SPELL_ID;
        }
        val currentSpell = when (spellBookId) {
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
                        "You don't have the correct staff to cast ${currentSpell.name}."
                    )
                    return false
                }
            }
            currentSpell.itemRequirement?.let { req ->
                if (req.anyOf.isNotEmpty() &&
                    req.anyOf.none { attacker.equipment.containsOneItem(it) }
                ) {
                    attacker.message(
                        "You don't have the required item to cast ${currentSpell.name}."
                    )
                    return false
                }
                if (req.allOf.isNotEmpty() &&
                    !req.allOf.all { attacker.equipment.containsOneItem(it) }
                ) {
                    attacker.message(
                        "You don't have all required items to cast ${currentSpell.name}."
                    )
                    return false
                }
            }
            if (spellBookId == ModernMagicks.id && currentSpell.id == 86) {
                if (defender is NPC) {
                    attacker.message("You can't cast teleport block on a npc.");
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
            if (!SpellHandler.checkAndRemoveRunes(attacker, currentSpell))
                return false
        }
        return true
    }

    private fun getCurrentWeapon(): RangedWeapon {
        val weaponId = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1
        return RangeData.getWeaponByItemId(weaponId)?: StandardRanged.getDefaultWeapon()
    }


    private fun getCurrentWeaponId(attacker: Player): Int {
        val weaponId = attacker.equipment.items[Equipment.SLOT_WEAPON.toInt()]?.id ?: -1
        return when {
            weaponId != -1 -> weaponId
            else -> -1
        }
    }

    override fun getAttackSpeed(): Int {
        return when (attacker.combatDefinitions.getSpellBook()) {
            Spellbook.MODERN_ID -> {
                val currentSpell = attacker.temporaryAttributes()["CASTED_SPELL"] as? Spell
                val weaponId = getCurrentWeaponId(attacker)

                when {
                    weaponId == Item.getId("item.armadyl_battlestaff") && currentSpell?.id == 99 -> 4
                    weaponId == Item.getId("item.harmonised_nightmare_staff") -> 4
                    else -> 5
                }
            }
            else -> 5
        }
    }


    override fun getHitDelay(): Int {
        val distance = Utils.getDistance(attacker, defender)
        return max(1, 1 + (1 + distance) / 3)
    }
    override fun getAttackDistance(): Int {
        return 8
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

        var currentSpell = when (attacker.combatDefinitions.getSpellBook()) {
            AncientMagicks.id -> AncientMagicks.getSpell(spellId)
            ModernMagicks.id -> ModernMagicks.getSpell(spellId)
            else -> null
        }

        if (currentSpell == null) {
            val hasPolypore = PolyporeStaff.hasWeapon(attacker)
            if (hasPolypore) {
                PolyporeStaff.cast(this, attacker, defender);
                return
            }
            val hasRunicStaff = GreaterRunicStaffWeapon.hasWeapon(attacker);
            if (hasRunicStaff && GreaterRunicStaffWeapon.getSpellId(attacker) != -1) {
                spellId = GreaterRunicStaffWeapon.getSpellId(attacker)
                currentSpell = when (attacker.combatDefinitions.getSpellBook()) {
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
                if (hit.damage > 0)
                    target.applyHit(hit)
                onHit(attacker, target, hit)
            }
        }
        addMagicExperience(totalDamage)
    }

    override fun onHit(attacker: Player, defender: Entity, hit: Hit) {
        super.onHit(attacker, defender, hit)
        var currentSpell = attacker.temporaryAttributes()["CASTED_SPELL"] as? Spell

        if (defender is Player && GreaterRunicStaffWeapon.hasWeapon(defender)) {
            val spellId = GreaterRunicStaffWeapon.getSpellId(attacker)
            currentSpell = when (attacker.combatDefinitions.getSpellBook()) {
                AncientMagicks.id -> AncientMagicks.getSpell(spellId)
                ModernMagicks.id -> ModernMagicks.getSpell(spellId)
                else -> null
            }
            if (Utils.roll(1,15)) {
                hit.damage = -2
                attacker.message("Your ${currentSpell?.name} is resisted by ${defender.displayName}'s greater runic staff.")
                defender.message("Your greater runic staff resists ${attacker.displayName}'s ${currentSpell?.name}.")
            }
            else if (Utils.roll(1, 4)) {
                defender.runicStaff.chargeCombat(1)
                attacker.message("${defender.displayName} absorbs your ${currentSpell?.name} into their greater runic staff!")
                defender.message("You absorb ${attacker.displayName}'s ${currentSpell?.name} into your greater runic staff.")
            }
        }
        if (currentSpell != null) {
            if (hit.damage == 0) {
                defender.gfx(SPLASH_GRAPHIC, 100)
                defender.playSound(227, 1)
                return;
            }
            if (currentSpell.name.contains("teleport block", ignoreCase = true)) {
                val blockTime = defender.temporaryAttributes().remove("TELEBLOCK_QUEUE") as? Int
                if (blockTime != null && blockTime != -1) {
                    defender.teleportBlock(blockTime)
                }
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
        manual: Boolean
    ) {
        val hit = registerHit(attacker, defender, combatType = CombatType.MAGIC, spellId = spell.id)
        val splash = hit.damage == 0
        val endGraphic = if (!splash) spell.endGraphic else Graphics(-1)

        if (hit.damage > 0 && spell.bind != -1) {
            if (!defender.isFreezeImmune) {
                defender.freeze(spell.bind)
            } else {
                attacker.message(
                    "That ${if (defender is NPC) "npc" else "player"} is already affected by this spell."
                )
                return
            }
        }

        spell.animationId.takeIf { it != -1 }?.let { attacker.animate(it) }
        spell.graphicId.takeIf { it.id != -1 }?.let { attacker.gfx(it) }
        spell.attackSound.takeIf { it != -1 }?.let { attacker.playSound(it, 1) }

        var impactTicks = 1

        if (spell.projectileIds.isNotEmpty()) {
            val heightDifferences = listOf(10, 0, -10)
            spell.projectileIds.zip(heightDifferences).forEach { (projectileId, heightDiff) ->
                impactTicks = ProjectileManager.send(
                    projectile = spell.projectileType,
                    gfxId = projectileId,
                    attacker = attacker,
                    defender = defender,
                    heightOffset = heightDiff,
                    hitGraphic = endGraphic
                )
            }
        }
        else if (spell.projectileId != -1) {
            impactTicks =
                if (endGraphic.id != -1) {
                    ProjectileManager.send(
                        projectile = spell.projectileType,
                        gfxId = spell.projectileId,
                        attacker = attacker,
                        defender = defender,
                        hitGraphic = endGraphic
                    )
                } else {
                    ProjectileManager.send(
                        projectile = spell.projectileType,
                        gfxId = spell.projectileId,
                        attacker = attacker,
                        defender = defender
                    )
                }
        }

        if (hit.damage > 0 && spell.bind != -1) {
            defender.addFreezeDelay(spell.bind, true)
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
                if (spell.chargeBoost) 1 else impactTicks + 1
            )
        )

        if (manual) {
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    attacker.newActionManager.forceStop()
                    attacker.resetWalkSteps()
                    attacker.setNextFaceEntity(null)
                }
            })
        }

        if (attacker.isDeveloperMode) {
            attacker.message(
                "Magic Attack -> " +
                        "Spell: ${spell.name}, " +
                        "SpellType: ${spell.type}, " +
                        "BaseDamage: ${spell.damage}, " +
                        "MaxHit: ${hit.maxHit}, " +
                        "Hit: ${hit.damage}"
            )
        }
    }


    private fun handleAncientMagic(attacker: Player, defender: Entity, spell: Spell, manual: Boolean) {
        val combatContext = CombatContext(
            combat = this,
            attacker = attacker,
            defender = defender,
            weapon = getCurrentWeapon(),
            weaponId = getCurrentWeaponId(attacker),
            attackStyle = AttackStyle.ACCURATE,//just fillers
            attackBonusType = AttackBonusType.CRUSH//just fillers
        )
        val targets = if (spell.multi) {
            combatContext.getMultiAttackTargets(
                maxDistance = 1,
                maxTargets = 9
            )
        } else {
            listOf(defender)
        }
        spell.animationId.takeIf { it != -1 }?.let { attacker.animate(it) }
        spell.graphicId.takeIf { it.id != -1 }?.let { attacker.gfx(it) }
        spell.attackSound.takeIf { it != -1 }?.let { attacker.playSound(it, 1) }
        for (t in targets) {
            val hit = registerHit(attacker, t, combatType = CombatType.MAGIC, spellId = spell.id)
            val splash = hit.damage == 0
            var endGraphic = if (!splash) spell.endGraphic else Graphics(-1)

            if (hit.damage > 0) {
                if (spell.id == 23 && (t.isFreezeImmune || t.isFrozen || t.size >= 2)) {
                    endGraphic = Graphics(1677, 100)
                }
                if (spell.miasmic) {
                    if (!t.tickManager.isActive(TickManager.TickKeys.MIASMIC_EFFECT))
                        t.tickManager.addSeconds(TickManager.TickKeys.MIASMIC_EFFECT, 12)//just to make sure all of them give 12 seconds, 48 is way to op
                }
                if (spell.bind != -1 && !t.isFreezeImmune) {
                    t.addFreezeDelay(spell.bind, false)
                }
                if (spell.element == ElementType.Blood) {
                    attacker.heal(hit.damage / 5)
                }
            }

            if (spell.projectileId != -1) {
                if (spell.id == 23) {
                    t.gfxAnchoredSouthWest(spell.projectileId, 100);
                } else {
                    ProjectileManager.send(
                        spell.projectileType,
                        spell.projectileId,
                        attacker = attacker,
                        defender = t,
                        hitGraphic = endGraphic
                    )
                }
            }
            attacker.temporaryAttributes()["CASTED_SPELL"] = spell
            delayHits(PendingHit(hit, t, getHitDelay()))
        }
        if (manual) {
            attacker.combatDefinitions.resetSpells(false)
            attacker.newActionManager.forceStop()
            attacker.resetWalkSteps()
            attacker.setNextFaceEntity(null)
        }
    }

    private fun addMagicExperience(totalDamage: Int) {
        var spellId = attacker.combatDefinitions.spellId
        val manual = isManualCast(spellId);
        if (manual) {
            attacker.combatDefinitions.resetSpells(false)
            spellId -= MIN_SPELL_ID
        }
        var currentSpell = when (attacker.combatDefinitions.getSpellBook()) {
            AncientMagicks.id -> AncientMagicks.getSpell(spellId)
            ModernMagicks.id -> ModernMagicks.getSpell(spellId)
            else -> null
        }
        val hasRunicStaff = GreaterRunicStaffWeapon.hasWeapon(attacker);
        if (hasRunicStaff && GreaterRunicStaffWeapon.getSpellId(attacker) != -1) {
            spellId = GreaterRunicStaffWeapon.getSpellId(attacker)
            currentSpell = when (attacker.combatDefinitions.getSpellBook()) {
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
        val spellXp = currentSpell?.xp?:0.0
        val baseXp = (totalDamage * 0.2)
        val combined = spellXp+baseXp
        if (attacker.getCombatDefinitions().isDefensiveCasting) {
            attacker.skills.addXp(Skills.DEFENCE, (totalDamage * 0.1))
            attacker.skills.addXp(Skills.MAGIC, (totalDamage * 0.133))
        } else {
            attacker.skills.addXp(Skills.MAGIC, combined)
        }
        attacker.skills.addXp(Skills.HITPOINTS, (totalDamage * 0.133))
    }

    private fun isManualCast(spellId: Int): Boolean {
        return spellId != NO_SPELL && spellId != DEFAULT_SPELL && spellId >= MIN_SPELL_ID
    }
}