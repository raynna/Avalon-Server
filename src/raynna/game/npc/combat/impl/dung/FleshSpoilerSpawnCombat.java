package raynna.game.npc.combat.impl.dung;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.util.Utils;

public class FleshSpoilerSpawnCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 11910 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(new Animation(Utils.random(3) == 0 ? 14474 : 14475));
		delayHit(npc, target, 0, npc.meleeHit(npc, npc.getMaxHit()));
		return 3;
	}
}
