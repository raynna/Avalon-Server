package com.rs.java.game.player.cutscenes.actions;

import com.rs.java.game.Animation;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;

public class NPCAnimationAction extends CutsceneAction {

	private Animation anim;

	public NPCAnimationAction(int cachedObjectIndex, Animation anim, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.anim = anim;
	}

	@Override
	public void process(Player player, Object[] cache) {
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.animate(anim);
	}

}
