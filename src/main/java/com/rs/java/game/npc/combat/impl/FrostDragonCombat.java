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
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class FrostDragonCombat extends CombatScript {

	private static final int[] DRAGON_SHIELDS = {11283, 11284, 1540};

	private static final int DRAGON_SLAM_ANIMATION = 80, DRAGON_HEADBUTT_ANIMATION = 91, DRAGONFIRE_BREATH_ANIMATION = 84, DRAGON_DEATH_ANIMATION = 92;
	private static final int DRAGONFIRE_GFX = 1, DRAGONFIRE_TOXIC_PROJECTILE = 393, DRAGONFIRE_NORMAL_PROJECTILE = 394, DRAGONFIRE_ICY_PROJECTILE = 395, DRAGONFIRE_SHOCKING_PROJECTILE = 396;
	private static final int ICE_ARROW_PROJECTILE = 16, WATER_PROJECTILE = 2707;


	@Override
	public Object[] getKeys() {
		return new Object[] { "Frost dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		final Player player = target instanceof Player ? (Player) target : null;

		int attackType = Utils.getRandom(3);

		switch (attackType) {
			case 0: // Melee
				if (npc.withinDistance(target, 3)) {
					int damage = NpcCombatCalculations.getRandomMaxHit(
							npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target
					);
					npc.animate(new Animation(Utils.roll(1, 2) ? DRAGON_SLAM_ANIMATION : DRAGON_HEADBUTT_ANIMATION));
					delayHit(npc, 0, target, getMeleeHit(npc, damage));
					return defs.getAttackDelay();
				}
			case 1: // Dragon breath / frost breath
				if (!(target instanceof Player p)) break;

				int rawDamage = Utils.getRandom(650);
				int mitigated = DragonFire.applyDragonfireMitigation(p, rawDamage);
				npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION)); // dragon breath animation
				ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, DRAGONFIRE_NORMAL_PROJECTILE, npc, target);
				delayHit(npc, Utils.getDistance(npc, target) > 2 ? 2 : 1, target, getRegularHit(npc, mitigated));
				DragonFire.handleDragonfireShield(p);
				break;
			case 2: // Ice arrow range
				int magicDamage = Utils.getRandom(250);
				npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION));
				ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, ICE_ARROW_PROJECTILE, npc, target);
				delayHit(npc, 1, target, getRangeHit(npc, magicDamage));
				break;
			case 3: // Standard ranged
				int rangeDamage = Utils.getRandom(250);
				npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION));
				ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, WATER_PROJECTILE, npc, target);
				delayHit(npc, 1, target, getMagicHit(npc, rangeDamage));
				break;
		}

		return defs.getAttackDelay();
	}
}
