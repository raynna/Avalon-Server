package raynna.game.player.cutscenes.actions;

import raynna.game.player.Player;

public class PlayerTransformAction extends CutsceneAction {

	private int npcId;

	public PlayerTransformAction(int npcId, int actionDelay) {
		super(-1, actionDelay);
		this.npcId = npcId;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.getAppearance().transformIntoNPC(npcId);
	}

}
