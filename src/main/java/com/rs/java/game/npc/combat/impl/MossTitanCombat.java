package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.controlers.WildernessControler;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

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
		if (target.isAtMultiArea() && WildernessControler.isAtWild(target)) {
			delayHit(npc, target, 1, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 160, NpcAttackStyle.MAGIC, target)));
			World.sendElementalProjectile(npc, target, 1462);
			if (Utils.getRandom(3) == 0)// 1/3 chance of being poisioned
				target.getPoison().makePoisoned(58);
		}
	}
}
