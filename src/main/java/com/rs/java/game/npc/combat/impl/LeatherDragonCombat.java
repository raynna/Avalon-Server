package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.DragonFire;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class LeatherDragonCombat extends CombatScript {

	private static final int DRAGON_SLAM_ANIMATION = 80, DRAGON_HEADBUTT_ANIMATION = 91, DRAGONFIRE_BREATH_ANIMATION = 84, DRAGON_DEATH_ANIMATION = 92;
	private static final int DRAGONFIRE_GFX = 1, DRAGONFIRE_TOXIC_PROJECTILE = 393, DRAGONFIRE_NORMAL_PROJECTILE = 394, DRAGONFIRE_ICY_PROJECTILE = 395, DRAGONFIRE_SHOCKING_PROJECTILE = 396;

	private static final int NEW_DRAGON_MELEE_ANIMATION = 12252, NEW_DRAGON_FIRE_ANIMATION = 12259;


	@Override
	public Object[] getKeys() {
		return new Object[] {
				"Green dragon", "Blue dragon", "Red dragon", "Black dragon",
				"Brutal green dragon", 742, 14548
		};
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		if (!isWithinMeleeRange(npc, target)) {
			return 0;
		}

		final NpcCombatDefinition defs = npc.getCombatDefinitions();

		// 75% chance melee, 25% chance dragonfire
		if (Utils.roll(3, 4)) {
			performMeleeAttack(npc, target, defs);
		} else {
			performDragonfireAttack(npc, target, defs);
		}

		return npc.getAttackSpeed();
	}

	private boolean isWithinMeleeRange(NPC npc, Entity target) {
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		return distanceX <= size && distanceX >= -1 && distanceY <= size && distanceY >= -1;
	}

	private void performMeleeAttack(NPC npc, Entity target, NpcCombatDefinition defs) {
	npc.animate(new Animation(NEW_DRAGON_MELEE_ANIMATION));

		int damage = NpcCombatCalculations.getRandomMaxHit(
				npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target
		);

		delayHit(npc, target, 0, getMeleeHit(npc, damage));
	}

	private void performDragonfireAttack(NPC npc, Entity target, NpcCombatDefinition defs) {
		if (!(target instanceof Player player)) {
			return;
		}

		npc.animate(new Animation(NEW_DRAGON_FIRE_ANIMATION));

		npc.gfx(DRAGONFIRE_GFX, 100);

		int rawDamage = Utils.getRandom(650);
		int mitigatedDamage = DragonFire.applyDragonfireMitigation(player, rawDamage, true);

		delayHit(npc, player, 1, getRegularHit(npc, mitigatedDamage));

		DragonFire.handleDragonfireShield(player);
	}
}
