package raynna.game.npc.fightcaves;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.WorldTile;
import raynna.game.player.controllers.FightCaves;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.game.npc.combatdata.NpcCombatDefinition;

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
