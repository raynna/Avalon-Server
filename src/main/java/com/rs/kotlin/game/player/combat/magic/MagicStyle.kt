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
import com.rs.kotlin.game.world.projectile.ProjectileManager

object MagicStyle : CombatStyle {

    private const val NO_SPELL = 65535
    private const val DEFAULT_SPELL = 65536
    private const val MIN_SPELL_ID = 256

    private const val SPLASH_GRAPHIC = 85


    private var currentSpell: Spell? = null

    private lateinit var attacker: Player
    private lateinit var defender: Entity

    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        this.attacker = attacker
        this.defender = defender
        val combatDefs = attacker.combatDefinitions
        val spellId = combatDefs.spellId
        currentSpell = when (combatDefs.getSpellBook()) {
            AncientMagicks.id -> AncientMagicks.getSpell(spellId)
            ModernMagicks.id -> ModernMagicks.getSpell(spellId)
            else -> null
        }
        if (currentSpell != null) {
            val weaponId = attacker.equipment.weaponId
            if (currentSpell?.staff != null && weaponId !in currentSpell?.staff!!.ids) {
                attacker.message("You don't have the correct staff to cast ${currentSpell?.name}.")
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
        val delay = when {
            distance <= 1 -> 1
            distance <= 3 -> 2
            distance <= 6 -> 3
            else -> 4
        }
        return delay.coerceAtLeast(1) // Minimum 1 tick delay
    }

    override fun getAttackDistance(): Int {
        return 8
    }

    override fun attack() {
        val combatDefs = attacker.combatDefinitions
        var spellId = combatDefs.spellId
        val manual = isManualCast(spellId)
        if (manual) {
            attacker.message("manual cast: reset tempCast")
            combatDefs.resetSpells(false)
            spellId -= 256
        }
        currentSpell = when (combatDefs.getSpellBook()) {
            AncientMagicks.id -> AncientMagicks.getSpell(spellId)
            ModernMagicks.id -> ModernMagicks.getSpell(spellId)
            else -> null
        }
        attacker.message("${ModernMagicks.id} vs ${combatDefs.getSpellBook()}")
        currentSpell?.let { spell ->
            when (combatDefs.getSpellBook()) {
                AncientMagicks.id -> handleAncientMagic(attacker, defender, spell, manual)
                ModernMagicks.id -> handleModernMagic(attacker, defender, spell, manual)
                else -> attacker.message("Unknown spellbook")
            }
        } ?: attacker.message("Invalid spell ID: $spellId")
    }

    override fun delayHits(vararg hits: PendingHit) {
        var totalDamage = 0
        for (pending in hits) {
            val hit = pending.hit
            PrayerEffectHandler.handleOffensiveEffects(attacker, defender, hit)
            PrayerEffectHandler.handleProtectionEffects(attacker, defender, hit)
            totalDamage += hit.damage
            scheduleHit(pending.delay) {
                if (hit.damage > 0)
                    defender.applyHit(hit)
                onHit(hit)
            }
        }
        addMagicExperience(totalDamage)
    }

    override fun onHit(hit: Hit) {
        if (hit.damage == 0) {
            defender.gfx(SPLASH_GRAPHIC)
            return
        }
        if (currentSpell != null) {
            if (currentSpell?.endGraphic?.id != -1 && currentSpell?.projectileId == -1 && currentSpell?.projectileIds!!.isEmpty()) {
                defender.gfx(currentSpell!!.endGraphic)
            }
        }
    }

    override fun onStop(interrupted: Boolean) {
    }

    private fun handleModernMagic(attacker: Player, defender: Entity, spell: Spell, manual: Boolean) {
        attacker.message("Casting modern spell: ${spell.name}")
        val hit = registerHit(attacker, defender, combatType = CombatType.MAGIC, spellId = spell.id)
        val splash = hit.damage == 0
        var endGraphic = if (!splash) spell.endGraphic else Graphics(SPLASH_GRAPHIC, 100)
        if (hit.damage > 0 && spell.bind != -1) {
            if (!defender.isFreezeImmune) {
                defender.freeze(spell.bind)
            } else {
                attacker.message("That ${if (defender is NPC) "npc" else "player"} is already affected by this spell.")
                return
            }
        }
        spell.animationId.takeIf { it != -1 }?.let { attacker.animate(it) }
        spell.graphicId.takeIf { it.id != -1 }?.let { attacker.gfx(it) }
        if (spell.projectileIds.isNotEmpty()) {
            val heightDifferences = listOf(10, 0, -10)
            spell.projectileIds.zip(heightDifferences).forEach { (projectileId, heightDiff) ->
                ProjectileManager.sendWithHeightAndHitGraphic(
                    spell.projectileType,
                    projectileId,
                    heightDiff,
                    attacker,
                    defender,
                    endGraphic
                )
            }
        }
        if (spell.projectileId != -1) {
            if (endGraphic.id != -1) {
                ProjectileManager.sendWithHitGraphic(
                    spell.projectileType,
                    spell.projectileId,
                    attacker,
                    defender,
                    endGraphic
                )
            } else {
                ProjectileManager.send(spell.projectileType, spell.projectileId, attacker, defender)
            }
        }
        if (hit.damage > 0 && spell.bind != -1) {
            defender.addFreezeDelay(spell.bind, true)
        }
        delayHits(PendingHit(hit, getHitDelay()))
        if (manual) {
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    attacker.newActionManager.forceStop()
                    attacker.resetWalkSteps()
                    attacker.setNextFaceEntity(null)
                }
            })
        }
        attacker.message(
            "Magic Attack -> " +
                    "Spell: ${spell.name.toString()}, " +
                    "SpellType: ${spell.type}, " +
                    "BaseDamage: ${spell.damage}, " +
                    "MaxHit: ${hit.maxHit}, " +
                    "Hit: ${hit.damage}"
        )
    }

    private fun handleAncientMagic(attacker: Player, defender: Entity, spell: Spell, manual: Boolean) {
        attacker.message("Casting ancient spell: ${spell.name}")
        val hit = registerHit(attacker, defender, combatType = CombatType.MAGIC, spellId = spell.id)
        spell.animationId.takeIf { it != -1 }?.let { attacker.animate(it) }
        spell.graphicId.takeIf { it.id != -1 }?.let { attacker.gfx(it) }
        val splash = hit.damage == 0
        var endGraphic = if (!splash) spell.endGraphic else Graphics(SPLASH_GRAPHIC, 100)
        if (hit.damage > 0 && spell.bind != -1) {
            if (!defender.isFreezeImmune) {
                defender.freeze(spell.bind)
            } else {
                if (spell.name.contains("ice barrage", ignoreCase = true)) {
                    endGraphic = Graphics(1677, 100)
                }
            }
        }
        if (spell.projectileId != -1) {
            if (spell.endGraphic.id != -1) {
                ProjectileManager.sendWithHitGraphic(
                    spell.projectileType,
                    spell.projectileId,
                    attacker,
                    defender,
                    endGraphic,
                )
            } else {
                ProjectileManager.send(spell.projectileType, spell.projectileId, attacker, defender)
            }
        }
        delayHits(PendingHit(hit, getHitDelay()))
        if (manual) {
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    attacker.newActionManager.forceStop()
                    attacker.resetWalkSteps()
                    attacker.setNextFaceEntity(null)
                }
            })
        }
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
        val spellXp = currentSpell?.xp
        val baseXp = (totalDamage * 0.3)
        val combined = baseXp.plus(spellXp!!)
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