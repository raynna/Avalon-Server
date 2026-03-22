package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.familiar.Familiar;
import raynna.game.player.Player;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class LavaTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7342, 7341 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// priority over regular attack
			npc.animate(new Animation(7883));
			npc.gfx(new Graphics(1491));
			delayHit(npc, target, 1, getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 140, NpcAttackStyle.CRUSH, target)));
			if (damage <= 4 && target instanceof Player) {
				Player player = (Player) target;
				player.getCombatDefinitions()
						.decreaseSpecialAttack((player.getCombatDefinitions().getSpecialAttackPercentage() / 10));
			}
		} else {
			damage = NpcCombatCalculations.getRandomMaxHit(npc, 140, NpcAttackStyle.CRUSH, target);
			npc.animate(new Animation(7980));
			npc.gfx(new Graphics(1490));
			delayHit(npc, target, 1, getMeleeHit(npc, damage));
		}
		if (Utils.getRandom(10) == 0)// 1/10 chance of happening
			delayHit(npc, target, 1, getMeleeHit(npc, Utils.getRandom(50)));
		return npc.getAttackSpeed();
	}
}
