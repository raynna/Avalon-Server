package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.familiar.Familiar;
import raynna.game.player.controllers.WildernessController;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class MossTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7330, 7329 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// priority over regular attack
			npc.animate(new Animation(8223));
			npc.gfx(new Graphics(1460));
			for (Entity targets : npc.getPossibleTargets()) {
				if (targets.equals(target) && !targets.isAtMultiArea())
					continue;
				sendSpecialAttack(targets, npc);
			}
			sendSpecialAttack(target, npc);
		} else {
			damage = NpcCombatCalculations.getRandomMaxHit(npc, 160, NpcAttackStyle.CRUSH, target);
			npc.animate(new Animation(8222));
			delayHit(npc, target, 1, getMeleeHit(npc, damage));
		}
		return npc.getAttackSpeed();
	}

	public void sendSpecialAttack(Entity target, NPC npc) {
		if (target.isAtMultiArea() && WildernessController.isAtWild(target)) {
			delayHit(npc, target, 1, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 160, NpcAttackStyle.MAGIC, target)));
			World.sendElementalProjectile(npc, target, 1462);
			if (Utils.getRandom(3) == 0)// 1/3 chance of being poisioned
				target.getPoison().makePoisoned(58);
		}
	}
}
