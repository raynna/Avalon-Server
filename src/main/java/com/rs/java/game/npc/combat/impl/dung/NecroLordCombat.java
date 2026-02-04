package com.rs.java.game.npc.combat.impl.dung;

import java.util.LinkedList;
import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeoneering.NecroLord;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class NecroLordCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 11737 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final NecroLord boss = (NecroLord) npc;

		if (Utils.random(10) == 0) {
			final int skeletonCount = boss.getManager().getParty().getTeam().size();
			final List<WorldTile> projectileTile = new LinkedList<WorldTile>();
			WorldTasksManager.schedule(new WorldTask() {
				int cycles;

				@Override
				public void run() {
					cycles++;

					if (cycles == 2) {
						for (int i = 0; i < skeletonCount; i++) {
							WorldTile tile = Utils.getFreeTile(boss.getManager().getTile(boss.getReference(), Utils.random(2) == 0 ? 5 : 10, 5), 4);
							projectileTile.add(tile);
							World.sendProjectileToTile(boss, tile, 2590);
						}
					} else if (cycles == 4) {
						for (WorldTile tile : projectileTile)
							boss.addSkeleton(tile);
						stop();
						return;
					}
				}
			}, 0, 0);
		}

		final int attack = Utils.random(4);
		switch (attack) {
		case 0://main attack
		case 1:
			npc.animate(new Animation(14209));
			npc.gfx(new Graphics(2716));
			World.sendElementalProjectile(npc, target, 2721);
			delayHit(npc, target, 1, npc.magicHit(npc, npc.getMaxHit()));
			target.gfx(new Graphics(2726, 75, 80));
			break;
		case 2:
		case 3:
			final WorldTile tile = new WorldTile(target);
			npc.animate(new Animation(attack == 2 ? 710 : 729));
			npc.gfx(new Graphics(attack == 2 ? 177 : 167, 0, 65));
			World.sendProjectileToTile(npc, tile, attack == 2 ? 178 : 168);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					for (Entity t : boss.getPossibleTargets()) {
						Hit hit = npc.magicHit(t, boss.getMaxHit());
						if (!t.withinDistance(tile, 1))
							continue;
						if (hit.getDamage() > 0) {
							if (attack == 2)
								t.setFreezeDelay(8);
							else {
								if (t instanceof Player) {
									Player p2 = (Player) t;
									p2.getPackets().sendGameMessage("You feel weary.");
									p2.setRunEnergy((int) (p2.getRunEnergy() * .5));
								}
								t.applyHit(hit);
							}
							t.gfx(new Graphics(attack == 2 ? 179 : 169, 60, 65));
						}
					}
				}
			}, 1);

			break;
		}
		return Utils.random(2) == 0 ? 4 : 5;
	}
}
