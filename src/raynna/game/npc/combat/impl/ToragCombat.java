package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.player.Player;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class ToragCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2029 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackAnim()));
		Hit hit = npc.meleeHit(target, defs.getMaxHit() / 2);
		Hit hit2 = npc.meleeHit(target, defs.getMaxHit() / 2);
		if ((hit.getDamage() != 0  || hit2.getDamage() != 0) && target instanceof Player targetPlayer && Utils.random(3) == 0) {
			target.gfx(new Graphics(399));
            targetPlayer.setRunEnergy(targetPlayer.getRunEnergy() > 4 ? targetPlayer.getRunEnergy() - 4 : 0);
		}
		delayHit(npc, target, 0, hit);
		delayHit(npc, target, 0, hit2);
		return npc.getAttackSpeed();
	}
}
