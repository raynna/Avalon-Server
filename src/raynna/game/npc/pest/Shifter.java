package raynna.game.npc.pest;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.WorldTile;
import raynna.game.minigames.pest.PestControl;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;

@SuppressWarnings("serial")
public class Shifter extends PestMonsters {

	public Shifter(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned,
			int index, PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned, index, manager);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		Entity target = this.getPossibleTargets().get(0);
		if (this.getCombat().process() && !this.withinDistance(target, 10) || Utils.random(15) == 0)
			teleportSpinner(target);
	}

	private void teleportSpinner(WorldTile tile) { // def 3902, death 3903
		setNextWorldTile(tile);
		animate(new Animation(3904));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				gfx(new Graphics(654));// 1502
			}
		});
	}
}
