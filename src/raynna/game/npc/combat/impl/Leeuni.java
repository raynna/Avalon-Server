package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.ForceTalk;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.util.Utils;
import raynna.game.player.Skills;
import raynna.game.player.Player;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class Leeuni extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13216 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		if (npc.getHitpoints() < npc.getMaxHitpoints() / 2
				&& Utils.random(5) == 0) {
			npc.heal(30);
		}
		npc.setCombatLevel(512);
		npc.setCapDamage(700);

		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		if (Utils.getRandom(2) == 0) { // magical attack
			npc.animate(new Animation(15042));
			for (Entity t : npc.getPossibleTargets()) {
				delayHit(
						npc,
                        t, 1,
                        getMagicHit(
								npc,
								NpcCombatCalculations.getRandomMaxHit(npc, 540,
										NpcAttackStyle.MAGIC, t)));
				World.sendElementalProjectile(npc, t, 1002);
				target.gfx(new Graphics(3000));
			}
		} else if (Utils.getRandom(2) == 1) {
			npc.animate(new Animation(15046));
			npc.setNextForceTalk(new ForceTalk("You will never kill me!"));
						int skill = Utils.getRandom(2);
						skill = skill == 0 ? Skills.PRAYER
								: (skill == 1 ? Skills.SUMMONING
										: Skills.PRAYER);
						Player player = (Player) target;
						if (skill == Skills.PRAYER)
							player.getPrayer().drainPrayer(990);
						else {
							int lvl = player.getSkills().getLevel(skill);
							lvl -= 1 + Utils.getRandom(4);
							player.getSkills().set(skill, lvl < 0 ? 0 : lvl);
						}
						player.message("Your " + Skills.SKILL_NAME[skill]
								+ " has been dropped!");
			
			delayHit(
					npc,
                    target, 0,
                    getMeleeHit(
							npc,
							NpcCombatCalculations.getRandomMaxHit(npc, 330,
									NpcAttackStyle.CRUSH, target)));
		} else { // melee attack
			npc.animate(new Animation(15046));
			delayHit(
					npc,
                    target, 0,
                    getMeleeHit(
							npc,
							NpcCombatCalculations.getRandomMaxHit(npc, 440,
									NpcAttackStyle.CRUSH, target)));
		}
		return npc.getAttackSpeed();
	}

}