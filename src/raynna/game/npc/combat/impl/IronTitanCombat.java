package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.familiar.Familiar;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class IronTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7376, 7375 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		boolean distant = false;
		int size = npc.getSize();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		int[] damages = { 0, 0, 0 };
		if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)
			distant = true;
		if (usingSpecial) {// priority over regular attack
			npc.animate(new Animation(7954));
			npc.gfx(new Graphics(1450));
			if (distant) {// Mage Hit & Magic Xp
				for (Integer appliedDamage : damages) {
					if (target instanceof Player) {
						appliedDamage = NpcCombatCalculations.getRandomMaxHit(npc,
								230,
								NpcAttackStyle.MAGIC, target);
					} else {
						appliedDamage = NpcCombatCalculations.getRandomMaxHit(npc, 230, NpcAttackStyle.MAGIC, target);
					}
					delayHit(npc, target, 2, getMagicHit(npc, appliedDamage));
					long familiarDelay = 3000;
					familiar.getOwner().addFamiliarDelay(familiarDelay);
					familiar.getOwner().getSkills().addXp(Skills.MAGIC, appliedDamage / 3);
				}
			} else {// Melee Hit & Defence Xp
				for (Integer appliedDamage : damages) {
					if (target instanceof Player) {
						appliedDamage = NpcCombatCalculations.getRandomMaxHit(npc,
								230,
								NpcAttackStyle.CRUSH, target);
					} else {
						appliedDamage = NpcCombatCalculations.getRandomMaxHit(npc, 230, NpcAttackStyle.CRUSH, target);
					}
					delayHit(npc, target, 2, getMeleeHit(npc, appliedDamage));
					long familiarDelay = 3000;
					familiar.getOwner().addFamiliarDelay(familiarDelay);
					familiar.getOwner().getSkills().addXp(Skills.DEFENCE, appliedDamage / 3);
				}
			}
		} else {
			if (distant) {
				damage = NpcCombatCalculations.getRandomMaxHit(npc, 255, NpcAttackStyle.MAGIC, target);
				npc.animate(new Animation(7694));
				World.sendSlowBowProjectile(npc, target, 1452);
				delayHit(npc, target, Utils.getDistance(npc, target) > 3 ? 3 : 2, getMagicHit(npc, damage));
				familiar.getOwner().getSkills().addXp(Skills.MAGIC, damage / 3);
			} else {// melee
				damage = NpcCombatCalculations.getRandomMaxHit(npc, 244, NpcAttackStyle.CRUSH, target);
				npc.animate(new Animation(7946));
				npc.gfx(new Graphics(1447));
				delayHit(npc, target, 1, getMeleeHit(npc, damage));
				familiar.getOwner().getSkills().addXp(Skills.DEFENCE, damage / 3);
			}
		}
		return npc.getAttackSpeed();
	}

}
