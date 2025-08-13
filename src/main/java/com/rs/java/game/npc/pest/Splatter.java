package com.rs.java.game.npc.pest;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.WorldTile;
import com.rs.java.game.minigames.pest.PestControl;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

@SuppressWarnings("serial")
public class Splatter extends PestMonsters {

	public Splatter(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned,
			int index, PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned, index, manager);
	}

	@Override
	public void processNPC() {
		super.processNPC();
	}

	private void sendExplosion() {
		final Splatter splatter = this;
		animate(new Animation(3888));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				animate(new Animation(3889));
				gfx(new Graphics(649 + (getId() - 3727)));
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						finish();
						for (Entity e : getPossibleTargets())
							if (e.withinDistance(splatter, 2))
								e.applyHit(new Hit(splatter, Utils.getRandom(400), HitLook.REGULAR_DAMAGE));
					}
				});
			}
		});
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		animate(-1);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					sendExplosion();
				else if (loop >= defs.getDeathDelay()) {
					reset();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}
