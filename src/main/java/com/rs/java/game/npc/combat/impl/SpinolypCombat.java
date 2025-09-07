package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.player.Player;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

import static com.rs.java.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

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
