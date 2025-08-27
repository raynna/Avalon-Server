package com.rs.kotlin.game.player.combat.magic

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.prayer.PrayerEffectHandler
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.damage.SoakDamage
import com.rs.kotlin.game.player.combat.magic.special.GreaterRunicStaff
import com.rs.kotlin.game.player.combat.magic.special.PolyporeStaff
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
            val requiredStaffIds = currentSpell.staff?.ids ?: emptyList()
            if (requiredStaffIds.isNotEmpty() && weaponId !in requiredStaffIds) {
                attacker.message("You don't have the correct staff to cast ${currentSpell.name}.")
                return false
            }
        }
        return true
    }

    override fun getAttackSpeed(): Int {
        val baseSpeed = when (attacker.combatDefinitions.getSpellBook()) {
            192 -> 5
            else -> 5
        }
        return baseSpeed
    }

    override fun getHitDelay(): Int {
        val distance = Utils.getDistance(attacker, defender)
        return max(1, 1 + (1 + distance) / 3)
    }

    override fun getAttackDistance(): Int {
        return 8
    }

    override fun attack() {
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
            val hasRunicStaff = GreaterRunicStaff.hasWeapon(attacker);
            if (hasRunicStaff && GreaterRunicStaff.getSpellId(attacker) != -1) {
                spellId = GreaterRunicStaff.getSpellId(attacker)
                currentSpell = when (attacker.combatDefinitions.getSpellBook()) {
                    AncientMagicks.id -> AncientMagicks.getSpell(spellId)
                    ModernMagicks.id -> ModernMagicks.getSpell(spellId)
                    else -> null
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
        val spellId = attacker.combatDefinitions.spellId
        val currentSpell = when (attacker.combatDefinitions.getSpellBook()) {
            AncientMagicks.id -> AncientMagicks.getSpell(spellId)
            ModernMagicks.id -> ModernMagicks.getSpell(spellId)
            else -> null
        }
        if (hit.damage == 0) {
            defender.gfx(SPLASH_GRAPHIC)
            return
        }
        if (currentSpell != null) {
            if (currentSpell.endGraphic.id != -1 && currentSpell.projectileId == -1 && currentSpell.projectileIds.isEmpty()) {
                defender.gfx(currentSpell.endGraphic)
            }
        }
    }

    override fun onStop(interrupted: Boolean) {
    }

    private fun handleModernMagic(attacker: Player, defender: Entity, spell: Spell, manual: Boolean) {
        val hit = registerHit(attacker, defender, combatType = CombatType.MAGIC, spellId = spell.id)
        val splash = hit.damage == 0
        val endGraphic = if (!splash) spell.endGraphic else Graphics(SPLASH_GRAPHIC, 100)
        if (hit.damage > 0 && spell.bind != -1) {
            if (!defender.isFreezeImmune) {
                defender.freeze(spell.bind)
            } else {
                attacker.message("That ${if (defender is NPC) "npc" else "player"} is already affected by this spell.")
                return
            }
        }
        if (!SpellHandler.checkAndRemoveRunes(attacker, spell))
            return;
        spell.animationId.takeIf { it != -1 }?.let { attacker.animate(it) }
        spell.graphicId.takeIf { it.id != -1 }?.let { attacker.gfx(it) }
        if (spell.projectileIds.isNotEmpty()) {
            val heightDifferences = listOf(10, 0, -10)
            spell.projectileIds.zip(heightDifferences).forEach { (projectileId, heightDiff) ->
                ProjectileManager.send(
                    spell.projectileType,
                    projectileId,
                    heightOffset = heightDiff,
                    attacker = attacker,
                    defender = defender,
                    hitGraphic = endGraphic
                )
            }
        }
        if (spell.projectileId != -1) {
            if (endGraphic.id != -1) {
                ProjectileManager.send(
                    spell.projectileType,
                    spell.projectileId,
                    attacker = attacker,
                    defender = defender,
                    hitGraphic = endGraphic
                )
            } else {
                ProjectileManager.send(spell.projectileType, spell.projectileId, attacker, defender)
            }
        }
        if (hit.damage > 0 && spell.bind != -1) {
            defender.addFreezeDelay(spell.bind, true)
        }
        delayHits(PendingHit(hit, defender, getHitDelay()))
        if (manual) {
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    attacker.newActionManager.forceStop()
                    attacker.resetWalkSteps()
                    attacker.setNextFaceEntity(null)
                }
            })
        }
        if (attacker.isDeveloperMode)
        attacker.message(
            "Magic Attack -> " +
                    "Spell: ${spell.name}, " +
                    "SpellType: ${spell.type}, " +
                    "BaseDamage: ${spell.damage}, " +
                    "MaxHit: ${hit.maxHit}, " +
                    "Hit: ${hit.damage}"
        )
    }

    private fun handleAncientMagic(attacker: Player, defender: Entity, spell: Spell, manual: Boolean) {
        if (!SpellHandler.checkAndRemoveRunes(attacker, spell))
            return
        val hit = registerHit(attacker, defender, combatType = CombatType.MAGIC, spellId = spell.id)
        spell.animationId.takeIf { it != -1 }?.let { attacker.animate(it) }
        spell.graphicId.takeIf { it.id != -1 }?.let { attacker.gfx(it) }
        val splash = hit.damage == 0
        var endGraphic = if (!splash) spell.endGraphic else Graphics(SPLASH_GRAPHIC, 100)
        if (hit.damage > 0 && spell.bind != -1) {
            if (!defender.isFreezeImmune) {
                defender.addFreezeDelay(spell.bind, false)
            } else {
                if (spell.name.contains("ice barrage", ignoreCase = true)) {
                    endGraphic = Graphics(1677, 100)
                }
            }
        }
        if (spell.projectileId != -1) {
            if (spell.endGraphic.id != -1) {
                ProjectileManager.send(
                    spell.projectileType,
                    spell.projectileId,
                    attacker = attacker,
                    defender = defender,
                    hitGraphic = endGraphic,
                )
            } else {
                ProjectileManager.send(spell.projectileType, spell.projectileId, attacker, defender)
            }
        }
        delayHits(PendingHit(hit, defender, getHitDelay()))
        if (manual) {
            attacker.combatDefinitions.resetSpells(false)
            attacker.newActionManager.forceStop()
            attacker.resetWalkSteps()
            attacker.setNextFaceEntity(null)
        }
        if (attacker.isDeveloperMode)
        attacker.message(
            "Magic Attack -> " +
                    "Spell: ${spell.name.toString()}, " +
                    "SpellType: ${spell.type}, " +
                    "BaseDamage: ${spell.damage}, " +
                    "MaxHit: ${hit.maxHit}, " +
                    "Hit: ${hit.damage}"
        )
    }

    private fun addMagicExperience(totalDamage: Int) {
        var spellId = attacker.combatDefinitions.spellId
        val manual = isManualCast(spellId);
        if (manual) {
            attacker.combatDefinitions.resetSpells(false)
            spellId -= MIN_SPELL_ID;
        }
        val currentSpell = when (attacker.combatDefinitions.getSpellBook()) {
            AncientMagicks.id -> AncientMagicks.getSpell(spellId)
            ModernMagicks.id -> ModernMagicks.getSpell(spellId)
            else -> null
        }
        val isOneXpPerHit = attacker.toggles("ONEXPPERHIT", false)
        val isOneXHits = attacker.toggles("ONEXHITS", false)
        if (isOneXpPerHit) {
            val xp = if (isOneXHits) ceil(totalDamage / 10.0) else totalDamage
            attacker.skills.addXpDelayed(Skills.HITPOINTS, xp.toDouble())
            return
        }
        val spellXp = currentSpell?.xp?:0.0
        val baseXp = (totalDamage * 0.2)
        val combined = spellXp+baseXp
        if (attacker.getCombatDefinitions().isDefensiveCasting) {
            attacker.skills.addXpDelayed(Skills.DEFENCE, (totalDamage * 0.1))
            attacker.skills.addXpDelayed(Skills.MAGIC, (totalDamage * 0.133))
        } else {
            attacker.skills.addXpDelayed(Skills.MAGIC, combined)
        }
        attacker.skills.addXpDelayed(Skills.HITPOINTS, (totalDamage * 0.133))
    }

    private fun isManualCast(spellId: Int): Boolean {
        return spellId != NO_SPELL && spellId != DEFAULT_SPELL && spellId >= MIN_SPELL_ID
    }
}