package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;

public class HybridMelee extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 1919, 19002 };
	}

	static int specialAttack = 100;

	@Override
	public int attack(final NPC npc, final Entity target) {
		specialAttack -= 25;
		npc.animate(new Animation(1062));
		npc.gfx(new Graphics(252, 0, 100));
		delayHit(npc, target, 0, getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 340, NpcAttackStyle.STAB, target)),
				getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 340, NpcAttackStyle.STAB, target)));
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (npc.getId() == 19002)
					npc.transformIntoNPC(Utils.getRandom(1) == 0 ? 19000 : 19001);
				else
					npc.transformIntoNPC(Utils.getRandom(1) == 0 ? 6367 : 3229);
				npc.setCombatLevel(83);
				npc.setCantFollowUnderCombat(false);
				npc.setAttackedByDelay(100);
				npc.setRandomWalk(npc.getDefinitions().walkMask);
				npc.setForceAgressive(true);
				npc.setNoDistanceCheck(false);
				npc.setTarget(target);
			}
		}, 1);
		return 4;
	}

	public int getMageDelay(NPC npc, Entity target) {
		if (Utils.getDistance(npc, target) > 3)
			return 4;
		if (Utils.getDistance(npc, target) == 2 || Utils.getDistance(npc, target) == 3)
			return 2;
		return 1;
	}

	public int getRangeDelay(NPC npc, Entity target) {
		if (Utils.getDistance(npc, target) > 3)
			return 2;
		return 1;
	}

}
