package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.familiar.Familiar;
import raynna.game.player.Player;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class TzKihCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tz-Kih", 7361, 7362 };
	}

	@Override
	public int attack(NPC npc, Entity target) {// yoa
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int damage = 0;
		if (npc instanceof Familiar) {// TODO get anim and gfx
			Familiar familiar = (Familiar) npc;
			boolean usingSpecial = familiar.hasSpecialOn();
			if (usingSpecial) {
				for (Entity entity : npc.getPossibleTargets()) {
					damage = getRandomMaxHit(npc, 70, NpcAttackStyle.CRUSH, target);
					if (target instanceof Player)
						((Player) target).getPrayer().drainPrayer(damage);
					delayHit(npc, entity, 0, getMeleeHit(npc, damage));
				}
			}
			return npc.getAttackSpeed();
		}
		npc.animate(new Animation(defs.getAttackAnim()));
		damage = getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target);
		if (target instanceof Player)
			((Player) target).getPrayer().drainPrayer(damage + 10);
		delayHit(npc, target, 0, getMeleeHit(npc, damage));
		return npc.getAttackSpeed();
	}
}
