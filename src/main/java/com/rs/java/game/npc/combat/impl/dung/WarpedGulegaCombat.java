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
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.npc.dungeonnering.WarpedGulega;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;

public class WarpedGulegaCombat extends CombatScript {

	private static final Graphics MELEE = new Graphics(2878);

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 12737 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final WarpedGulega boss = (WarpedGulega) npc;

		int style = Utils.random(4);
		switch (style) {
		case 3://reg aeo melee
			npc.animate(new Animation(15004));

			final List<WorldTile> attackTiles = new LinkedList<WorldTile>();
			for (Entity t : boss.getPossibleTargets(true, true))
				attackTiles.add(new WorldTile(t));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					for (WorldTile tile : attackTiles)
						World.sendGraphics(npc, MELEE, tile);
					for (Entity t : boss.getPossibleTargets(true, true)) {
						tileLoop: for (WorldTile tile : attackTiles) {
							if (t.getX() == tile.getX() && t.getY() == tile.getY()) {
								delayHit(npc, t, 0, getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, (int) (npc.getMaxHit() * 0.75), NpcAttackStyle.CRUSH, t)));
								break tileLoop;
							}
						}
					}
				}
			});
			break;
		case 1://reg range aeo
			npc.animate(new Animation(15001));
			npc.gfx(new Graphics(2882));
			for (Entity t : npc.getPossibleTargets(true, true)) {
				World.sendElementalProjectile(npc, t, 2883);
				t.gfx(new Graphics(2884, 90, 0));
				delayHit(npc, t, 2, getRangeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, (int) (npc.getMaxHit() * 0.75), NpcAttackStyle.RANGED, t)));
			}
			break;
		case 2://reg magic aeo
			npc.animate(new Animation(15007));
			for (Entity t : npc.getPossibleTargets(true, true)) {
				World.sendElementalProjectile(npc, t, 2880);
				t.gfx(new Graphics(2881, 90, 0));
				delayHit(npc, t, 2, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, (int) (npc.getMaxHit() * 0.75), NpcAttackStyle.MAGIC, t)));
			}
			break;
		case 0:
			npc.animate(new Animation(15004));
			WorldTasksManager.schedule(new WorldTask() {

				WorldTile center;
				int cycles;

				@Override
				public void run() {
					cycles++;

					if (cycles == 1) {
						center = new WorldTile(target);
						sendTenticals(boss, center, 2);
					} else if (cycles == 3)
						sendTenticals(boss, center, 1);
					else if (cycles == 5)
						sendTenticals(boss, center, 0);
					else if (cycles == 6) {
						for (Entity t : npc.getPossibleTargets(true, true)) {
							if (t.getX() == center.getX() && t.getY() == center.getY())
								t.applyHit(new Hit(npc, t.getHitpoints() - 1, HitLook.REGULAR_DAMAGE));
						}
						stop();
						return;
					}
				}
			}, 0, 0);
			return 7;
		}
		return 4;
	}

	private void sendTenticals(NPC npc, WorldTile center, int stage) {
		if (stage == 0) {
			World.sendGraphics(npc, MELEE, center);
		} else if (stage == 2 || stage == 1) {
			World.sendGraphics(npc, MELEE, center.transform(-stage, stage, 0));
			World.sendGraphics(npc, MELEE, center.transform(stage, stage, 0));
			World.sendGraphics(npc, MELEE, center.transform(-stage, -stage, 0));
			World.sendGraphics(npc, MELEE, center.transform(stage, -stage, 0));
		}
	}
}
