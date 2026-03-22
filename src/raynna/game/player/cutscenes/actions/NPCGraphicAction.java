package raynna.game.player.cutscenes.actions;

import raynna.game.Graphics;
import raynna.game.npc.NPC;
import raynna.game.player.Player;

public class NPCGraphicAction extends CutsceneAction {

	private Graphics gfx;

	public NPCGraphicAction(int cachedObjectIndex, Graphics gfx, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.gfx = gfx;
	}

	@Override
	public void process(Player player, Object[] cache) {
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.gfx(gfx);
	}

}
