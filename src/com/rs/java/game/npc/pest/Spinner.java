package com.rs.java.game.npc.pest;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.WorldTile;
import com.rs.java.game.minigames.pest.PestControl;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class Spinner extends PestMonsters {

	private byte healTicks;

	public Spinner(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned,
			int index, PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned, index, manager);
	}

	@Override
	public void processNPC() {
		PestPortal portal = manager.getPortals()[portalIndex];
		if (portal.isDead()) {
			explode();
			return;
		}
		if (!portal.isLocked) {
			healTicks++;
			if (!withinDistance(portal, 1))
				this.addWalkSteps(portal.getX(), portal.getY());
			else if (healTicks % 6 == 0)
				healPortal(portal);
		}
	}

	private void healPortal(final PestPortal portal) {
		setNextFaceEntity(portal);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				animate(new Animation(3911));
				gfx(new Graphics(658, 0, 96 << 16));
				if (portal.getHitpoints() != 0)
					portal.heal((portal.getMaxHitpoints() / portal.getHitpoints()) * 45);
				healTicks = 0; /* Saves memory in the long run. Meh */
			}
		});
	}

	private void explode() {
		final NPC npc = this;
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				for (Player player : manager.getPlayers()) {
					if (!withinDistance(player, 7))
						continue;
					player.getPoison().makePoisoned(50);
					player.applyHit(new Hit(npc, 50, HitLook.REGULAR_DAMAGE));
					npc.reset();
					npc.finish();
				}
			}
		}, 1);
	}
}
