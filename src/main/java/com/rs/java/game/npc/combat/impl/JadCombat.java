package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.world.projectile.ProjectileManager;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.task.WorldTaskBuilder;
import com.rs.kotlin.game.world.task.WorldTaskDsl;
import com.rs.kotlin.game.world.task.WorldTasks;

public class JadCombat extends CombatScript {

	private final int RANGE_ANIM = 16202;
	private final int RANGE_GFX = 2994;

	private final int MAGIC_ANIM = 16195;
	private final int MAGIC_PROJECTILE = 2996;
	private final int MAGIC_GFX = 2995;

	@Override
	public Object[] getKeys() {
		return new Object[] { 2745, 15208 };
	}

    enum JadAttack { MELEE, RANGE, MAGIC }

	@Override
	public int attack(final NPC npc, final Entity target) {
		JadAttack attack;
		boolean inMelee = npc.isWithinMeleeRange(target);
		if (inMelee) {
			attack = Utils.randomOf(JadAttack.MELEE, JadAttack.MAGIC, JadAttack.RANGE);
		} else {
			attack = Utils.randomOf(JadAttack.RANGE, JadAttack.MAGIC);
		}
		switch (attack) {
			case MELEE -> performMeleeAttack(npc, target);
			case RANGE -> performRangeAttack(npc, target);
			case MAGIC -> performMagicAttack(npc, target);
		}
		return npc.getAttackSpeed();
	}

	private void performMeleeAttack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
		delayHit(npc, target, 0, meleeHit);
	}

	private void performRangeAttack(NPC npc, Entity target) {
		npc.animate(RANGE_ANIM);
		npc.animate(RANGE_GFX);
		WorldTasks.submit(3, () -> {
			Hit rangeHit = npc.rangedHit(target, npc.getMaxHit());
			delayHit(npc, target, 1, rangeHit);
			target.gfx(new Graphics(3000));
		});
	}

	private void performMagicAttack(NPC npc, Entity target) {
		npc.animate(MAGIC_ANIM);
		npc.gfx(MAGIC_GFX);
		WorldTasks.submit(3, () -> {
			Hit magicHit = npc.magicHit(target, npc.getMaxHit());
			ProjectileManager.send(Projectile.JAD_MAGE, MAGIC_PROJECTILE, npc, target, () -> {
				applyRegisteredHit(npc, target, magicHit);
			});
		});
	}

}
