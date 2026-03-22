package raynna.game.npc.combat.impl;

import raynna.game.*;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;
import raynna.game.world.projectile.Projectile;
import raynna.game.world.projectile.ProjectileManager;

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
