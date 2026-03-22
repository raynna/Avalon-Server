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

public class LeatherDragonCombat extends CombatScript {

	private static final int DRAGONFIRE_GFX = 1;
	private static final int MELEE_ANIMATION = Rscm.INSTANCE.animation("animation.leather_dragon_attack");
	private static final int FIREBREATH_ANIMATION = Rscm.INSTANCE.animation("animation.leather_dragon_firebreath");

	private final static int FIREBREATH_SOUND = Rscm.INSTANCE.sound("sound.dragonfire_breath");

	enum DragonAttack { MELEE, FIREBREATH }

	@Override
	public Object[] getKeys() {
		return new Object[] {
				"Green dragon", "Blue dragon", "Red dragon", "Black dragon", 742, 14548
		};
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		DragonAttack attack = Utils.randomWeighted(
				DragonAttack.MELEE, 75,
				DragonAttack.FIREBREATH, 25
		);
		switch (attack) {
			case MELEE -> performMeleeAttack(npc, target);
			case FIREBREATH -> performDragonfireAttack(npc, target);
		}
		return npc.getAttackSpeed();
	}


	private void performMeleeAttack(NPC npc, Entity target) {
		npc.animate(new Animation(MELEE_ANIMATION));
		if (npc.getCombatDefinitions().getAttackSound() != -1)
			npc.playSound(npc.getCombatDefinitions().getAttackSound(), 1);
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit(), NpcAttackStyle.CRUSH);
		delayHit(npc, target, 0, meleeHit);
	}

	private void performDragonfireAttack(NPC npc, Entity target) {
		if (!(target instanceof Player player)) {
			return;
		}

		npc.animate(new Animation(FIREBREATH_ANIMATION));
		npc.gfx(DRAGONFIRE_GFX, 100);
		npc.playSound(FIREBREATH_SOUND, 1);
		boolean accuracyRoll = NpcCombatCalculations.getAccuracyRoll(npc, NpcAttackStyle.MAGIC, target);
		int mitigatedDamage = DragonFire.applyDragonfireMitigation(player, accuracyRoll, DragonFire.DragonType.CHROMATIC);
		Hit dragonfire = npc.regularHit(target, mitigatedDamage);
		delayHit(npc, target, 1, dragonfire);
		DragonFire.rechargeDragonfireShield(player);
	}
}
