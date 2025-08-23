package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

public class BorkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bork" };
	}
	
	public boolean spawnOrk = false;

	@Override
	public int attack(NPC npc, Entity target) {
		if (npc.getHitpoints() <= (npc.getMaxHitpoints() * 0.4) && !spawnOrk) {
			Player player = (Player) target;
			npc.setNextForceTalk(new ForceTalk("Come to my aid, brothers!"));
			player.getControlerManager().startControler("BorkControler", 1, npc);
			spawnOrk = true;
		}
		npc.animate(Utils.getRandom(1) == 0 ? npc.getAttackAnimation() : 8757);
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
		delayHit(npc, target, 0, meleeHit);
		return npc.getAttackSpeed();
	}

}
