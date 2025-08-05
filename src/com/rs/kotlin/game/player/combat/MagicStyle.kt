package com.rs.kotlin.game.player.combat

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.Entity
import com.rs.java.game.Hit
import com.rs.java.game.Hit.HitLook
import com.rs.java.game.player.Player
import com.rs.java.game.player.actions.combat.AncientMagicks
import com.rs.java.game.player.actions.combat.ancientspells.RSAncientCombatSpells
import com.rs.java.game.player.actions.combat.ancientspells.RSAncientCombatSpells.AncientCombatSpellsStore
import com.rs.java.utils.Utils
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager

object MagicStyle : CombatStyle {
    override fun canAttack(attacker: Player, target: Entity): Boolean {
        return true
    }

    override fun getAttackDelay(attacker: Player): Int = 5
    override fun getAttackDistance(attacker: Player): Int = 8

    override fun applyHit(attacker: Player, defender: Entity, hit: Hit) {
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
               defender.applyHit(hit);
            }
        }, getMageDelay(attacker, defender))
    }

    override fun attack(attacker: Player, target: Entity) {
        val spellId = attacker.getCombatDefinitions().spellId;
        val autoCast = spellId != 65535 && spellId != 65536 && spellId >= 256
        if (!autoCast) {
            attacker.getCombatDefinitions().resetSpells(false);
        }
        attacker.message("attacking with magic, spellbook ${attacker.combatDefinitions.getSpellBook()}")
        if (attacker.combatDefinitions.getSpellBook() == AncientMagicks.SPELLBOOK_ID) {
            attacker.message("ancients");
            val spell = AncientCombatSpellsStore.getSpell(spellId)
            attacker.message("spell $spell")
            if (spell.animation != -1) {
                attacker.animate(spell.animation);
            }
            if (spell.projectileId != -1 && spell.endGfx != -1) {
                ProjectileManager.sendWithHitGraphic(Projectile.ARROW, spell.projectileId, attacker, target, spell.endGfx);
            }
            if (spell.spellType == RSAncientCombatSpells.ICE_SPELL) {
                attacker.playSound(171, 1);
            }
            val hit = Hit(target, spell.baseDamage, HitLook.MAGIC_DAMAGE);
            applyHit(attacker, target, hit)
        }
    }

    override fun onHit(attacker: Player, target: Entity) {
    }

    override fun onStop(attacker: Player?, target: Entity?, interrupted: Boolean) {
    }

    private fun getMageDelay(player: Player, target: Entity): Int {
        val spellId = player.getCombatDefinitions().spellId
        val spellBook = player.getCombatDefinitions().getSpellBook()
        val getDistance = Utils.getDistance(player, target)
        val mageDelay = if (spellBook != 192) {
            if (spellId in 36..39) {
                if (getDistance > 3) 2
                else 1
            } else {
                if (getDistance >= 4) 4
                else getDistance
            }
        } else {
            if (getDistance > 3) 3
            else getDistance
        }
        return mageDelay
    }
}
