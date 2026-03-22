package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.player.Player;
import raynna.game.player.prayer.AncientPrayer;
import raynna.game.player.prayer.NormalPrayer;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class JadinkoCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 13820, 13821, 13822 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NpcCombatDefinition defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		if (target instanceof Player) {
			Player player = (Player) target;
			if (player.getPrayer().isActive(NormalPrayer.PROTECT_FROM_MAGIC) || player.getPrayer().isActive(AncientPrayer.DEFLECT_MAGIC)) {
				npc.setForceFollowClose(true);
				meleeAttack(npc, target);
				return npc.getAttackSpeed();
			} else {
				npc.setForceFollowClose(false);
				if ((distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)) {
					magicAttack(npc, target);
					return npc.getAttackSpeed();
				} else {
					switch (Utils.random(2)) {
					case 0:
						magicAttack(npc, target);
						break;
					case 1:
					default:
						meleeAttack(npc, target);
					}
				}
				return npc.getAttackSpeed();
			}
		} else
			return npc.getAttackSpeed();
	}

	private void magicAttack(NPC npc, Entity target) {
		npc.animate(new Animation(npc.getId() == 13820 ? 3031 : 3215));
		delayHit(npc, target, 2, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.MAGIC, target)));
	}

	private void meleeAttack(NPC npc, Entity target) {
		npc.animate(new Animation(npc.getId() == 13820 ? 3009 : 3214));
		delayHit(npc, target, 0, getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.SLASH, target)));
	}
}
