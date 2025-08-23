package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.others.TormentedDemon;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class TormentedDemonCombat extends CombatScript {

	//GFX
	private static final int HIT_GFX = 2883;
	private static final int MAGIC_PROJECTILE_ID = 1884;
	private static final int SHIELD_GFX = 1885;
	private static final Graphics MELEE_GFX = new Graphics(1886, 2, 0);
	private static final int RANGE_PROJECTILE_ID = 1887;


	// Melee
	private static final int MELEE_MAX_HIT = 189;
	private static final Animation MELEE_ANIMATION = new Animation(10922);

	// Magic
	private static final int MAGIC_MAX_HIT = 269;
	private static final Animation MAGIC_ANIMATION = new Animation(10918);

	// Ranged
	private static final int RANGED_MAX_HIT = 269;
	private static final Animation RANGED_ANIMATION = new Animation(10919);

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tormented demon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		TormentedDemon torm = (TormentedDemon) npc;

		int attackStyle = torm.getCurrentCombatType();

		// Switch combat style if melee can't reach
		if (attackStyle == 0 && !torm.withinDistance(target, 1)) {
			int random = Utils.random(1, 2);
			while (random == torm.getPreviousCombatType())
				random = Utils.random(1, 2);
			attackStyle = random;
			torm.setCurrentCombatType(attackStyle);
		}

		switch (attackStyle) {
			case 0 -> attackMelee(npc, target);
			case 1 -> attackMagic(npc, target);
			case 2 -> attackRanged(npc, target);
		}

		return defs.getAttackDelay();
	}

	private void attackMelee(NPC npc, Entity target) {
		int damage = NpcCombatCalculations.getRandomMaxHit(npc, MELEE_MAX_HIT, NpcAttackStyle.SLASH, target);
		npc.animate(MELEE_ANIMATION);
		npc.gfx(MELEE_GFX);
		delayHit(npc, target, 0, getMeleeHit(npc, damage));
	}

	private void attackMagic(NPC npc, Entity target) {
		int damage = NpcCombatCalculations.getRandomMaxHit(npc, MAGIC_MAX_HIT, NpcAttackStyle.MAGIC, target);
		npc.animate(MAGIC_ANIMATION);
		ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, MAGIC_PROJECTILE_ID, npc, target);
		delayHit(npc, target, 2, getMagicHit(npc, damage));
	}

	private void attackRanged(NPC npc, Entity target) {
		int damage = NpcCombatCalculations.getRandomMaxHit(npc, RANGED_MAX_HIT, NpcAttackStyle.RANGED, target);
		npc.animate(RANGED_ANIMATION);
		ProjectileManager.sendSimple(Projectile.ARROW, RANGE_PROJECTILE_ID, npc, target);
		delayHit(npc, target, 2, getRangeHit(npc, damage));
	}
}
