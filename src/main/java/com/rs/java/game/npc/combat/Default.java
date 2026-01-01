package com.rs.java.game.npc.combat;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.kotlin.game.npc.combatdata.AttackStyle;
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
		AttackStyle definitionAttackStyle = defs.getAttackStyle();

		switch (definitionAttackStyle) {
			case AttackStyle.MELEE -> {
				NpcAttackStyle attackStyle = NpcAttackStyle.fromList(npc.getCombatData().attackStyles);
				switch (attackStyle) {
					case STAB, SLASH, CRUSH -> {
						Hit meleeHit = npc.meleeHit(target, npc.getMaxHit(), attackStyle);
						delayHit(npc, target, 0, meleeHit);
					}

				}
			}
			case AttackStyle.RANGE -> {
				Hit rangeHit = npc.rangedHit(target, npc.getMaxHit());
				if (defs.getAttackProjectile() != -1) {
					ProjectileManager.send(Projectile.ARROW, defs.getAttackProjectile(), npc, target, () -> {
						applyRegisteredHit(npc, target, rangeHit);
					});
				} else {
					delayHit(npc, target, npc.getHitDelay(npc, target), rangeHit);
				}
			}

			case AttackStyle.MAGIC -> {
				Hit mageHit = npc.magicHit(target, npc.getMaxHit());
				if (defs.getAttackProjectile() != -1) {
					ProjectileManager.send(Projectile.ELEMENTAL_SPELL, defs.getAttackProjectile(), npc, target, () -> {
						applyRegisteredHit(npc, target, mageHit);
					});
				} else {
					delayHit(npc, target, npc.getHitDelay(npc, target), mageHit);
				}
			}
		}

		if (defs.getAttackGfx() != -1) {
			npc.gfx(new Graphics(defs.getAttackGfx()));
		}
		npc.animate(new Animation(defs.getAttackAnim()));
		if (defs.getAttackSound() != -1)
			npc.playSound(defs.getAttackSound(), 1);
		return npc.getAttackSpeed();
	}
}
