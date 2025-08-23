package com.rs.java.game.npc.combat;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.npc.NPC;
import com.rs.kotlin.game.npc.combatdata.CombatData;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class Default extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Default" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setBonuses();
		CombatData data = npc.getCombatData();
		NpcAttackStyle attackStyle = NpcAttackStyle.fromList(npc.getCombatData().attackStyles);

		int damage = NpcCombatCalculations.getRandomMaxHit(
				npc,
				data.maxHit.getMaxhit() * 10,
				attackStyle,
				target
		);

		switch (attackStyle) {
			case STAB, SLASH, CRUSH ->
					delayHit(npc, target, 0, getMeleeHit(npc, damage));

			case RANGED -> {
				delayHit(npc, target, 2, getRangeHit(npc, damage));
				if (defs.getAttackProjectile() != -1) {
					ProjectileManager.sendSimple(Projectile.ARROW, defs.getAttackProjectile(), npc, target);
				}
			}

			case MAGIC -> {
				delayHit(npc, target, 2, getMagicHit(npc, damage));
				if (defs.getAttackProjectile() != -1) {
					ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, defs.getAttackProjectile(), npc, target);
				}
			}
		}

		if (defs.getAttackGfx() != -1) {
			npc.gfx(new Graphics(defs.getAttackGfx()));
		}
		npc.animate(new Animation(defs.getAttackEmote()));

		return data.attackSpeedTicks;
	}
}
