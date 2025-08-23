package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.utils.Utils;

public class GeneralGraardorCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6260 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.getRandom(4) == 0) {
			switch (Utils.getRandom(10)) {
			case 0:
				npc.setNextForceTalk(new ForceTalk("Death to our enemies!"));
				npc.playSound(3219, 2);
				break;
			case 1:
				npc.setNextForceTalk(new ForceTalk("Brargh!"));
				npc.playSound(3209, 2);
				break;
			case 2:
				npc.setNextForceTalk(new ForceTalk("Break their bones!"));
				npc.playSound(3221, 2);
				break;
//			case 3:
//				npc.setNextForceTalk(new ForceTalk("For the glory of Bandos!"));
//				player.getPackets().sendSound(soundId, 0, 2);//TODO: Get sound id
//				break;
			case 4:
				npc.setNextForceTalk(new ForceTalk("Split their skulls!"));
				npc.playSound(3229, 2);
				break;
			case 5:
				npc.setNextForceTalk(new ForceTalk("We feast on the bones of our enemies tonight!"));
				npc.playSound(3206, 2);
				break;
			case 6:
				npc.setNextForceTalk(new ForceTalk("CHAAARGE!"));
				npc.playSound(3220, 2);
				break;
			case 7:
				npc.setNextForceTalk(new ForceTalk("Crush them underfoot!"));
				npc.playSound(3224, 2);
				break;
			case 8:
				npc.setNextForceTalk(new ForceTalk("All glory to Bandos!"));
				npc.playSound(3205, 2);
				break;
			case 9:
				npc.setNextForceTalk(new ForceTalk("GRAAAAAAAAAR!"));
				npc.playSound(3207, 2);
				break;
			case 10:
				npc.setNextForceTalk(new ForceTalk("FOR THE GLORY OF THE BIG HIGH WAR GOD!"));
				npc.playSound(3228, 2);
				break;
			}
		}
		if (Utils.getRandom(2) == 0) { // range magical attack
			npc.animate(new Animation(7063));
			for (Entity t : npc.getPossibleTargets()) {
				delayHit(npc, t, 1, getRangeHit(npc, getRandomMaxHit(npc, 335, NPCCombatDefinitions.RANGE, t)));
				World.sendElementalProjectile(npc, t, 1200);
			}
		} else { // melee attack
			if (Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(),
					0))
				npc.animate(new Animation(defs.getAttackEmote()));
			delayHit(npc, target, 0,
                    getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
		}
		return defs.getAttackDelay();
	}
}
