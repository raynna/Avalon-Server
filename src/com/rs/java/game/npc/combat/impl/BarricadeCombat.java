package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;

public class BarricadeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		// TODO Auto-generated method stub
		return new Object[] { "Barricade" };
	}

	/*
	 * empty
	 */
	@Override
	public int attack(NPC npc, Entity target) {
		return 0;
	}

}
