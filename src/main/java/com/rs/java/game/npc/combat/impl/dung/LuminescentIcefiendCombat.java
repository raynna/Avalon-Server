package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeoneering.LuminscentIcefiend;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
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
			World.sendElementalProjectile(npc, target, 2529);
			delayHit(icefiend, target, 2, npc.magicHit(npc, icefiend.getMaxHit()));
		} else {
			npc.animate(new Animation(13337));
			World.sendElementalProjectile(npc, target, 2530);
			delayHit(icefiend, target, 2, npc.rangedHit(npc, (int) (icefiend.getMaxHit() * .90)));
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
