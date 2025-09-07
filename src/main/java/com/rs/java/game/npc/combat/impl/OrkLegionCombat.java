package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class OrkLegionCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ork legion" };
	}

	public String[] messages = { "For Bork!", "Die Human!", "To the attack!", "All together now!" };

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition cdef = npc.getCombatDefinitions();
		npc.animate(new Animation(cdef.getAttackAnim()));
		if (Utils.getRandom(3) == 0)
			npc.setNextForceTalk(new ForceTalk(messages[Utils.getRandom(messages.length > 3 ? 3 : 0)]));
		delayHit(npc, target, 0, getMeleeHit(npc, cdef.getMaxHit()));
		return npc.getAttackSpeed();
	}

}
