package raynna.game.npc.combat.impl;

import raynna.game.Entity;
import raynna.game.ForceTalk;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.player.Player;
import raynna.util.Utils;

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
