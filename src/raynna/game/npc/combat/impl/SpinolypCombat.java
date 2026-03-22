package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.player.Player;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class SpinolypCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Spinolyp" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackAnim()));
		World.sendNPCProjectile(npc, target, npc.getCombatDefinitions().getAttackProjectile());
		// range based magic attack
		int damage = getRandomMaxHit(npc, 10, NpcAttackStyle.MAGIC, target);
		delayHit(npc, target, 1, getMagicHit(npc, damage));
		// drain prayer points on sucessfull hit
		if (damage > 0) {
			if (target instanceof Player) {
				Player p2 = (Player) target;
				boolean spectral = p2.getEquipment().getShieldId() == 13744;
				p2.getPrayer().drainPrayer(spectral ? 5 : 10);
			}
		}
		return npc.getAttackSpeed();
	}
}
