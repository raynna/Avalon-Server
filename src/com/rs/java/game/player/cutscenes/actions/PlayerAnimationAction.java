package com.rs.java.game.player.cutscenes.actions;

import com.rs.java.game.Animation;
import com.rs.java.game.player.Player;

public class PlayerAnimationAction extends CutsceneAction {

	private Animation anim;

	public PlayerAnimationAction(Animation anim, int actionDelay) {
		super(-1, actionDelay);
		this.anim = anim;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.animate(anim);
	}

}
