package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.DragonFire;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.rscm.Rscm;

public class WyvernCombat extends CombatScript {

	private static final int MELEE_ANIMATION = Rscm.INSTANCE.animation("animation.wyvern_melee_attack");
	private static final int RANGE_ANIMATION = Rscm.INSTANCE.animation("animation.wyvern_range_attack");
	private static final int ICY_BREATH_ANIMATION = Rscm.INSTANCE.animation("animation.wyvern_breath");

	private static final int RANGE_GFX = Rscm.INSTANCE.graphic("graphic.skeletal_wyvern_range");
	private static final int ICY_BREATH_GFX = Rscm.INSTANCE.graphic("graphic.skeletal_wyvern_icy_breath");
	private static final int ICY_BREATH_HIT = Rscm.INSTANCE.graphic("graphic.skeletal_wyvern_icy_breath_hit");

	enum WyvernAttack { MELEE, RANGE, ICY_BREATH}

	@Override
	public Object[] getKeys() {
		return new Object[] {
				3068
		};
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		WyvernAttack attack;
		boolean inMelee = npc.isWithinMeleeRange(target);
		if (inMelee) {
			attack = Utils.randomOf(WyvernAttack.MELEE, WyvernAttack.RANGE, WyvernAttack.ICY_BREATH);
		} else {
			attack = Utils.randomOf(WyvernAttack.RANGE, WyvernAttack.ICY_BREATH);
		}
		switch (attack) {
			case MELEE -> performMeleeAttack(npc, target);
			case RANGE -> performRangeAttack(npc, target);
			case ICY_BREATH -> performIcyBreath(npc, target);
		}
		return npc.getAttackSpeed();
	}


	private void performMeleeAttack(NPC npc, Entity target) {
		npc.animate(MELEE_ANIMATION);
		if (npc.getCombatDefinitions().getAttackSound() != -1)
			npc.playSound(npc.getCombatDefinitions().getAttackSound(), 1);
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit(), NpcAttackStyle.CRUSH);
		delayHit(npc, target, 0, meleeHit);
	}

	private void performRangeAttack(NPC npc, Entity target) {
		npc.animate(RANGE_ANIMATION);
		npc.gfx(RANGE_GFX);
		if (npc.getCombatDefinitions().getAttackSound() != -1)
			npc.playSound(npc.getCombatDefinitions().getAttackSound(), 1);
		Hit rangeHit = npc.rangedHit(target, npc.getMaxHit());
		delayHit(npc, target, 1, rangeHit);
	}

	private void performIcyBreath(NPC npc, Entity target) {
		if (!(target instanceof Player player)) {
			return;
		}
		npc.animate(ICY_BREATH_ANIMATION);
		npc.gfx(ICY_BREATH_GFX, 100);
		boolean accuracyRoll = NpcCombatCalculations.getAccuracyRoll(npc, NpcAttackStyle.MAGIC, target);
		int mitigatedDamage = DragonFire.applyDragonfireMitigation(player, accuracyRoll, DragonFire.DragonType.WYVERN);
		Hit icyBreath = npc.regularHit(target, mitigatedDamage);
		delayHit(npc, target, 1, hit -> {
			if (icyBreath.getDamage() > 0) {
				target.gfx(ICY_BREATH_HIT, 100);
				DragonFire.handleDragonfireShield(player);
			}
		}, icyBreath);
	}
}
