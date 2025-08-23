package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.utils.Utils;

public class OrkLegionCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ork legion" };
	}

	public String[] messages = { "For Bork!", "Die Human!", "To the attack!", "All together now!" };

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions cdef = npc.getCombatDefinitions();
		npc.animate(new Animation(cdef.getAttackEmote()));
		if (Utils.getRandom(3) == 0)
			npc.setNextForceTalk(new ForceTalk(messages[Utils.getRandom(messages.length > 3 ? 3 : 0)]));
		delayHit(npc, target, 0, getMeleeHit(npc, cdef.getMaxHit()));
		return cdef.getAttackDelay();
	}

}
