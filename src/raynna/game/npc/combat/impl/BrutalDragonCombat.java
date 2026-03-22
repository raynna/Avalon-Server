package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.DragonFire;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.player.Player;
import raynna.util.Utils;
import raynna.data.rscm.Rscm;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.world.projectile.Projectile;
import raynna.game.world.projectile.ProjectileManager;

public class BrutalDragonCombat extends CombatScript {

	private static final int DRAGONBREATH_GFX = 1;
	private static final int DRAGONFIRE_GFX = 393;
	private static final int MELEE_ANIMATION = Rscm.INSTANCE.animation("animation.leather_dragon_attack");
	private static final int FIREBREATH_ANIMATION = Rscm.INSTANCE.animation("animation.leather_dragon_firebreath");

	private final static int FIREBREATH_SOUND = Rscm.INSTANCE.sound("sound.dragonfire_breath");

	private final static int MAGIC_PROJECTILE = 2705;
	private final static int MAGIC_IMPACT = 2710;
	private final static int MAGIC_CAST_SOUND = 207;
	private final static int MAGIC_IMPACT_SOUND = 208;

	enum DragonAttack { MELEE, MAGIC, DRAGONFIRE, FIREBREATH }

	@Override
	public Object[] getKeys() {
		return new Object[] {
				"Brutal green dragon"
		};
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		boolean inMelee = npc.isWithinMeleeRange(target);
		DragonAttack attack = Utils.randomWeighted(
				DragonAttack.MELEE, 33,
				DragonAttack.FIREBREATH, 33,
				DragonAttack.MAGIC
		);
		if (!inMelee) {
			attack = Utils.randomOf(DragonAttack.MAGIC, DragonAttack.DRAGONFIRE);
		}
		switch (attack) {
			case MELEE -> performMeleeAttack(npc, target);
			case MAGIC -> performMagicAttack(npc, target);
			case DRAGONFIRE -> performDragonFire(npc, target);
			case FIREBREATH -> performDragonBreath(npc, target);
		}
		return npc.getAttackSpeed();
	}


	private void performMeleeAttack(NPC npc, Entity target) {
		npc.animate(new Animation(MELEE_ANIMATION));
		if (npc.getCombatDefinitions().getAttackSound() != -1)
			npc.playSound(npc.getCombatDefinitions().getAttackSound(), 1);
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit(), NpcAttackStyle.STAB);
		delayHit(npc, target, 0, meleeHit);
	}

	private void performMagicAttack(NPC npc, Entity target) {
		npc.animate(new Animation(FIREBREATH_ANIMATION));
		Hit magicHit = npc.magicHit(target, 180);
		npc.playSound(MAGIC_CAST_SOUND, 1);
		ProjectileManager.send(Projectile.DRAGONFIRE, MAGIC_PROJECTILE, new Graphics(MAGIC_IMPACT, 100), npc, target, ()-> {
			applyRegisteredHit(npc, target, magicHit);
			if (magicHit.getDamage() > 0) {
				npc.playSound(MAGIC_IMPACT_SOUND, 1);
			}
		});
	}

	private void performDragonFire(NPC npc, Entity target) {
		if (!(target instanceof Player player)) {
			return;
		}
		npc.animate(new Animation(FIREBREATH_ANIMATION));
		npc.playSound(FIREBREATH_SOUND, 1);
		boolean accuracyRoll = NpcCombatCalculations.getAccuracyRoll(npc, NpcAttackStyle.MAGIC, target);

		int damage = DragonFire.applyDragonfireMitigation(
				player,
				accuracyRoll,
				DragonFire.DragonType.CHROMATIC
		);

		Hit dragonfire = npc.regularHit(target, damage);
		ProjectileManager.send(Projectile.DRAGONFIRE, DRAGONFIRE_GFX, npc, target, () -> {
					applyRegisteredHit(npc, target, dragonfire);
					DragonFire.rechargeDragonfireShield(player);
				}
		);
	}

	private void performDragonBreath(NPC npc, Entity target) {
		if (!(target instanceof Player player)) {
			return;
		}
		npc.animate(new Animation(FIREBREATH_ANIMATION));
		npc.gfx(DRAGONBREATH_GFX, 100);
		npc.playSound(FIREBREATH_SOUND, 1);
		boolean accuracyRoll = NpcCombatCalculations.getAccuracyRoll(npc, NpcAttackStyle.MAGIC, target);
		int mitigatedDamage = DragonFire.applyDragonfireMitigation(player, accuracyRoll, DragonFire.DragonType.CHROMATIC);
		Hit dragonfire = npc.regularHit(target, mitigatedDamage);
		delayHit(npc, target, 1, dragonfire);
		DragonFire.rechargeDragonfireShield(player);
	}
}
