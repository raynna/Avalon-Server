package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class SteelTitanCombat extends CombatScript {

	private static final int SPECIAL_HIT_MAX = 244;
	private static final int REGULAR_MELEE_MAX = 244;
	private static final int REGULAR_MAGIC_MAX = 255;
	private static final int REGULAR_RANGE_MAX = 244;
	private static final int SPECIAL_ANIMATION = 8190;
	private static final int MELEE_ANIMATION = 8183;
	private static final int ATTACK_ANIMATION = 7694;


	private static final int SPECIAL_GRAPHIC = 1449;
	private static final int SPECIAL_GRAPHIC_2 = 1450;
	private static final int RANGE_PROJECTILE = 1445;
	private static final int RANGE_HIT_GFX = 1448;
	private static final int MAGE_HIT_GFX = 1451;

	private static final long FAMILIAR_DELAY = 3000;
	private static final int SPECIAL_HIT_COUNT = 4;

	@Override
	public Object[] getKeys() {
		return new Object[]{7344, 7343};
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;

		boolean usingSpecial = familiar.hasSpecialOn();
		boolean distant = isDistant(npc, target);

		if (usingSpecial) {
			performSpecialAttack(familiar, npc, target, distant);
		} else {
			performRegularAttack(familiar, npc, target, distant);
		}

		return npc.getAttackSpeed() + 1;
	}

	private boolean isDistant(NPC npc, Entity target) {
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		return distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1;
	}

	private void performSpecialAttack(Familiar familiar, NPC npc, Entity target, boolean distant) {
		npc.animate(SPECIAL_ANIMATION);
		npc.gfx(SPECIAL_GRAPHIC);
		npc.gfx(SPECIAL_GRAPHIC_2);
		for (int i = 0; i < SPECIAL_HIT_COUNT; i++) {
			if (distant) {
				Hit rangeHit = npc.rangedHit(target, SPECIAL_HIT_MAX);
				delayHit(npc, target, 2, rangeHit);
				applyFamiliarXP(familiar, rangeHit.getDamage(), Skills.RANGE);
			} else {
				Hit meleeHit = npc.meleeHit(target, SPECIAL_HIT_MAX);
				delayHit(npc, target, 2, meleeHit);
				applyFamiliarXP(familiar, meleeHit.getDamage(), Skills.STRENGTH);
			}

			familiar.getOwner().addFamiliarDelay(FAMILIAR_DELAY);
		}
	}

	private void performRegularAttack(Familiar familiar, NPC npc, Entity target, boolean distant) {
		int attackType = distant ? Utils.getRandom(2) : 2;

		switch (attackType) {
			case 0:
				npc.animate(ATTACK_ANIMATION);
				npc.gfx(1444);
				Hit mageHit = npc.magicHit(target, REGULAR_MAGIC_MAX);
				ProjectileManager.send(Projectile.ELEMENTAL_SPELL, 1445, npc, target, () -> {
					applyRegisteredHit(npc, target, mageHit);
					target.gfx(1448, 100);
				});
				applyFamiliarXP(familiar, mageHit.getDamage(), Skills.MAGIC);
				break;

			case 1: // range
				npc.animate(ATTACK_ANIMATION);
				npc.gfx(1444);
				Hit rangeHit = npc.rangedHit(target, REGULAR_RANGE_MAX);
				ProjectileManager.send(Projectile.ELEMENTAL_SPELL, 1445, npc, target, () -> {
					applyRegisteredHit(npc, target, rangeHit);
					target.gfx(1448, 100);
				});
				applyFamiliarXP(familiar, rangeHit.getDamage(), Skills.RANGE);
				break;
			case 2:
				npc.animate(MELEE_ANIMATION);
				Hit meleeHit = npc.meleeHit(target, REGULAR_MELEE_MAX);
				delayHit(npc, target, 0, meleeHit);
				applyFamiliarXP(familiar, meleeHit.getDamage(), Skills.STRENGTH);
				break;

		}
	}

	private void applyFamiliarXP(Familiar familiar, int damage, int skill) {
		Player owner = familiar.getOwner();
		if (owner.toggles("ONEXPPERHIT", false)) {
			int xp = damage;
			owner.getSkills().addXpNoBonus(skill, xp);
		} else {
			owner.getSkills().addXp(skill, damage / 3);
		}
	}
}
