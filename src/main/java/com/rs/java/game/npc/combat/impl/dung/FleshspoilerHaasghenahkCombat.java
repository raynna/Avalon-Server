package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeonnering.FleshspoilerHaasghenahk;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

public class FleshspoilerHaasghenahkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 11925, 11895 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final FleshspoilerHaasghenahk boss = (FleshspoilerHaasghenahk) npc;

		for (Entity t : npc.getPossibleTargets()) {
			if (Utils.colides(t.getX(), t.getY(), t.getSize(), npc.getX(), npc.getY(), npc.getSize()))
				delayHit(npc, t, 0, getRegularHit(npc, boss.getMaxHit()));
		}
		if (boss.isSecondStage())
			return 0;
		boolean magicOnly = boss.canUseMagicOnly();
		if (magicOnly || Utils.random(5) == 0) {
			if (magicOnly) {
				if (target instanceof Player) {
					Player player = (Player) target;
					if (player.getPrayer().isMageProtecting() && Utils.random(3) == 0)
						boss.setUseMagicOnly(false);
				}
			}
			npc.animate(new Animation(14463));
			delayHit(npc, target, 1, npc.meleeHit(npc, boss.getMaxHit()));
		} else {
			npc.animate(new Animation(13320));
			delayHit(npc, target, 0, npc.meleeHit(npc, boss.getMaxHit()));
		}
		return 6;
	}
}
