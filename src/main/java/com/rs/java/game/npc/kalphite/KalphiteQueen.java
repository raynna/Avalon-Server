package com.rs.java.game.npc.kalphite;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class KalphiteQueen extends NPC {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6226823114356352373L;

	public KalphiteQueen(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
		setForceTargetDistance(64);
		setForceAgressiveDistance(10);
		setForceAgressive(true);
	}

	@Override
	public void handleHit(final Hit hit) {
		super.handleHit(hit);
	}

	@Override
	public void sendDeath(Entity source) {
		final NpcCombatDefinition defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		animate(-1);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					animate(new Animation(defs.getDeathAnim()));
				} else if (loop >= defs.getDeathDelay()) {
					if (getId() == 1158) {
						setCantInteract(true);
						transformIntoNPC(1160);
						gfx(new Graphics(1055));
						animate(new Animation(6270));
						WorldTasksManager.schedule(new WorldTask() {

							@Override
							public void run() {
								reset();
								setCantInteract(false);
							}

						}, 5);
					} else {
						drop();
						reset();
						setLocation(getRespawnTile());
						finish();
						if (!isSpawned())
							setRespawnTask();
						transformIntoNPC(1158);
					}
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

}
