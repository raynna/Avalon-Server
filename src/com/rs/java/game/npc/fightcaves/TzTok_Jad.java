package com.rs.java.game.npc.fightcaves;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.player.controlers.FightCaves;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;

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
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		animate(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					animate(new Animation(defs.getDeathEmote()));
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
