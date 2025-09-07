package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class DeathslingerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 11208, 11210, 11212, 11214, 11216, 11218, 11220, 11222, 11224, 11226 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int tier = (npc.getId() - 11208) / 2;
		NpcCombatDefinition definitions = npc.getCombatDefinitions();
		int damage = definitions.getMaxHit();
		if (usingSpecial) {
			npc.gfx(new Graphics(2447));
			damage = (int) (npc.getMaxHit() * (1.05 * tier));
			if (Utils.random(11 - tier) == 0)
				target.getPoison().makePoisoned(100);
		}
		npc.animate(new Animation(13615));
		World.sendProjectileToTile(npc, target, 2448);
		delayHit(npc, target, 2, npc.rangedHit(npc, damage));
		return npc.getAttackSpeed();
	}
}
