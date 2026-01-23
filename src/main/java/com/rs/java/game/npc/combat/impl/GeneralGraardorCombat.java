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

	private final static int MELEE_ANIMATION = 7060;
	private final static int RANGE_ANIMATION = 7063;
	private final static int RANGE_PROJECTILE = 1200;


	enum GeneralGraardorAttack { MELEE, RANGE }

	@Override
	public Object[] getKeys() {
		return new Object[]{6260};
	}

	@Override
	public int attack(NPC npc, Entity target) {
		maybeShout(npc);
		boolean inMelee = npc.isWithinMeleeRange(target);
		if (!inMelee) {
			return npc.getAttackSpeed();
		}
		GeneralGraardorAttack attack = Utils.randomWeighted(GeneralGraardorAttack.MELEE, 66, GeneralGraardorAttack.RANGE, 33);
		switch (attack) {
			case RANGE -> performRangedAttack(npc);
			case MELEE -> performMeleeAttack(npc, target);
		}
		return npc.getAttackSpeed();
	}

	private void performRangedAttack(NPC npc) {
		npc.animate(new Animation(RANGE_ANIMATION));
		for (Entity t : npc.getPossibleTargets()) {
			int damage = Utils.random(150, 350);
			Hit rangeHit = npc.rangedHit(t, damage);
			ProjectileManager.send(Projectile.GENERAL_GRAARDOR, RANGE_PROJECTILE, npc, t, () -> applyRegisteredHit(npc, t, rangeHit));
		}
	}

	private void performMeleeAttack(NPC npc, Entity target) {
		npc.animate(MELEE_ANIMATION);
		Hit meleeHit = npc.meleeHit(target, 600);
		delayHit(npc, target, 0, meleeHit);
	}

	private void shout(NPC npc, String text, int soundId) {
		npc.setNextForceTalk(new ForceTalk(text));
		npc.playSound(soundId, 2);
	}

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
}
