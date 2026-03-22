package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class Culinaromancer extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 3491 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int attackStyle = Utils.random(2);
		if (attackStyle == 0 || attackStyle == 1) { // Melee
			npc.animate(new Animation(defs.getAttackAnim()));
			delayHit(npc, target, 0,
                    npc.meleeHit(npc, defs.getMaxHit()));
			return npc.getAttackSpeed();
		}
		if (attackStyle == 2) {
			World.sendCBOWProjectile(npc, target, 362);
			npc.animate(new Animation(1979));
			target.addFreezeDelay(5000, false);
			delayHit(npc, target, 1,
                    npc.magicHit(npc, defs.getMaxHit()));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.gfx(new Graphics(369, 0, 0));
				}
			}, 1);
			return npc.getAttackSpeed() + 2;
		}
		return attackStyle;
	}
}
