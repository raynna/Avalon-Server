package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.utils.Utils;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.Player;

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

		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.getRandom(2) == 0) { // magical attack
			npc.animate(new Animation(15042));
			for (Entity t : npc.getPossibleTargets()) {
				delayHit(
						npc,
						1,
						t,
						getMagicHit(
								npc,
								getRandomMaxHit(npc, 540,
										NPCCombatDefinitions.MAGE, t)));
				World.sendProjectile(npc, t, 1002, 41, 16, 41, 35, 16, 0);
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
					0,
					target,
					getMeleeHit(
							npc,
							getRandomMaxHit(npc, 330,
									NPCCombatDefinitions.MELEE, target)));
		} else { // melee attack
			npc.animate(new Animation(15046));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getRandomMaxHit(npc, 440,
									NPCCombatDefinitions.MELEE, target)));
		}
		return defs.getAttackDelay();
	}

}