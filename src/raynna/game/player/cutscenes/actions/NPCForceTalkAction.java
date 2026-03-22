package raynna.game.player.cutscenes.actions;

import raynna.game.ForceTalk;
import raynna.game.npc.NPC;
import raynna.game.player.Player;

public class NPCForceTalkAction extends CutsceneAction {

	private String text;

	public NPCForceTalkAction(int cachedObjectIndex, String text, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.text = text;
	}

	@Override
	public void process(Player player, Object[] cache) {
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextForceTalk(new ForceTalk(text));
	}

}
