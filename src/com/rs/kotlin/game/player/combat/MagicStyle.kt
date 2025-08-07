package com.rs.kotlin.game.player.combat

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.magic.*
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager

object MagicStyle : CombatStyle {
    private const val NO_SPELL = 65535
    private const val DEFAULT_SPELL = 65536
    private const val MIN_SPELL_ID = 256

    private const val SPLASH_GRAPHIC = 85

    private var currentSpell: Spell? = null

    override fun canAttack(attacker: Player, defender: Entity): Boolean {
        return true
    }

    override fun getAttackDelay(attacker: Player): Int = 5
    override fun getAttackDistance(attacker: Player): Int = 8

    override fun applyHit(attacker: Player, defender: Entity, hit: Hit) {
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                if (hit.damage == 0) {
                    defender.gfx(SPLASH_GRAPHIC, 100)
                    return
                }
                defender.applyHit(hit)
            }
        }, getMagicHitDelay(attacker, defender))
    }

    override fun attack(attacker: Player, defender: Entity) {
        val combatDefs = attacker.combatDefinitions
        var spellId = combatDefs.spellId
        attacker.message("spellId ${spellId}")
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

        logAttack(attacker)
        attacker.message("${ModernMagicks.id} vs ${combatDefs.getSpellBook()}")
        currentSpell?.let { spell ->
            when (combatDefs.getSpellBook()) {
                AncientMagicks.id -> handleAncientMagic(attacker, defender, spell, manual)
                ModernMagicks.id -> handleModernMagic(attacker, defender, spell, manual)
                else -> attacker.message("Unknown spellbook")
            }
        } ?: attacker.message("Invalid spell ID: $spellId")
    }

    override fun onHit(attacker: Player, defender: Entity) {}

    override fun onStop(attacker: Player?, defender: Entity?, interrupted: Boolean) {}

    private fun isManualCast(spellId: Int): Boolean {
        return spellId != NO_SPELL && spellId != DEFAULT_SPELL && spellId >= MIN_SPELL_ID
    }

    private fun logAttack(attacker: Player) {
        attacker.message("Attacking with magic, spellbook ${attacker.combatDefinitions.spellBook}")
    }

    private fun handleAncientMagic(attacker: Player, defender: Entity, spell: Spell, manual: Boolean) {
        attacker.message("Casting ancient spell: ${spell.name}")
        val hitSuccess = CombatCalculations.calculateMagicAccuracy(attacker, defender)
        val maxHit = CombatCalculations.calculateMagicMaxHit(attacker, spell)
        val hit = Hit(attacker, if (hitSuccess) Utils.random(maxHit) else 0, HitLook.MAGIC_DAMAGE)
        var endGraphic = spell.endGraphicId
        attacker.skills.addXp(Skills.MAGIC, spell.xp + (hit.damage * 0.3))

        if (!hitSuccess) {
            endGraphic = -1 // to make sure hitgraphic doesnt apply if its a splash
        }

        if (hit.damage > 0 && spell.bind != -1) {
            defender.setFreezeDelay(spell.bind.toLong())
        }

        spell.animationId.takeIf { it != -1 }?.let { attacker.animate(it) }
        spell.graphicId.takeIf { it != -1 }?.let { attacker.gfx(it) }

        if (spell.projectileId != -1) {
            if (spell.endGraphicId != -1) {
                ProjectileManager.sendWithHitGraphic(
                    Projectile.ELEMENTAL_SPELL,
                    spell.projectileId,
                    attacker,
                    defender,
                    endGraphic,
                    100
                )
            } else {
                ProjectileManager.send(Projectile.ELEMENTAL_SPELL, spell.projectileId, attacker, defender)
            }
        }

        applyHit(attacker, defender, hit)

        if (manual) {
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    attacker.newActionManager.forceStop()
                    attacker.resetWalkSteps()
                    attacker.setNextFaceEntity(null)
                }
            })
        }
    }

    private fun handleModernMagic(attacker: Player, defender: Entity, spell: Spell, manual: Boolean) {
        attacker.message("Casting modern spell: ${spell.name}")
        val hitSuccess = CombatCalculations.calculateMagicAccuracy(attacker, defender)
        val maxHit = CombatCalculations.calculateMagicMaxHit(attacker, spell)
        val hit = Hit(attacker, if (hitSuccess) Utils.random(maxHit) else 0, HitLook.MAGIC_DAMAGE)
        var endGraphic = spell.endGraphicId
        attacker.skills.addXp(Skills.MAGIC, spell.xp + (hit.damage * 0.3))

        if (!hitSuccess) {
            endGraphic = -1
        }

        if (hit.damage > 0 && spell.bind != -1) {
            defender.setFreezeDelay(spell.bind.toLong())
        }

        spell.animationId.takeIf { it != -1 }?.let { attacker.animate(it) }
        spell.graphicId.takeIf { it != -1 }?.let { attacker.gfx(it) }

        if (spell.projectileIds.isNotEmpty()) {
            val heightDifferences = listOf(10, 0, -10)

            spell.projectileIds.zip(heightDifferences).forEach { (projectileId, heightDiff) ->
                ProjectileManager.sendWithHeightAndHitGraphic(
                    Projectile.ELEMENTAL_SPELL,
                    projectileId,
                    heightDiff,
                    attacker,
                    defender,
                    endGraphic
                )
            }
        }

        if (spell.projectileId != -1) {
            if (spell.endGraphicId != -1) {
                ProjectileManager.sendWithHitGraphic(
                    Projectile.ELEMENTAL_SPELL,
                    spell.projectileId,
                    attacker,
                    defender,
                    endGraphic,
                    100
                )
            } else {
                ProjectileManager.send(Projectile.ELEMENTAL_SPELL, spell.projectileId, attacker, defender)
            }
        }

        applyHit(attacker, defender, hit)

        if (manual) {
            WorldTasksManager.schedule(object : WorldTask() {
                override fun run() {
                    attacker.newActionManager.forceStop()
                    attacker.resetWalkSteps()
                    attacker.setNextFaceEntity(null)
                }
            })
        }
    }

    private fun getMagicHitDelay(player: Player, defender: Entity): Int {
        val spell = currentSpell ?: return 3
        val distance = Utils.getDistance(player, defender)
        return calculateMagicHitDelay(distance, spell)
    }

    private fun calculateMagicHitDelay(distance: Int, spell: Spell): Int {
        var delay = when {
            distance <= 1 -> 1    // Point-blank range
            distance <= 3 -> 2    // Short range
            distance <= 6 -> 3    // Medium range
            else -> 4             // Long range (max 10 squares in RS)
        }

        // Adjust for spell types
        delay += when (spell.type) {
            SpellType.Combat -> {
                when {
                    spell.name.contains("Bolt") -> 0   // Bolt spells are faster
                    spell.name.contains("Blitz") -> 1  // Blitz spells slightly slower
                    spell.name.contains("Barrage") -> 2 // Barrage spells slowest
                    else -> 0 // Default
                }
            }
            else -> 0 // Non-combat spells
        }

        return delay.coerceAtLeast(1) // Minimum 1 tick delay
    }
}