package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class GeneralGraardorCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]{6260}; // Graardor ID
	}

	@Override
	public int attack(NPC npc, Entity target) {
		maybeShout(npc);

		// 50% chance ranged, 50% melee
		if (Utils.random(2) == 0) {
			performRangedAttack(npc);
		} else {
			performMeleeAttack(npc, target);
		}
		return npc.getAttackSpeed();
	}

	// --------------------------
	// Shouts
	// --------------------------
	private void maybeShout(NPC npc) {
		if (Utils.random(4) != 0) {
			return;
		}

		int roll = Utils.random(10);
		switch (roll) {
			case 0 -> shout(npc, "Death to our enemies!", 3219);
			case 1 -> shout(npc, "Brargh!", 3209);
			case 2 -> shout(npc, "Break their bones!", 3221);
			case 3 -> shout(npc, "Split their skulls!", 3229);
			case 4 -> shout(npc, "We feast on the bones of our enemies tonight!", 3206);
			case 5 -> shout(npc, "CHAAARGE!", 3220);
			case 6 -> shout(npc, "Crush them underfoot!", 3224);
			case 7 -> shout(npc, "All glory to Bandos!", 3205);
			case 8 -> shout(npc, "GRAAAAAAAAAR!", 3207);
			case 9 -> shout(npc, "FOR THE GLORY OF THE BIG HIGH WAR GOD!", 3228);
		}
	}

	private void shout(NPC npc, String text, int soundId) {
		npc.setNextForceTalk(new ForceTalk(text));
		npc.playSound(soundId, 2);
	}

	// --------------------------
	// Ranged
	// --------------------------
	private void performRangedAttack(NPC npc) {
		npc.animate(new Animation(7063));
		for (Entity t : npc.getPossibleTargets()) {
			Hit rangeHit = npc.rangedHit(npc,335);
			delayHit(npc, t, 1, rangeHit);
			ProjectileManager.sendSimple(Projectile.ARROW, 1200, npc, t);
		}
	}

	// --------------------------
	// Melee
	// --------------------------
	private void performMeleeAttack(NPC npc, Entity target) {
		NpcCombatDefinition defs = npc.getCombatDefinitions();
		if (Utils.isOnRange(
				npc.getX(), npc.getY(), npc.getSize(),
				target.getX(), target.getY(), target.getSize(),
				0)) {
			npc.animate(new Animation(defs.getAttackAnim()));
		}

		Hit meleeHit = getMeleeHit(npc,
				NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target)
		);
		delayHit(npc, target, 0, meleeHit);
	}
}
