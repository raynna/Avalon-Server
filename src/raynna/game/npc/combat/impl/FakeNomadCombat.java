package raynna.game.npc.combat.impl;

import raynna.game.*;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class FakeNomadCombat extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 8529 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(12697));
		Hit magicHit = npc.magicHit(target, 50);
		boolean hit = magicHit.getDamage() != 0;
		delayHit(npc, target, 2, getRegularHit(npc, hit ? 50 : 0));
		World.sendElementalProjectile(npc, target, 1657);
		if (hit) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.gfx(new Graphics(2278, 0, 100));
				}
			}, 1);
		}
		return npc.getAttackSpeed();
	}

}
