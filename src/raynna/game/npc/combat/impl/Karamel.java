package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.ForceTalk;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.player.Player;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class Karamel extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 3495 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int attackStyle = Utils.getRandom(2);
		if (attackStyle == 0) { // range
			npc.animate(new Animation(defs.getAttackAnim()));
			delayHit(npc, target, 1,
                    getRangeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.RANGED, target)));
			return npc.getAttackSpeed();
		}
		if (attackStyle == 2 || attackStyle == 1) {
			World.sendCBOWProjectile(npc, target, 362);
			npc.animate(new Animation(1979));
			npc.setNextForceTalk(new ForceTalk("Semolina-Go!"));
			Player p2 = (Player) target;
			p2.addFreezeDelay(5000, false);
			delayHit(npc, target, 2, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 100, NpcAttackStyle.MAGIC, target)));
			delayHit(npc, target, 2, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 100, NpcAttackStyle.MAGIC, target)));
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
