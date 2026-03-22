package raynna.game.player.cutscenes.actions;

import raynna.game.Graphics;
import raynna.game.player.Player;

public class PlayerGraphicAction extends CutsceneAction {

	private Graphics gfx;

	public PlayerGraphicAction(Graphics gfx, int actionDelay) {
		super(-1, actionDelay);
		this.gfx = gfx;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.gfx(gfx);
	}

}
