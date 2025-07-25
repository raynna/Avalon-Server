package com.rs.java.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.rs.core.cache.defintions.NPCDefinitions;
import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceMovement;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.minigames.godwars.zaros.Nex;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.cutscenes.NexCutScene;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class NexCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Nex" };
	}

	public WorldTile[] NO_ESCAPE_TELEPORTS = { new WorldTile(2924, 5213, 0), // north
			new WorldTile(2934, 5202, 0), // east,
			new WorldTile(2924, 5192, 0), // south
			new WorldTile(2913, 5202, 0), }; // west

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		final Nex nex = (Nex) npc;
		if (nex.isFollowTarget()) {
			nex.setForceFollowClose(true);
			nex.setFollowTarget(Utils.getRandom(1) == 0);
			if (nex.getAttacksStage() == 0) {
				if (nex.getLastVirus() < Utils.currentTimeMillis() && Utils.getRandom(2) == 0) {
					nex.setLastVirus(Utils.currentTimeMillis() + 60000);
					npc.setNextForceTalk(new ForceTalk("Let the virus flow through you."));
					nex.playSound(3296, 2);
					npc.animate(new Animation(6987));
					nex.sendVirusAttack(new ArrayList<Entity>(), npc.getPossibleTargets(), target);
					return defs.getAttackDelay();
				}
			}
			if (Utils.getRandom(nex.getStage() == 4 ? 5 : 10) == 0) {
				npc.setNextForceTalk(new ForceTalk("There is..."));
				nex.playSound(3294, 2);
				npc.setCantInteract(true);
				npc.getCombat().removeTarget();
				final int idx = Utils.random(NO_ESCAPE_TELEPORTS.length);
				final WorldTile dir = NO_ESCAPE_TELEPORTS[idx];
				final WorldTile center = new WorldTile(2924, 5202, 0);
				WorldTasksManager.schedule(new WorldTask() {
					private int count;

					@Override
					public void run() {
						if (count == 0) {
							npc.animate(new Animation(6321));
							npc.gfx(new Graphics(1216));
						} else if (count == 1) {
							nex.setNextWorldTile(dir);
							nex.setNextForceTalk(new ForceTalk("NO ESCAPE!"));
							nex.playSound(3292, 2);
							nex.setNextForceMovement(new ForceMovement(dir, 1, center, 3,
									idx == 3 ? 1 : idx == 2 ? 0 : idx == 1 ? 3 : 2));
							for (Entity entity : nex.calculatePossibleTargets(center, dir, idx == 0 || idx == 2)) {
								if (entity instanceof Player) {
									Player player = (Player) entity;
									player.getCutscenesManager().play(new NexCutScene(dir, idx));
									player.applyHit(new Hit(npc, Utils.getRandom(nex.getStage() == 4 ? 800 : 650),
											HitLook.REGULAR_DAMAGE));
									player.animate(new Animation(10070));
									player.setNextForceMovement(new ForceMovement(player, 1,
											idx == 3 ? 3 : idx == 2 ? 2 : idx == 1 ? 1 : 0));
								}
							}
						} else if (count == 3) {
							nex.setNextWorldTile(center);
						} else if (count == 4) {
							nex.setTarget(target);
							npc.setCantInteract(false);
							stop();
						}
						count++;
					}
				}, 0, 1);
				return defs.getAttackDelay();
			}
			int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
			delayHit(npc, 0, target, getMeleeHit(npc, damage));
			npc.animate(new Animation(defs.getAttackEmote()));
			return defs.getAttackDelay();
		} else {
			nex.setForceFollowClose(false);
			nex.setFollowTarget(Utils.getRandom(1) == 0);
			if (Utils.getRandom(15) == 0) {
				int distance = 0;
				Entity settedTarget = null;
				for (Entity t : npc.getPossibleTargets()) {
					if (t instanceof Player) {
						int thisDistance = Utils.getDistance(t.getX(), t.getY(), npc.getX(), npc.getY());
						if (settedTarget == null || thisDistance > distance) {
							distance = thisDistance;
							settedTarget = t;
						}
					}
				}
				if (settedTarget != null && distance > 10) {
					final Player player = (Player) settedTarget;
					player.lock(3);
					player.setNextForceMovement(new ForceMovement(nex, 2,
							Utils.getMoveDirection(npc.getCoordFaceX(player.getSize()) - player.getX(),
									npc.getCoordFaceY(player.getSize()) - player.getY())));
					npc.animate(new Animation(6986));
					npc.setTarget(player);
					player.animate(new Animation(-1));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextWorldTile(nex);
							player.getPackets()
									.sendGameMessage("You've been injured and you can't use protective curses!");
							player.setPrayerDelay(Utils.getRandom(20000) + 5);
							player.getPackets().sendGameMessage("You're stunned.");
						}
					});
					return defs.getAttackDelay();
				}
			}
			if (nex.getAttacksStage() == 0) {
				npc.animate(new Animation(6987));
				for (Entity t : npc.getPossibleTargets()) {
					World.sendElementalProjectile(npc, t, 471);
					int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, t);
					delayHit(npc, 1, t, getMagicHit(npc, damage));
					if (damage > 0 && Utils.getRandom(5) == 0)
						t.getPoison().makePoisoned(80);
				}
				return defs.getAttackDelay();
			} else if (nex.getAttacksStage() == 1) {
				if (!nex.isEmbracedShadow()) {
					nex.setEmbracedShadow(true);
					npc.setNextForceTalk(new ForceTalk("Embrace darkness!"));
					nex.playSound(3322, 2);
					npc.animate(new Animation(6355));
					npc.gfx(new Graphics(1217));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							if (nex.getAttacksStage() != 1 || nex.hasFinished()) {
								for (Entity entity : nex.getPossibleTargets()) {
									if (entity instanceof Player) {
										Player player = (Player) entity;
										player.getPackets().sendGlobalConfig(1435, 255);
									}
								}
								stop();
								return;
							}
							if (Utils.getRandom(2) == 0) {
								for (Entity entity : nex.getPossibleTargets()) {
									if (entity instanceof Player) {
										Player player = (Player) entity;
										int distance = Utils.getDistance(player.getX(), player.getY(), npc.getX(),
												npc.getY());
										if (distance > 30)
											distance = 30;
										player.getPackets().sendGlobalConfig(1435, (distance * 255 / 30));
									}
								}
							}
						}
					}, 0, 0);
					return defs.getAttackDelay();
				}
				if (!nex.isTrapsSettedUp() && Utils.getRandom(2) == 0) {
					nex.setTrapsSettedUp(true);
					npc.setNextForceTalk(new ForceTalk("Fear the Shadow!"));
					nex.playSound(3314, 2);
					npc.animate(new Animation(6984));
					npc.gfx(new Graphics(1215));
					ArrayList<Entity> possibleTargets = nex.getPossibleTargets();
					final HashMap<String, int[]> tiles = new HashMap<String, int[]>();
					for (Entity t : possibleTargets) {
						String key = t.getX() + "_" + t.getY();
						if (!tiles.containsKey(t.getX() + "_" + t.getY())) {
							tiles.put(key, new int[] { t.getX(), t.getY() });
						}
					}
					WorldTasksManager.schedule(new WorldTask() {
						private boolean firstCall;

						@Override
						public void run() {
							if (!firstCall) {
								ArrayList<Entity> possibleTargets = nex.getPossibleTargets();
								for (int[] tile : tiles.values()) {
									World.sendGraphics(null, new Graphics(383), new WorldTile(tile[0], tile[1], 0));
									for (Entity t : possibleTargets)
										if (t.getX() == tile[0] && t.getY() == tile[1])
											t.applyHit(
													new Hit(npc, Utils.getRandom(400) + 400, HitLook.REGULAR_DAMAGE));
								}
								firstCall = true;
							} else {
								nex.setTrapsSettedUp(false);
								stop();
							}
						}

					}, 3, 3);
					return defs.getAttackDelay();
				}
				npc.animate(new Animation(6987));
				for (final Entity t : npc.getPossibleTargets()) {
					int distance = Utils.getDistance(t.getX(), t.getY(), npc.getX(), npc.getY());
					if (distance <= 10) {
						int damage = 800 - (distance * 800 / 11);
						World.sendElementalProjectile(npc, t, 380);
						delayHit(npc, 1, t,
								getRangeHit(npc, getRandomMaxHit(npc, damage, NPCCombatDefinitions.RANGE, t)));
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								t.gfx(new Graphics(471));
							}
						}, 1);
					}
				}
				return defs.getAttackDelay();
			} else if (nex.getAttacksStage() == 2) {
				if (Utils.getRandom(4) == 0 && target instanceof Player) {
					npc.setNextForceTalk(new ForceTalk("I demand a blood sacrifice!"));
					nex.playSound(3293, 2);
					final Player player = (Player) target;
					player.getAppearence().setGlowRed(true);
					player.getPackets().sendGameMessage("Nex has marked you as a sacrifice, RUN!");
					final int x = player.getX();
					final int y = player.getY();
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.getAppearence().setGlowRed(false);
							if (x == player.getX() && y == player.getY()) {
								player.getPackets().sendGameMessage(
										"You didn't make it far enough in time - Nex fires a punishing attack!");
								npc.animate(new Animation(6987));
								for (final Entity t : npc.getPossibleTargets()) {
									World.sendElementalProjectile(npc, t, 374);
									final int damage = getRandomMaxHit(npc, 290, NPCCombatDefinitions.MAGE, t);
									delayHit(npc, 1, t, getMagicHit(npc, damage));
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											t.gfx(new Graphics(376));
											nex.heal(damage / 4);
											if (t instanceof Player) {
												Player p = (Player) t;
												p.getPrayer().drainPrayerOnHalf();
											}
										}
									}, 1);
								}
							}
						}
					}, defs.getAttackDelay());
					return defs.getAttackDelay() * 2;
				}
				if (nex.getLastSiphon() < Utils.currentTimeMillis() && npc.getHitpoints() <= 18000
						&& Utils.getRandom(2) == 0) {
					nex.setLastSiphon(Utils.currentTimeMillis() + 30000);
					nex.killBloodReavers();
					npc.setNextForceTalk(new ForceTalk("A siphon will solve this!"));
					nex.playSound(3317, 2);
					npc.animate(new Animation(6948));
					npc.gfx(new Graphics(1201));
					nex.setDoingSiphon(true);
					int bloodReaverSize = NPCDefinitions.getNPCDefinitions(13458).size;
					int respawnedBloodReaverCount = 0;
					int maxMinions = Utils.getRandom(3);
					if (maxMinions != 0) {
						int[][] dirs = Utils.getCoordOffsetsNear(bloodReaverSize);
						for (int dir = 0; dir < dirs[0].length; dir++) {
							final WorldTile tile = new WorldTile(new WorldTile(target.getX() + dirs[0][dir],
									target.getY() + dirs[1][dir], target.getPlane()));
							if (World.canMoveNPC(tile.getPlane(), tile.getX(), tile.getY(), bloodReaverSize)) {
								nex.getBloodReavers()[respawnedBloodReaverCount++] = new NPC(13458, tile, -1, true,
										true);
								if (respawnedBloodReaverCount == maxMinions)
									break;
							}
						}
					}
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							nex.setDoingSiphon(false);
						}
					}, 8);
					return defs.getAttackDelay();
				}
				npc.animate(new Animation(6986));
				World.sendElementalProjectile(npc, target, 374);
				delayHit(npc, 1, target,
						getMagicHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
				return defs.getAttackDelay();
			} else if (nex.getAttacksStage() == 3) {
				npc.animate(new Animation(6986));
				for (final Entity t : npc.getPossibleTargets()) {
					World.sendElementalProjectile(npc, t, 362);
					int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, t);
					delayHit(npc, 1, t, getMagicHit(npc, damage));
					if (damage > 0 && Utils.getRandom(5) == 0) {// 1/6
																// probability
																// freezing
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								t.setFreezeDelay(18000);
								t.gfx(new Graphics(369));
							}
						}, 2);

					}
				}
				return defs.getAttackDelay();
			} else if (nex.getAttacksStage() == 4) {
				npc.animate(new Animation(6987));
				for (Entity t : npc.getPossibleTargets()) {
					World.sendElementalProjectile(npc, t, 471);
					int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, t);
					delayHit(npc, 1, t, getMagicHit(npc, damage));
				}
				return defs.getAttackDelay();
			}
		}
		return defs.getAttackDelay();
	}
}
