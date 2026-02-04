package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.npc.dungeoneering.Stomp;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class StompCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Stomp" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();

		Stomp stomp = (Stomp) npc;

		if (npc.getHitpoints() <= 10 || npc.getHitpoints() < (npc.getMaxHitpoints() * (2 - stomp.getStage()) * 0.33)) {
			stomp.charge();
			return npc.getAttackSpeed();
		}
		// 0 - first 33%
		//1 - 66-33%
		//2 - 33-0%
		//3 - 0%

		if (stomp.getStage() > 1 && Utils.random(10) == 0) {
			final WorldTile tile = new WorldTile(target);
			World.sendGraphics(npc, new Graphics(2400), tile);
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					for (Entity target : npc.getPossibleTargets()) {
						if (target.getX() == tile.getX() && target.getY() == tile.getY())
							target.applyHit(new Hit(npc, (int) (target.getMaxHitpoints() * 0.25), HitLook.RANGE_DAMAGE));
					}
				}
			}, 4);
		}

		int attackStyle = Utils.random(/*stomp.getStage() > 1 ? 4 : */stomp.getStage() > 0 ? 3 : 2);
		if (attackStyle == 0 && !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
			attackStyle = 1;

		switch (attackStyle) {
		case 0:
			npc.animate(new Animation(defs.getAttackAnim()));
			delayHit(npc, target, 0, getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.CRUSH, target)));
			break;
		case 1:
			npc.animate(new Animation(13449));
			npc.gfx(new Graphics(2401));
			for (Entity t : npc.getPossibleTargets()) {
				World.sendElementalProjectile(npc, t, 2402);
				t.gfx(new Graphics(2403, 70, 0));
				delayHit(npc, t, 1, getRangeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.RANGED, target)));
			}
			break;
		case 2:
			npc.animate(new Animation(13450));
			npc.gfx(new Graphics(2404));
			World.sendElementalProjectile(npc, target, 2405);
			target.gfx(new Graphics(2406, 120, 0));
			delayHit(npc, target, 2, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.MAGIC, target)));
			break;

		}
		return npc.getAttackSpeed();
	}
}
