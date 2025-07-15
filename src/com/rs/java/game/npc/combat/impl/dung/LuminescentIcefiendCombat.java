package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.dungeonnering.LuminscentIcefiend;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class LuminescentIcefiendCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 9912 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final LuminscentIcefiend icefiend = (LuminscentIcefiend) npc;

		if (icefiend.isSpecialEnabled()) {
			npc.animate(new Animation(13338));
			npc.gfx(new Graphics(2524));

			icefiend.commenceSpecial();
			return 20;
		}

		boolean magicAttack = Utils.random(2) == 0;

		if (magicAttack) {
			npc.animate(new Animation(13352));
			World.sendProjectile(npc, target, 2529, 15, 16, 35, 35, 16, 0);
			delayHit(icefiend, 2, target, getMagicHit(npc, getRandomMaxHit(npc, icefiend.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
		} else {
			npc.animate(new Animation(13337));
			World.sendProjectile(npc, target, 2530, 30, 16, 35, 35, 0, 0);
			delayHit(icefiend, 2, target, getRangeHit(npc, getRandomMaxHit(npc, (int) (icefiend.getMaxHit() * .90), NPCCombatDefinitions.RANGE, target)));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					target.gfx(new Graphics(2531));
				}
			}, 2);
		}
		return 4;
	}
}
