package com.rs.java.game.npc.combat.impl.dung;

import java.util.ArrayList;
import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.combat.impl.KalphiteQueenCombat;
import com.rs.java.game.npc.dungeonnering.BulwarkBeast;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class BulwarkBeastCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Bulwark beast" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		((BulwarkBeast) npc).refreshBar();

		final NPCCombatDefinitions defs = npc.getCombatDefinitions();

		if (Utils.random(15) == 0) {
			List<Entity> targets = npc.getPossibleTargets();
			npc.animate(new Animation(13007));
			for (Entity t : targets) {
				if (Utils.isOnRange(t.getX(), t.getY(), t.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0)) {
					t.gfx(new Graphics(2400));
					delayHit(npc, 1, t, getRegularHit(npc, 1 + Utils.random((int) (npc.getMaxHit() * 0.7))));
				}
			}
			return npc.getAttackSpeed();
		}

		//mage, range, melee
		int attackStyle = Utils.random(Utils.isOnRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0) ? 3 : 2);
		switch (attackStyle) {
		case 0:
			npc.animate(new Animation(13004));
			npc.gfx(new Graphics(2397));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					KalphiteQueenCombat.attackMageTarget(new ArrayList<Player>(), npc, npc, target, 2398, 2399);
				}

			});
			break;
		case 1:
			npc.animate(new Animation(13006));
			npc.gfx(new Graphics(2394));
			List<Entity> targets = npc.getPossibleTargets();
			for (Entity t : targets) {
				World.sendProjectileToTile(npc, t, 2395);
				t.gfx(new Graphics(2396, 75, 0));
				delayHit(npc, 1, t, getRangeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.RANGE, t)));
			}
			break;
		case 2:
			npc.animate(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
			break;
		}
		return npc.getAttackSpeed();
	}
}
