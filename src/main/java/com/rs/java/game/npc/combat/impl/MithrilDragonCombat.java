package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.DragonFire;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.Rscm;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class MithrilDragonCombat extends CombatScript {

	private static final int DRAGON_HEADBUTT_ANIMATION = 13158, DRAGONFIRE_ANIMATION = 13160, DRAGONFIRE_BREATH_ANIMATION = 13164;
	private static final int DRAGONFIRE_GFX = 1, DRAGONFIRE_NORMAL_PROJECTILE = 393;
	private static final int MAGIC_PROJECTILE = 2706;
	private static final int RANGE_PROJECTILE = 12;
	private final static int FIREBREATH_SOUND = Rscm.INSTANCE.sound("sound.dragonfire_breath");

	@Override
	public Object[] getKeys() {
		return new Object[] { "Mithril dragon" };
	}

	enum MithrilDragonAttack { MELEE, MAGIC, RANGE, DRAGONFIRE, DRAGON_BREATH }

	@Override
	public int attack(NPC npc, Entity target) {
		MithrilDragonAttack attack;
		boolean inMelee = npc.isWithinMeleeRange(target);
		if (inMelee) {
			attack = Utils.randomOf(MithrilDragonAttack.MELEE, MithrilDragonAttack.DRAGON_BREATH, MithrilDragonAttack.MAGIC, MithrilDragonAttack.RANGE);
		} else {
			attack = Utils.randomOf(MithrilDragonAttack.MAGIC, MithrilDragonAttack.RANGE, MithrilDragonAttack.DRAGONFIRE);
		}
		switch (attack) {
			case MELEE -> performMeleeAttack(npc, target);
			case DRAGON_BREATH -> performDragonfireBreath(npc, target);
			case DRAGONFIRE -> performDragonfireAttack(npc, target);
			case RANGE -> performRangeAttack(npc, target);
			case MAGIC -> performMagicAttack(npc, target);
		}
		return npc.getAttackSpeed();
	}

	private void performMeleeAttack(NPC npc, Entity target) {
		npc.animate(DRAGON_HEADBUTT_ANIMATION);
		int attackSound = npc.getCombatDefinitions().getAttackSound();
		if (attackSound != -1)
			npc.playSound(attackSound, 1);
		Hit meleeAttack = npc.meleeHit(target, 280);
		delayHit(npc, target, 0, meleeAttack);
	}

	private void performDragonfireBreath(NPC npc, Entity target) {
		if (!(target instanceof Player player)) {
			return;
		}

		npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION));
		npc.gfx(DRAGONFIRE_GFX, 100);
		npc.playSound(FIREBREATH_SOUND, 1);

		boolean accuracyRoll = NpcCombatCalculations.getAccuracyRoll(npc, NpcAttackStyle.MAGIC, target);
		int mitigatedDamage = DragonFire.applyDragonfireMitigation(player, accuracyRoll, DragonFire.DragonType.METALLIC);

		Hit dragonfire = npc.regularHit(target, mitigatedDamage);
		delayHit(npc, player, 1, dragonfire);
		DragonFire.handleDragonfireShield(player);
	}

	private void performDragonfireAttack(NPC npc, Entity target) {
		if (!(target instanceof Player player)) return;
		npc.animate(new Animation(DRAGONFIRE_ANIMATION));
		npc.playSound(FIREBREATH_SOUND, 1);
		boolean accuracyCheck = NpcCombatCalculations.getAccuracyRoll(npc, NpcAttackStyle.MAGIC, target);
		int mitigated = DragonFire.applyDragonfireMitigation(player, accuracyCheck, DragonFire.DragonType.METALLIC);

		Hit dragonfire = npc.regularHit(target, mitigated);
		ProjectileManager.send(Projectile.DRAGONFIRE, DRAGONFIRE_NORMAL_PROJECTILE, npc, target, () -> {
			applyRegisteredHit(npc, target, dragonfire);
			DragonFire.handleDragonfireShield(player);
		});
	}

	private void performRangeAttack(NPC npc, Entity target) {
		npc.animate(DRAGONFIRE_ANIMATION);
		Hit rangeHit = npc.rangedHit(target, 180);
		ProjectileManager.send(Projectile.ELEMENTAL_SPELL, RANGE_PROJECTILE, 32, npc, target, () -> applyRegisteredHit(npc, target, rangeHit));
	}

	private void performMagicAttack(NPC npc, Entity target) {
		npc.animate(DRAGONFIRE_ANIMATION);
		Hit magicHit = npc.magicHit(target, 180);
		ProjectileManager.send(Projectile.ELEMENTAL_SPELL, MAGIC_PROJECTILE, 32, npc, target, () -> applyRegisteredHit(npc, target, magicHit));
	}

}
