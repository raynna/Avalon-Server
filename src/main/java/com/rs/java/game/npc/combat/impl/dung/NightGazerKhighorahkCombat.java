package com.rs.java.game.npc.combat.impl.dung;

import java.util.LinkedList;
import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.NewForceMovement;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.dungeonnering.NightGazerKhighorahk;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class NightGazerKhighorahkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Night-gazer Khighorahk" };
	}

	public void sendRangeAoe(final NightGazerKhighorahk gazer) {
		if (gazer.isDead())
			return;
		gazer.animate(new Animation(13425));
		for (Entity target : gazer.getPossibleTargets()) {
			World.sendElementalProjectile(gazer, target, 2385);
			delayHit(gazer, target, 1, getRangeHit(gazer, getRandomMaxHit(gazer, (int) (gazer.getMaxHit() * 0.6), NPCCombatDefinitions.RANGE, target)));
		}

		if (!gazer.isSecondStage()) {
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					if (gazer.isDead())
						return;
					gazer.animate(new Animation(13422));
				}

			}, 5);
		}
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NightGazerKhighorahk gazer = (NightGazerKhighorahk) npc;
		final DungeonManager manager = gazer.getManager();

		/*
		 * without this check its possible to lure him so that he always nukes
		 */
		if (!gazer.isUsedSpecial()) {
			final List<Entity> targets = gazer.getPossibleTargets();
			boolean success = false;
			for (Entity t : targets) {
				if (Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), t.getX(), t.getY(), t.getSize(), 1)) {
					if (!success)
						success = true;
					npc.animate(new Animation(gazer.isSecondStage() ? 13427 : 13429));
					npc.gfx(new Graphics(/*gazer.isSecondStage() ? 2391 : */2390));
					gazer.setUsedSpecial(true);
				}
			}
			if (success) {
				WorldTasksManager.schedule(new WorldTask() {

					private int ticks;
					private List<WorldTile> tiles = new LinkedList<WorldTile>();

					@Override
					public void run() {
						ticks++;
						if (ticks == 1) {
							npc.animate(new Animation(gazer.isSecondStage() ? 13426 : 13428));
						} else if (ticks == 3) {
							for (Entity t : targets) {
								if (Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), t.getX(), t.getY(), t.getSize(), 1)) {
									t.applyHit(new Hit(npc, Utils.random((int) (t.getMaxHitpoints() * 0.74)) + 1, HitLook.REGULAR_DAMAGE));
									if (t instanceof Player) {
										Player player = (Player) t;
										player.lock(2);
										player.stopAll();
									}
									byte[] dirs = Utils.getDirection(npc.getDirection());
									WorldTile tile = null;
									distanceLoop: for (int distance = 2; distance >= 0; distance--) {
										tile = new WorldTile(new WorldTile(t.getX() + (dirs[0] * distance), t.getY() + (dirs[1] * distance), t.getPlane()));
										if (World.isFloorFree(tile.getPlane(), tile.getX(), tile.getY()) && manager.isAtBossRoom(tile))
											break distanceLoop;
										else if (distance == 0)
											tile = new WorldTile(t);
									}
									tiles.add(tile);
									t.faceEntity(gazer);
									t.animate(new Animation(10070));
									t.setNextForceMovement(new NewForceMovement(t, 0, tile, 2, t.getDirection()));
								}
							}
						} else if (ticks == 4) {
							for (int index = 0; index < tiles.size(); index++) {
								Entity t = targets.get(index);
								if (Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), t.getX(), t.getY(), t.getSize(), 1))
									t.setNextWorldTile(tiles.get(index));
							}
							stop();
							return;
						}
					}
				}, 0, 0);
				return 10;
			}
		} else
			gazer.setUsedSpecial(false);

		if (Utils.random(10) == 0) { //range aoe
			if (!gazer.isSecondStage()) {
				npc.animate(new Animation(13423));
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						sendRangeAoe(gazer);
					}

				}, 1);
				return npc.getAttackSpeed() + 6;
			} else {
				sendRangeAoe(gazer);
				return npc.getAttackSpeed() + 1;
			}
		} else {
			if (Utils.random(3) == 0) { //range single target
				npc.animate(new Animation(gazer.isSecondStage() ? 13433 : 13434));
				World.sendElementalProjectile(npc, target, 2385);
				delayHit(npc, target, 3, getRangeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.RANGE, target)));
				return npc.getAttackSpeed() + 1;
			} else { //magic
				npc.animate(new Animation(gazer.isSecondStage() ? 13430 : 13431));
				World.sendElementalProjectile(npc, target, 2385);
				target.gfx(new Graphics(2386, 70, 100));
				delayHit(npc, target, 1, getMagicHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
				return npc.getAttackSpeed();
			}
		}
	}
}
