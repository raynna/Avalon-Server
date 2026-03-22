package raynna.game.player.cutscenes.actions;

import raynna.game.Animation;
import raynna.game.npc.NPC;
import raynna.game.player.Player;

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
