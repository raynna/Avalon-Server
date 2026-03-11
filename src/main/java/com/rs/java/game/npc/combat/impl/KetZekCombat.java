package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class KetZekCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ket-Zek", 15207 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		boolean inMelee = npc.isWithinMeleeRange(target);
		if (!inMelee) {
			commenceMagicAttack(npc, target);
			return npc.getAttackSpeed();
		}
		boolean melee = Utils.randomBoolean();
		if (melee) {
			npc.animate(npc.getMaxHit());
			Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
			npc.animate(npc.getAttackAnimation());
			delayHit(npc, target, 0, meleeHit);
		} else {
			commenceMagicAttack(npc, target);
		}
		return npc.getAttackSpeed();
	}

	private void commenceMagicAttack(final NPC npc, final Entity target) {
		npc.animate(16136);
		Hit mageHit = npc.magicHit(target, npc.getMaxHit());
		ProjectileManager.send(Projectile.KET_ZEK, 2984, new Graphics(2983, 0, 96 << 16), npc, target, () -> {
			applyRegisteredHit(npc, target, mageHit);
		});
	}
}
