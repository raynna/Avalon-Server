package raynna.game.player.cutscenes.actions;

import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.game.player.cutscenes.Cutscene;

public class NPCFaceTileAction extends CutsceneAction {

	private int x, y;

	public NPCFaceTileAction(int cachedObjectIndex, int x, int y, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.x = x;
		this.y = y;
	}

	@Override
	public void process(Player player, Object[] cache) {
		Cutscene scene = (Cutscene) cache[0];
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextFaceWorldTile(new WorldTile(scene.getBaseX() + x, scene.getBaseY() + y, npc.getPlane()));
	}

}
