package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
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
			default -> npc.getCombatDefinitions().getAttackEmote();
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
			default -> npc.getCombatDefinitions().getAttackEmote();
		};
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();

        npc.getTemporaryAttributtes().putIfAbsent("revenantHeals", 0);
		int timesHealed = (int) npc.getTemporaryAttributtes().get("revenantHeals");
		if (timesHealed < 15 && npc.getHitpoints() < npc.getMaxHitpoints() * 0.30 && Utils.roll(1, 3)) {
			npc.heal(npc.getMaxHitpoints() / 4);
			npc.getTemporaryAttributtes().put("revenantHeals", timesHealed + 1);
		}

		int attackStyle = Utils.random(3);
		if (attackStyle == 2) {
			int dx = target.getX() - npc.getX();
			int dy = target.getY() - npc.getY();
			int size = npc.getSize();
			if (dx > size || dx < -1 || dy > size || dy < -1) {
				attackStyle = Utils.random(2);
			}
		}

		if (attackStyle != 2 && target instanceof Player player) {
			player.getPackets().sendSound(202, 0, 1);
		}

		switch (attackStyle) {
			case 0 -> performMagicAttack(npc, target, defs);
			case 1 -> performRangeAttack(npc, target, defs);
			case 2 -> performMeleeAttack(npc, target, defs);
		}

		return defs.getAttackDelay();
	}


	private void performMagicAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		int damage = NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target);
		delayHit(npc, 2, target, getMagicHit(npc, damage));
		ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, 1276, npc, target);

		if (damage > 0) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.gfx(new Graphics(1277, 0, 100));
					if (Utils.roll(1, 5)) {
						target.gfx(new Graphics(363));
						target.addFreezeDelay(9, false);
					}
				}
			}, 2);
		}

		npc.animate(new Animation(getMagicAnimation(npc)));
	}

	private void performRangeAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		int damage = NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.RANGE, target);
		delayHit(npc, 2, target, getRangeHit(npc, damage));
		ProjectileManager.sendSimple(Projectile.ARROW, 1278, npc, target);
		npc.animate(new Animation(getRangeAnimation(npc)));
	}

	private void performMeleeAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		int damage = NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		npc.animate(new Animation(defs.getAttackEmote()));
	}
}
