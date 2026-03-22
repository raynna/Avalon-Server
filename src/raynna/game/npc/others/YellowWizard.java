package raynna.game.npc.others;

import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.game.player.controllers.RunespanController;
import raynna.util.Utils;

@SuppressWarnings("serial")
public class YellowWizard extends NPC {

	private RunespanController controler;
	private long spawnTime;

	public YellowWizard(WorldTile tile, RunespanController controler) {
		super(15430, tile, -1, true, true);
		spawnTime = Utils.currentTimeMillis();
		this.controler = controler;
	}

	@Override
	public void processNPC() {
		if (spawnTime + 300000 < Utils.currentTimeMillis())
			finish();
	}

	@Override
	public void finish() {
		controler.removeWizard();
		transformIntoNPC(-1);
		super.finish();
	}

	public static void giveReward(Player player) {

	}

	@Override
	public boolean withinDistance(Player tile, int distance) {
		return tile == controler.getPlayer() && super.withinDistance(tile, distance);
	}

}
