package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.DragonFire;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.player.Player;
import raynna.util.Utils;
import raynna.data.rscm.Rscm;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;
import raynna.game.world.projectile.Projectile;
import raynna.game.world.projectile.ProjectileManager;

public class MetalDragonCombat extends CombatScript {

	private static final int DRAGON_HEADBUTT_ANIMATION = 13158, DRAGONFIRE_ANIMATION = 13160, DRAGONFIRE_BREATH_ANIMATION = 13164;
	private static final int DRAGONFIRE_GFX = 1, DRAGONFIRE_NORMAL_PROJECTILE = 393;

	private final static int FIREBREATH_SOUND = Rscm.INSTANCE.sound("sound.dragonfire_breath");

	enum MetallicDragonAttack { MELEE, DRAGON_BREATH, DRAGONFIRE }

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bronze dragon", "Iron dragon", "Steel dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		MetallicDragonAttack attack;

		boolean inMelee = npc.isWithinMeleeRange(target);
		if (inMelee) {
			attack = Utils.randomWeighted(MetallicDragonAttack.MELEE, 75, MetallicDragonAttack.DRAGON_BREATH, 25);
		} else {
			attack = MetallicDragonAttack.DRAGONFIRE;
		}
		switch (attack) {
			case DRAGONFIRE -> performDragonfireAttack(npc, target);
			case DRAGON_BREATH -> performDragonfireBreath(npc, target);
			case MELEE -> performMeleeAttack(npc, target);
		}
		return npc.getAttackSpeed();
	}

	private void performMeleeAttack(NPC npc, Entity target) {
		NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(DRAGON_HEADBUTT_ANIMATION);
		int attackSound = npc.getCombatDefinitions().getAttackSound();
		if (attackSound != -1)
			npc.playSound(attackSound, 1);
		Hit meleeAttack = npc.meleeHit(target, defs.getMaxHit());
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
		DragonFire.rechargeDragonfireShield(player);
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
			DragonFire.rechargeDragonfireShield(player);
		});
	}
}
