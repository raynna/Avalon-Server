package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.DragonFire;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.Rscm;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

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
		DragonFire.handleDragonfireShield(player);
	}
}
