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
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
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
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		final Player player = target instanceof Player ? (Player) target : null;

		int attackType = Utils.getRandom(3);

		switch (attackType) {
			case 0: // Melee
				if (npc.withinDistance(target, 3)) {
					Hit meleeHit = npc.meleeHit(target, defs.getMaxHit());
					npc.animate(new Animation(Utils.roll(1, 2) ? DRAGON_SLAM_ANIMATION : DRAGON_HEADBUTT_ANIMATION));
					delayHit(npc, target, 0, meleeHit);
					return npc.getAttackSpeed();
				}
			case 1: // Dragon breath / frost breath
				if (!(target instanceof Player p)) break;
				npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION));

				int rawDamage = Utils.getRandom(650);
				int mitigated = DragonFire.applyDragonfireMitigation(p, rawDamage, DragonFire.DragonType.CHROMATIC);
				Hit dragonfire = npc.regularHit(target, mitigated);
				ProjectileManager.send(Projectile.DRAGONFIRE, DRAGONFIRE_NORMAL_PROJECTILE, npc, target, () -> {
					applyRegisteredHit(npc, target, dragonfire);
					DragonFire.handleDragonfireShield(p);
				});
				break;
			case 2: // Ice arrow range
				npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION));
				Hit rangeHit = npc.rangedHit(target, 250);
				ProjectileManager.send(Projectile.ARROW, ICE_ARROW_PROJECTILE, npc, target, 64, () -> {
					applyRegisteredHit(npc, target, rangeHit);
				});
				break;
			case 3:
				npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION));
				Hit magicHit = npc.magicHit(target, 250);
				ProjectileManager.send(Projectile.ELEMENTAL_SPELL, WATER_PROJECTILE, npc, target, 64, () -> {
					applyRegisteredHit(npc, target, magicHit);
				});
				break;
		}

		return npc.getAttackSpeed();
	}
}
