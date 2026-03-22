package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.familiar.Familiar;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class MinotaurCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bronze Minotaur", "Iron Minotaur", "Steel Minotaur", "Mithril Minotaur",
				"Adamant Minotaur", "Rune Minotaur" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			familiar.submitSpecial(familiar.getOwner());
			npc.animate(new Animation(8026));
			npc.gfx(new Graphics(1334));
			World.sendElementalProjectile(npc, target, 1333);
		} else {
			npc.animate(new Animation(6829));
			delayHit(npc, target, 1, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 40, NpcAttackStyle.MAGIC, target)));
		}
		return npc.getAttackSpeed();
	}
}
