package com.rs.java.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class LucienCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14256 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.getRandom(5);

		if (Utils.getRandom(10) == 0) {
			ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
			final HashMap<String, int[]> tiles = new HashMap<String, int[]>();
			for (Entity t : possibleTargets) {
				if (t instanceof Player) {
					Player p = (Player) t;
					if (!p.getMusicsManager().hasMusic(1008)) {
						p.getMusicsManager().playMusic(581);
						p.getMusicsManager().playMusic(584);
						p.getMusicsManager().playMusic(579);
						p.getMusicsManager().playMusic(1008);
						// p.getPackets()
						// .sendGameMessage(
						// "You've received a reward while fighting Lucius!");
					}
				}
				String key = t.getX() + "_" + t.getY();
				if (!tiles.containsKey(t.getX() + "_" + t.getY())) {
					tiles.put(key, new int[] { t.getX(), t.getY() });
					World.sendProjectileToTile(npc, new WorldTile(t.getX(), t.getY(), npc.getPlane()), 1900);
				}
			}
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
					for (int[] tile : tiles.values()) {

						World.sendGraphics(null, new Graphics(1896), new WorldTile(tile[0], tile[1], 0));
						for (Entity t : possibleTargets)
							if (t.getX() == tile[0] && t.getY() == tile[1])
								t.applyHit(new Hit(npc, Utils.getRandom(200) + 200, HitLook.REGULAR_DAMAGE));
					}
					stop();
				}

			}, 5);
		} else if (Utils.getRandom(10) == 0) {
			npc.gfx(new Graphics(444));
			npc.heal(1000);
		}
		if (attackStyle == 0) { // normal mage move
			npc.animate(new Animation(11338));
			delayHit(npc, 2, target, getMagicHit(npc, getRandomMaxHit(npc, 400, NPCCombatDefinitions.MAGE, target)));
			World.sendElementalProjectile(npc, target, 2963);
		} else if (attackStyle == 1) { // normal mage move
			npc.animate(new Animation(11338));
			delayHit(npc, 2, target, getRangeHit(npc, getRandomMaxHit(npc, 250, NPCCombatDefinitions.RANGE, target)));
			World.sendElementalProjectile(npc, target, 1904);
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					target.gfx(new Graphics(1910));
				}

			}, 2);

		} else if (attackStyle == 2) {
			npc.animate(new Animation(11318));
			npc.gfx(new Graphics(1901));
			World.sendElementalProjectile(npc, target, 1899);
			delayHit(npc, 4, target, getMagicHit(npc, getRandomMaxHit(npc, 400, NPCCombatDefinitions.MAGE, target)));
		} else if (attackStyle == 3) {
			npc.animate(new Animation(11373));
			npc.gfx(new Graphics(1898));
			target.gfx(new Graphics(2954));
			for (int i = 0; i < 10; i++)
				delayHit(npc, 2, target, getRegularHit(npc, 50));
		} else if (attackStyle == 4) {
			npc.animate(new Animation(11364));
			npc.gfx(new Graphics(2600));
			npc.setCantInteract(true);
			npc.getCombat().removeTarget();
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					for (Entity t : npc.getPossibleTargets()) {
						t.applyHit(new Hit(npc, 500, HitLook.REGULAR_DAMAGE, 0));
					}
					npc.getCombat().addCombatDelay(3);
					npc.setCantInteract(false);
					npc.setTarget(target);
				}

			}, 4);
			return 0;
		} else if (attackStyle == 5) {
			npc.setCantInteract(true);
			npc.animate(new Animation(11319));
			npc.getCombat().removeTarget();
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					npc.setCantInteract(false);
					npc.setTarget(target);
					int size = npc.getSize();
					int[][] dirs = Utils.getCoordOffsetsNear(size);
					for (int dir = 0; dir < dirs[0].length; dir++) {
						final WorldTile tile = new WorldTile(new WorldTile(target.getX() + dirs[0][dir],
								target.getY() + dirs[1][dir], target.getPlane()));
						if (World.canMoveNPC(tile.getPlane(), tile.getX(), tile.getY(), size)) { // if
																									// found
																									// done
							npc.setNextWorldTile(tile);
						}
					}
				}
			}, 3);
			return defs.getAttackDelay();
		}

		return defs.getAttackDelay();
	}
}
