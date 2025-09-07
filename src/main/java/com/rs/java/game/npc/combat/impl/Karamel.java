package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

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
