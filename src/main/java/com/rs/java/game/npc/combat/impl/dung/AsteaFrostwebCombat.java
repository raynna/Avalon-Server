package com.rs.java.game.npc.combat.impl.dung;

import java.util.ArrayList;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.dungeonnering.AsteaFrostweb;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class AsteaFrostwebCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Astea Frostweb" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.getRandom(10) == 0) {
			AsteaFrostweb boss = (AsteaFrostweb) npc;
			boss.spawnSpider();
		}
		if (Utils.getRandom(10) == 0) { // spikes
			ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
			npc.animate(new Animation(defs.getAttackEmote()));
			for (Entity t : possibleTargets)
				delayHit(npc, t, 1, new Hit(npc, Utils.random((int) (npc.getMaxHit() * 0.5) + 1), HitLook.REGULAR_DAMAGE));
			return defs.getAttackDelay();
		} else {
			int attackStyle = Utils.random(2);
			if (attackStyle == 1) { // check melee
				if (Utils.getDistance(npc.getX(), npc.getY(), target.getX(), target.getY()) > 1)
					attackStyle = 0; // set mage
				else { // melee
					npc.animate(new Animation(defs.getAttackEmote()));
					delayHit(npc, target, 0, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(),  NPCCombatDefinitions.MELEE, target)));
					return defs.getAttackDelay();
				}
			}
			if (attackStyle == 0) { // mage
				npc.animate(new Animation(defs.getAttackEmote()));
				ArrayList<Entity> possibleTargets = npc.getPossibleTargets();

				int d = getMaxHit(npc, NPCCombatDefinitions.MAGE, target);
				delayHit(npc, target, 1, getMagicHit(npc, d));
				if (d != 0) {
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							if (target.isFrozen())
								target.gfx(new Graphics(1677, 0, 100));
							else {
								target.gfx(new Graphics(369));
								target.setFreezeDelay(8);
							}
						}
					}, 1);
					for (final Entity t : possibleTargets) {
						if (t != target && t.withinDistance(target, 2)) {
							int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, t);
							delayHit(npc, t, 1, getMagicHit(npc, damage));
							if (damage != 0) {
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										if (t.isFrozen())
											t.gfx(new Graphics(1677, 0, 100));
										else {
											t.gfx(new Graphics(369));
											t.setFreezeDelay(8);
										}
									}
								}, 1);
							}

						}
					}
				}
				if (Utils.getDistance(npc.getX(), npc.getY(), target.getX(), target.getY()) <= 1) { // lure
					// after
					// freeze
					npc.resetWalkSteps();
					npc.addWalkSteps(target.getX() + Utils.random(3), target.getY() + Utils.random(3));
				}
			}
		}
		return defs.getAttackDelay();
	}

	private int getMaxHit(NPC npc, int mage, Entity target) {
		// TODO Auto-generated method stub
		return 0;
	}
}
