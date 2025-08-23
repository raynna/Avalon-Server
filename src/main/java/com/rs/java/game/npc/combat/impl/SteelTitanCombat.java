package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class SteelTitanCombat extends CombatScript {

	private static final int SPECIAL_HIT_MAX = 244;
	private static final int REGULAR_MELEE_MAX = 244;
	private static final int REGULAR_MAGIC_MAX = 255;
	private static final int REGULAR_RANGE_MAX = 244;
	private static final int SPECIAL_ANIMATION = 8190;
	private static final int MELEE_ANIMATION = 8183;
	private static final int MAGIC_ANIMATION = 7694;


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
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;

		boolean usingSpecial = familiar.hasSpecialOn();
		boolean distant = isDistant(npc, target);

		if (usingSpecial) {
			performSpecialAttack(familiar, npc, target, distant);
		} else {
			performRegularAttack(familiar, npc, target, distant);
		}

		return defs.getAttackDelay() + 1;
	}

	private boolean isDistant(NPC npc, Entity target) {
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		return distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1;
	}

	private void performSpecialAttack(Familiar familiar, NPC npc, Entity target, boolean distant) {
		npc.animate(new Animation(SPECIAL_ANIMATION));
		npc.gfx(new Graphics(SPECIAL_GRAPHIC));
		npc.gfx(new Graphics(SPECIAL_GRAPHIC_2));
		for (int i = 0; i < SPECIAL_HIT_COUNT; i++) {
			int damage = NpcCombatCalculations.getRandomMaxHit(npc, SPECIAL_HIT_MAX,
					distant ? NpcAttackStyle.RANGED : NpcAttackStyle.CRUSH, target);

			if (distant) {
				ProjectileManager.sendWithGraphic(Projectile.ELEMENTAL_SPELL, RANGE_PROJECTILE, npc, target, new Graphics(RANGE_HIT_GFX, 100));
				delayHit(npc, target, 2, getRangeHit(npc, damage));
				applyFamiliarXP(familiar, damage, Skills.RANGE);
			} else {
				delayHit(npc, target, 2, getMeleeHit(npc, damage));
				applyFamiliarXP(familiar, damage, Skills.STRENGTH);
			}

			familiar.getOwner().addFamiliarDelay(FAMILIAR_DELAY);
		}
	}

	private void performRegularAttack(Familiar familiar, NPC npc, Entity target, boolean distant) {
		int attackType = distant ? Utils.getRandom(2) : 2;

		int damage = 0;

		switch (attackType) {
			case 0: // magic
				damage = NpcCombatCalculations.getRandomMaxHit(npc, REGULAR_MAGIC_MAX, NpcAttackStyle.MAGIC, target);
				npc.animate(new Animation(MAGIC_ANIMATION));
				ProjectileManager.sendWithGraphic(Projectile.ELEMENTAL_SPELL, RANGE_PROJECTILE, npc, target, new Graphics(RANGE_HIT_GFX, 100));
				delayHit(npc, target, Utils.getDistance(npc, target) > 3 ? 2 : 1, getMagicHit(npc, damage));
				applyFamiliarXP(familiar, damage, Skills.MAGIC);
				break;

			case 1: // range
				damage = NpcCombatCalculations.getRandomMaxHit(npc, REGULAR_RANGE_MAX, NpcAttackStyle.RANGED, target);
				npc.animate(new Animation(SPECIAL_ANIMATION));
				ProjectileManager.sendWithGraphic(Projectile.ELEMENTAL_SPELL, RANGE_PROJECTILE, npc, target, new Graphics(MAGE_HIT_GFX, 0));
				delayHit(npc, target, Utils.getDistance(npc, target) > 3 ? 2 : 1, getRangeHit(npc, damage));
				applyFamiliarXP(familiar, damage, Skills.RANGE);
				break;
			case 2:
				damage = NpcCombatCalculations.getRandomMaxHit(npc, REGULAR_MELEE_MAX, NpcAttackStyle.CRUSH, target);
				npc.animate(new Animation(MELEE_ANIMATION));
				delayHit(npc, target, 0, getMeleeHit(npc, damage));
				applyFamiliarXP(familiar, damage, Skills.STRENGTH);
				break;

		}
	}

	private void applyFamiliarXP(Familiar familiar, int damage, int skill) {
		Player owner = familiar.getOwner();
		if (owner.toggles("ONEXPPERHIT", false)) {
			int xp = owner.toggles("ONEXHITS", false) ? Math.round(damage) / 10 : damage;
			owner.getSkills().addXpNoBonus(skill, xp);
		} else {
			owner.getSkills().addXp(skill, damage / 3);
		}
	}
}
