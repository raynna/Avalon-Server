package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class RevenantCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] {
				13465, 13466, 13467, 13468, 13469,
				13470, 13471, 13472, 13473, 13474,
				13475, 13476, 13477, 13478, 13479,
				13480, 13481
		};
	}

	private int getMagicAnimation(NPC npc) {
		return switch (npc.getId()) {
			case 13465 -> 7500;
			case 13466, 13467, 13468, 13469 -> 7499;
			case 13470, 13471 -> 7506;
			case 13472 -> 7503;
			case 13473 -> 7507;
			case 13474 -> 7496;
			case 13475 -> 7497;
			case 13476 -> 7515;
			case 13477 -> 7498;
			case 13478 -> 7505;
			case 13479 -> 7515;
			case 13480 -> 7508;
			default -> npc.getCombatDefinitions().getAttackAnim();
		};
	}

	private int getRangeAnimation(NPC npc) {
		return switch (npc.getId()) {
			case 13465 -> 7501;
			case 13466, 13467, 13468, 13469 -> 7513;
			case 13470, 13471 -> 7519;
			case 13472 -> 7516;
			case 13473 -> 7520;
			case 13474 -> 7521;
			case 13475 -> 7510;
			case 13476 -> 7501;
			case 13477 -> 7512;
			case 13478 -> 7518;
			case 13479 -> 7514;
			case 13480 -> 7522;
			default -> npc.getCombatDefinitions().getAttackAnim();
		};
	}

	enum RevenantAttack { MELEE, RANGE, MAGIC }

	@Override
	public int attack(final NPC npc, final Entity target) {
        npc.getTemporaryAttributtes().putIfAbsent("revenantHeals", 0);
		int timesHealed = (int) npc.getTemporaryAttributtes().get("revenantHeals");
		if (timesHealed < 10 && npc.getHitpoints() < npc.getMaxHitpoints() * 0.50 && Utils.roll(1, 3)) {
			npc.heal(npc.getMaxHitpoints() / 4);
			npc.getTemporaryAttributtes().put("revenantHeals", timesHealed + 1);
			return npc.getAttackSpeed();
		}
		RevenantAttack attack;
		boolean inMeleeDistance = npc.isWithinMeleeRange(target);
		if (inMeleeDistance) {
			attack = Utils.randomWeighted(RevenantAttack.MELEE, 33, RevenantAttack.RANGE, 33, RevenantAttack.MAGIC, 33);
		} else {
			attack = Utils.randomWeighted(RevenantAttack.RANGE, 50, RevenantAttack.MAGIC, 50);
		}
		if (attack != RevenantAttack.MELEE && target instanceof Player player) {
			player.playSound(202, 1);
		}
		switch (attack) {
			case MELEE -> performMeleeAttack(npc, target);
			case RANGE -> performRangeAttack(npc, target);
			case MAGIC -> performMagicAttack(npc, target);
		}
		return npc.getAttackSpeed();
	}


	private void performMagicAttack(NPC npc, Entity target) {
		npc.animate(getMagicAnimation(npc));
		Hit magicHit = npc.magicHit(target, npc.getMaxHit());
		if (target instanceof Player player) {
			Item gloves = player.getEquipment().getItem(Equipment.SLOT_HANDS);
			if (gloves != null && gloves.isAnyOf("item.forinthry_brace_5", "item.forinthry_brace_4", "item.forinthry_brace_3", "item.forinthry_brace_2", "item.forinthry_brace_1")) {
				magicHit.setDamage((int) (magicHit.getDamage() * 0.1));
			}
		}
		ProjectileManager.send(Projectile.STANDARD_MAGIC_FAST, 1276, 32, npc, target, () -> {
			applyRegisteredHit(npc, target, magicHit);
			if (magicHit.getDamage() > 0) {
				target.gfx(1277, 100);
				if (Utils.roll(1, 5)) {
					target.gfx(363);
					target.addFreezeDelay(9, false);
				}
			}
		});
	}

	private void performRangeAttack(NPC npc, Entity target) {
		npc.animate(getRangeAnimation(npc));
		Hit rangeHit = npc.rangedHit(target, npc.getMaxHit());
		if (target instanceof Player player) {
			Item gloves = player.getEquipment().getItem(Equipment.SLOT_HANDS);
			if (gloves != null && gloves.isAnyOf("item.forinthry_brace_5", "item.forinthry_brace_4", "item.forinthry_brace_3", "item.forinthry_brace_2", "item.forinthry_brace_1")) {
				rangeHit.setDamage((int) (rangeHit.getDamage() * 0.1));
			}
		}
		ProjectileManager.send(Projectile.STANDARD_MAGIC_FAST, 1278, 32, npc, target, () -> {
			applyRegisteredHit(npc, target, rangeHit);
		});
	}

	private void performMeleeAttack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
		if (target instanceof Player player) {
			Item gloves = player.getEquipment().getItem(Equipment.SLOT_HANDS);
			if (gloves != null && gloves.isAnyOf("item.forinthry_brace_5", "item.forinthry_brace_4", "item.forinthry_brace_3", "item.forinthry_brace_2", "item.forinthry_brace_1")) {
				meleeHit.setDamage((int) (meleeHit.getDamage() * 0.1));
			}
		}
		delayHit(npc, target, 0, meleeHit);
	}
}
