package com.rs.java.game.npc.combat;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.npc.NPC;
import com.rs.kotlin.game.npc.combatdata.CombatData;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class Default extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Default" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NpcCombatDefinition defs = npc.getCombatDefinitions();
		CombatData data = npc.getCombatData();
		NpcAttackStyle attackStyle = NpcAttackStyle.fromList(npc.getCombatData().attackStyles);

		int damage = NpcCombatCalculations.getRandomMaxHit(
				npc,
				npc.getMaxHit(),
				attackStyle,
				target
		);

		switch (attackStyle) {
			case STAB, SLASH, CRUSH ->
					delayHit(npc, target, 0, getMeleeHit(npc, damage));

			case RANGED -> {
				delayHit(npc, target, 2, getRangeHit(npc, damage));
				System.out.println("npc: " + npc.getName() + " attackGfx: " + defs.getAttackGfx() + ", projectile: " + defs.getAttackProjectile());
				if (defs.getAttackProjectile() != -1) {
					ProjectileManager.sendSimple(Projectile.ARROW, defs.getAttackProjectile(), npc, target);
				}
			}

			case MAGIC -> {
				delayHit(npc, target, 2, getMagicHit(npc, damage));
				System.out.println("npc: " + npc.getName() + " attackGfx: " + defs.getAttackGfx() + ", projectile: " + defs.getAttackProjectile());
				if (defs.getAttackProjectile() != -1) {
					ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, defs.getAttackProjectile(), npc, target);
				}
			}
		}

		if (defs.getAttackGfx() != -1) {
			npc.gfx(new Graphics(defs.getAttackGfx()));
		}
		npc.animate(new Animation(defs.getAttackAnim()));
		if (defs.getAttackSound() != -1)
			npc.playSound(defs.getAttackSound(), 1);
		System.out.println("Default attack for NPC id: " + npc.getId() + " using " + attackStyle + " for max hit: " + damage + ", Attack speed: " + data.attackSpeedTicks);
		return npc.getAttackSpeed();
	}
}
