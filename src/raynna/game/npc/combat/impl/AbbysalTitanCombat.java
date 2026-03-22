package raynna.game.npc.combat.impl;

import raynna.game.Entity;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.player.Player;

public class AbbysalTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7350, 7349 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(7980);
		npc.gfx(1490);
		Hit meleeHit = npc.meleeHit(target, 140);
		if (target instanceof Player player) {
            if (meleeHit.getDamage() > 0 && player.getPrayer().getPrayerPoints() > 0)
				player.getPrayer().drainPrayer(meleeHit.getDamage()  / 2);
		}
		delayHit(npc, target, 0, meleeHit);
		return npc.getAttackSpeed();
	}
}
