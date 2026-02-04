package com.rs.java.game.npc.combat.impl.dung;

import java.util.LinkedList;
import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeoneering.FamishedEye;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class FamishedEyeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 12436, 12451, 12466 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final FamishedEye eye = (FamishedEye) npc;

		if (eye.isInactive())
			return 0;
		else if (!eye.isFirstHit()) {
			eye.setFirstHit(true);
			return Utils.random(5, 15);
		}

		npc.animate(new Animation(14916));
		WorldTasksManager.schedule(new WorldTask() {

			private List<WorldTile> tiles;
			private WorldTile targetTile;

			int cycles;

			@Override
			public void run() {
				cycles++;
				if (cycles == 1) {
					tiles = new LinkedList<WorldTile>();
					targetTile = new WorldTile(target);
					World.sendProjectileToTile(eye, targetTile, 2849);
				} else if (cycles == 2) {
					for (int x = -1; x < 2; x++) {
						for (int y = -1; y < 2; y++) {
							WorldTile attackedTile = targetTile.transform(x, y, 0);
							if (x != y)
								World.sendProjectileToTile(eye, targetTile, 2851);
							tiles.add(attackedTile);
						}
					}
				} else if (cycles == 3) {
					for (WorldTile tile : tiles) {
						if (!tile.matches(targetTile))
							World.sendGraphics(eye, new Graphics(2852, 35, 5), tile);
						for (Entity t : eye.getPossibleTargets()) {
							if (t.matches(tile))
								t.applyHit(new Hit(eye, (int) Utils.random(eye.getMaxHit() * .25, eye.getMaxHit()), HitLook.REGULAR_DAMAGE));
						}
					}
					tiles.clear();
					stop();
					return;
				}
			}
		}, 0, 0);
		return (int) Utils.random(5, 35);
	}
}
