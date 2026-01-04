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
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class MetalDragonCombat extends CombatScript {

	private static final int DRAGON_SLAM_ANIMATION = 80, DRAGON_HEADBUTT_ANIMATION = 13158, DRAGONFIRE_ANIMATION = 13160, DRAGONFIRE_BREATH_ANIMATION = 13164, DRAGON_DEATH_ANIMATION = 92;
	private static final int DRAGONFIRE_GFX = 1, DRAGONFIRE_TOXIC_PROJECTILE = 394, DRAGONFIRE_NORMAL_PROJECTILE = 393, DRAGONFIRE_ICY_PROJECTILE = 395, DRAGONFIRE_SHOCKING_PROJECTILE = 396;


	@Override
	public Object[] getKeys() {
		return new Object[] { "Bronze dragon", "Iron dragon", "Steel dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		if (!isWithinMeleeRange(npc, target)) {
			performDragonfireAttack(npc, target);
		} else {
			if (Utils.getRandom(2) == 0) {
				performMeleeAttack(npc, target);
			} else {
				performDragonfireBreath(npc, target, defs);
			}
		}
		return npc.getAttackSpeed();
	}

	private boolean isWithinMeleeRange(NPC npc, Entity target) {
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		return distanceX <= size && distanceX >= -1 && distanceY <= size && distanceY >= -1;
	}

	private void performMeleeAttack(NPC npc, Entity target) {
		NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(Utils.roll(1, 2) ? DRAGON_HEADBUTT_ANIMATION : DRAGON_HEADBUTT_ANIMATION));
		int damage = NpcCombatCalculations.getRandomMaxHit(
				npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target
		);
		delayHit(npc, target, 0, getMeleeHit(npc, damage));
	}

	private void performDragonfireBreath(NPC npc, Entity target, NpcCombatDefinition defs) {
		if (!(target instanceof Player player)) {
			return;
		}

		npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION));

		npc.gfx(DRAGONFIRE_GFX, 100);

		int rawDamage = Utils.getRandom(650);
		int mitigatedDamage = DragonFire.applyDragonfireMitigation(player, rawDamage, true);

		delayHit(npc, player, 1, getRegularHit(npc, mitigatedDamage));

		DragonFire.handleDragonfireShield(player);
	}

	private void performDragonfireAttack(NPC npc, Entity target) {
		if (!(target instanceof Player player)) return;

		int rawDamage = Utils.getRandom(650);

		npc.animate(new Animation(DRAGONFIRE_ANIMATION));
		ProjectileManager.sendSimple(Projectile.DRAGONFIRE, DRAGONFIRE_NORMAL_PROJECTILE, npc, target);

		int mitigated = DragonFire.applyDragonfireMitigation(player, rawDamage, false);
		delayHit(npc, player, Utils.getDistance(player, npc) > 2 ? 2 : 1, getRegularHit(npc, mitigated));

		DragonFire.handleDragonfireShield(player);
	}
}
