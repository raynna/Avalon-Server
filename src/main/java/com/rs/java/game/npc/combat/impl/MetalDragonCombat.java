package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.meta.DragonFireShieldMetaData;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.DragonFire;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.prayer.AncientPrayer;
import com.rs.java.game.player.prayer.NormalPrayer;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class MetalDragonCombat extends CombatScript {

	private static final int DRAGON_SLAM_ANIMATION = 80, DRAGON_HEADBUTT_ANIMATION = 91, DRAGONFIRE_BREATH_ANIMATION = 84, DRAGON_DEATH_ANIMATION = 92;
	private static final int DRAGONFIRE_GFX = 1, DRAGONFIRE_TOXIC_PROJECTILE = 393, DRAGONFIRE_NORMAL_PROJECTILE = 394, DRAGONFIRE_ICY_PROJECTILE = 395, DRAGONFIRE_SHOCKING_PROJECTILE = 396;


	@Override
	public Object[] getKeys() {
		return new Object[] { "Bronze dragon", "Iron dragon", "Steel dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if (!isWithinMeleeRange(npc, target)) {
			performDragonfireAttack(npc, target);
		} else {
			if (Utils.getRandom(2) == 0) {
				performMeleeAttack(npc, target);
			} else {
				performDragonfireAttack(npc, target);
			}
		}
		return npc.getCombatDefinitions().getAttackDelay();
	}

	private boolean isWithinMeleeRange(NPC npc, Entity target) {
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		return distanceX <= size && distanceX >= -1 && distanceY <= size && distanceY >= -1;
	}

	private void performMeleeAttack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.animate(new Animation(Utils.roll(1, 2) ? DRAGON_SLAM_ANIMATION : DRAGON_HEADBUTT_ANIMATION));
		int damage = NpcCombatCalculations.getRandomMaxHit(
				npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target
		);

		delayHit(npc, 0, target, getMeleeHit(npc, damage));
	}

	private void performDragonfireAttack(NPC npc, Entity target) {
		if (!(target instanceof Player player)) return;

		int rawDamage = Utils.getRandom(650);

		npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION));
		ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, DRAGONFIRE_NORMAL_PROJECTILE, npc, target);

		int mitigated = DragonFire.applyDragonfireMitigation(player, rawDamage);
		delayHit(npc, Utils.getDistance(player, npc) > 2 ? 2 : 1, player, getRegularHit(npc, mitigated));

		DragonFire.handleDragonfireShield(player);
	}
}
