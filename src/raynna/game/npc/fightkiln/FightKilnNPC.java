package raynna.game.npc.fightkiln;

import java.util.ArrayList;
import java.util.List;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.game.player.controllers.FightKiln;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.game.npc.combatdata.NpcCombatDefinition;

@SuppressWarnings("serial")
public class FightKilnNPC extends NPC {

	private FightKiln controler;

	public FightKilnNPC(int id, WorldTile tile, FightKiln controler) {
		super(id, tile, -1, true, true);
		setForceMultiArea(true);
		setNoDistanceCheck(true);
		setForceTargetDistance(64);
		setForceAgressiveDistance(64);
		this.controler = controler;
	}

	private int getDeathGfx() {
		switch (getId()) {
		case 15201:
			return 2926;
		case 15202:
			return 2927;
		case 15203:
			return 2957;
		case 15213:
		case 15214:
		case 15204:
			return 2928;
		case 15205:
			return 2959;
		case 15206:
		case 15207:
			return 2929;
		case 15208:
		case 15211:
		case 15212:
			return 2973;
		default:
			return 2926;
		}
	}

	@Override
	public void sendDeath(Entity source) {
		final NpcCombatDefinition defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		animate(-1);
		controler.checkCrystal();
		gfx(new Graphics(getDeathGfx()));
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					animate(new Animation(defs.getDeathAnim()));
				} else if (loop >= defs.getDeathDelay() - 4) {
					reset();
					finish();
					controler.removeNPC();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}
