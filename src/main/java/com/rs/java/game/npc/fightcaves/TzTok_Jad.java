package com.rs.java.game.npc.fightcaves;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.controllers.FightCaves;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

@SuppressWarnings("serial")
public class TzTok_Jad extends FightCavesNPC {

	private boolean spawnedMinions;
	private FightCaves controler;

	public TzTok_Jad(int id, WorldTile tile, FightCaves controler) {
		super(id, tile);
		setForceMultiArea(true);
		setNoDistanceCheck(true);
		setForceAgressive(true);
		setForceTargetDistance(64);
		setForceAgressiveDistance(64);
		this.controler = controler;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (!spawnedMinions && getHitpoints() < getMaxHitpoints() / 2) {
			spawnedMinions = true;
			controler.spawnHealers();
		}
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
					gfx(new Graphics(2924 + getSize()));
				} else if (loop >= defs.getDeathDelay()) {
					reset();
					finish();
					controler.win();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

}
