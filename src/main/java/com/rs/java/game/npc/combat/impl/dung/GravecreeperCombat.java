package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeonnering.Gravecreeper;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class GravecreeperCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 11708 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final Gravecreeper boss = (Gravecreeper) npc;
		if (boss.getSpecialDelay() != -2 && (boss.getSpecialDelay() == -1 || (Utils.random(10) == 0 && boss.getSpecialDelay() <= Utils.currentTimeMillis()))) { //might change this chance here
			if (boss.getSpecialDelay() != -1 && Utils.random(5) != 0) {
				boss.setNextForceTalk(new ForceTalk("Burrnnn!"));
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						boss.createBurnTiles(new WorldTile(boss));
					}
				}, 1);
				boss.setSpecialDelay(Utils.currentTimeMillis() + Gravecreeper.BURN_DELAY);
				if (Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
					boss.setForceFollowClose(true);
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							boss.setForceFollowClose(false);
						}
					}, 7);
				}
				return 4;
			} else {
				boss.useSpecial();
				return 4;
			}
		}

		boolean atDistance = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0);
		int attack = Utils.random(!atDistance ? 2 : 1);
		switch (attack) {
		case 0://range
			npc.animate(new Animation(14504));
			World.sendElementalProjectile(npc, target, 2753);
			delayHit(npc, target, 1, npc.rangedHit(npc, npc.getMaxHit()));
			break;
		case 1://melee
			npc.animate(new Animation(14503));
			delayHit(npc, target, 0, npc.meleeHit(npc, npc.getMaxHit()));
			break;
		}
		return 4;
	}
}
