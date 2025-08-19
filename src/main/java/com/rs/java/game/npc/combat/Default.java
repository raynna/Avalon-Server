package com.rs.java.game.npc.combat;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.World;
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
		int attackStyle = defs.getAttackStyle();

		int damage = NpcCombatCalculations.getRandomMaxHit(
				npc,
				defs.getMaxHit(),
				attackStyle,
				target
		);

		switch (attackStyle) {
			case NPCCombatDefinitions.MELEE ->
					delayHit(npc, 0, target, getMeleeHit(npc, damage));

			case NPCCombatDefinitions.RANGE -> {
				delayHit(npc, 2, target, getRangeHit(npc, damage));
				if (defs.getAttackProjectile() != -1) {
					ProjectileManager.sendSimple(Projectile.ARROW, defs.getAttackProjectile(), npc, target);
				}
			}

			case NPCCombatDefinitions.MAGE -> {
				delayHit(npc, 2, target, getMagicHit(npc, damage));
				if (defs.getAttackProjectile() != -1) {
					ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, defs.getAttackProjectile(), npc, target);
				}
			}
		}

		if (defs.getAttackGfx() != -1) {
			npc.gfx(new Graphics(defs.getAttackGfx()));
		}
		npc.animate(new Animation(defs.getAttackEmote()));

		return defs.getAttackDelay();
	}
}
