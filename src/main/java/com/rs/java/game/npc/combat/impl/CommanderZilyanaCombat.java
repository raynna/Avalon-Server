package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.npc.worldboss.WorldBossNPC;

public class CommanderZilyanaCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]{6247}; // Commander Zilyana
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NpcCombatDefinition defs = npc.getCombatDefinitions();

		maybeShout(npc);

		if (Utils.random(2) == 0) {
			performMagicAttack(npc);
		} else {
			performMeleeAttack(npc, target, defs);
		}

		return npc.getCombatData().attackSpeedTicks;
	}

	// --------------------------
	// Shouts
	// --------------------------
	private void maybeShout(NPC npc) {
		if (Utils.random(4) != 0) return;

		switch (Utils.random(10)) {
			case 0 -> shout(npc, "Death to the enemies of the light!", 3247);
			case 1 -> shout(npc, "Slay the evil ones!", 3242);
			case 2 -> shout(npc, "Saradomin lend me strength!", 3263);
			case 3 -> shout(npc, "By the power of Saradomin!", 3262);
			case 4 -> shout(npc, "May Saradomin be my sword.", 3251);
			case 5 -> shout(npc, "Good will always triumph!", 3260);
			case 6 -> shout(npc, "Forward! Our allies are with us!", 3245);
			case 7 -> shout(npc, "Saradomin is with us!", 3266);
			case 8 -> shout(npc, "In the name of Saradomin!", 3250);
			case 9 -> shout(npc, "Attack! Find the Godsword!", 3258);
		}
	}

	private void shout(NPC npc, String text, int soundId) {
		npc.setNextForceTalk(new ForceTalk(text));
		npc.playSound(soundId, 2);
	}

	// --------------------------
	// Magic attack
	// --------------------------
	private void performMagicAttack(NPC npc) {
		npc.animate(new Animation(6967));

		for (Entity t : npc.getPossibleTargets()) {
			if (!t.withinDistance(npc, npc instanceof WorldBossNPC ? 16 : 3)) {
				continue;
			}
			Hit magicHit = npc.magicHit(npc, 300);
			if (magicHit.getDamage() > 0) {
				delayHit(npc, t, 1, magicHit);
				t.gfx(new Graphics(1194));
			}
		}
	}

	// --------------------------
	// Melee attack
	// --------------------------
	private void performMeleeAttack(NPC npc, Entity target, NpcCombatDefinition defs) {
		npc.animate(new Animation(defs.getAttackAnim()));

		Hit meleeHit = npc.meleeHit(npc, defs.getMaxHit(), NpcAttackStyle.SLASH);
		delayHit(npc, target, 0, meleeHit);
	}
}
