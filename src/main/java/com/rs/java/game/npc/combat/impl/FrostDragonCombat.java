package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.DragonFire;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.Rscm;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class FrostDragonCombat extends CombatScript {

	private static final int DRAGONFIRE_GFX = 1;
	private static final int ICE_ARROW_PROJECTILE = 16, WATER_PROJECTILE = 2707;
	private static final int MELEE_ANIMATION = Rscm.INSTANCE.animation("animation.frost_dragon_attack");
	private static final int FIREBREATH_ANIMATION = Rscm.INSTANCE.animation("animation.frost_dragon_firebreath");

	private final static int FIREBREATH_SOUND = Rscm.INSTANCE.sound("sound.dragonfire_breath");

	private static final FrostDragonAttack[] ATTACKS = FrostDragonAttack.values();

	enum FrostDragonAttack { MELEE, DRAGON_BREATH, RANGE, MAGIC }

	@Override
	public Object[] getKeys() {
		return new Object[] { "Frost dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();

		FrostDragonAttack attack;

		boolean inMelee = npc.withinDistance(target, 1, npc.getSize());
		if (inMelee) {
			attack = ATTACKS[Utils.random(ATTACKS.length)];
		} else {
			attack = Utils.randomOf(FrostDragonAttack.RANGE, FrostDragonAttack.MAGIC);
		}
		switch (attack) {
			case MELEE:
				Hit meleeHit = npc.meleeHit(target, defs.getMaxHit());
				npc.animate(MELEE_ANIMATION);
				int attackSound = npc.getCombatDefinitions().getAttackSound();
				if (attackSound != -1)
					npc.playSound(attackSound, 1);
				delayHit(npc, target, 0, meleeHit);
				return npc.getAttackSpeed();
			case DRAGON_BREATH:
				if (!(target instanceof Player p)) return npc.getAttackSpeed();
				npc.animate(FIREBREATH_ANIMATION);
				npc.gfx(DRAGONFIRE_GFX);
				npc.playSound(FIREBREATH_SOUND, 1);
				boolean accuracyRoll = NpcCombatCalculations.getAccuracyRoll(npc, NpcAttackStyle.MAGIC, target);
				int mitigated = DragonFire.applyDragonfireMitigation(p, accuracyRoll, DragonFire.DragonType.CHROMATIC);
				Hit dragonfire = npc.regularHit(target, mitigated);
				delayHit(npc, target, 1, dragonfire);
				break;
			case RANGE:
				npc.animate(FIREBREATH_ANIMATION);
				Hit rangeHit = npc.rangedHit(target, 250);
				ProjectileManager.send(Projectile.ARROW, ICE_ARROW_PROJECTILE, npc, target, 64, () -> {
					applyRegisteredHit(npc, target, rangeHit);
				});
				break;
			case MAGIC:
				npc.animate(FIREBREATH_ANIMATION);
				Hit magicHit = npc.magicHit(target, 250);
				ProjectileManager.send(Projectile.ELEMENTAL_SPELL, WATER_PROJECTILE, npc, target, 64, () -> {
					applyRegisteredHit(npc, target, magicHit);
				});
				break;
		}

		return npc.getAttackSpeed();
	}
}
