package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.ForceTalk;
import raynna.game.Graphics;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.player.Player;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class ThunderCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 11872 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		if (Utils.getRandom(4) == 0) {
			switch (Utils.getRandom(3)) {
			case 0:
			npc.setNextForceTalk(new ForceTalk("RAAAAAARRRRRRGGGGHHHH"));
			break;
		case 1:
			npc.setNextForceTalk(new ForceTalk("You're going straight to hell!"));
			break;
		case 2:
			String name = "";
			if (target instanceof Player)
				name = ((Player) target).getDisplayName();
			npc.setNextForceTalk(new ForceTalk("I'm going to crush you, " + name));
			break;
		case 3:
			name = "";
			if (target instanceof Player)
				name = ((Player) target).getDisplayName();
			npc.setNextForceTalk(new ForceTalk("Die with pain, " + name));
			break;
		}
		}
		//TODO: Get right gfx
		if (Utils.getRandom(1) == 0) { // mage magical attack
			npc.setCapDamage(800);
			npc.animate(new Animation(14525));
			npc.setNextForceTalk(new ForceTalk("FUS RO DAH"));
			npc.playSound(168, 2);
			for (Entity t : npc.getPossibleTargets()) {
				if (!t.withinDistance(npc, 18))
					continue;
				int damage = getRandomMaxHit(npc, defs.getMaxHit(),
						NpcAttackStyle.MAGIC, t);
				if (damage > 0) {
					delayHit(npc, t, 1, getMagicHit(npc, damage));
					t.gfx(new Graphics(3428));
				}
			}

		} else { // melee attack
			npc.animate(new Animation(defs.getAttackAnim()));
			npc.setCapDamage(800);
			delayHit(
					npc,
                    target, 0,
                    getMeleeHit(
							npc,
							getRandomMaxHit(npc, defs.getMaxHit(),
									NpcAttackStyle.CRUSH, target)));
		}
		return npc.getAttackSpeed();
	}
}