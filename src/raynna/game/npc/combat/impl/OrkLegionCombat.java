package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.ForceTalk;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class OrkLegionCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ork legion" };
	}

	public String[] messages = { "For Bork!", "Die Human!", "To the attack!", "All together now!" };

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		if (Utils.getRandom(3) == 0)
			npc.setNextForceTalk(new ForceTalk(messages[Utils.getRandom(messages.length > 3 ? 3 : 0)]));
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
		delayHit(npc, target, 0, meleeHit);
		return npc.getAttackSpeed();
	}

}
