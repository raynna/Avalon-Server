package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

public class AquaniteCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Aquanite" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackEmote()));
		int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target);
		if (target instanceof Player) {
			Player p2 = (Player) target;
		if (Utils.random(10) == 0) {
			if (p2.getPrayer().usingPrayer(0, 17) || p2.getPrayer().usingPrayer(1, 7)) {
				p2.getPrayer().closeProtectPrayers();
				p2.getPackets().sendGameMessage("The creature's attack turns off your " + (p2.getPrayer().usingPrayer(1, 7) ? "Deflect from Magic" : "Protect from Magic") +" prayer!");
				}
			}
		}
		World.sendProjectile(npc, target, defs.getAttackProjectile(), 41, 16, 41, 35, 16, 0);
		npc.gfx(new Graphics(defs.getAttackGfx()));
		delayHit(npc, 2, target, getMagicHit(npc, damage));
		return defs.getAttackDelay();
	}

}
