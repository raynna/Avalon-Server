package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class StormBringerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 11126, 11128, 11130, 11132, 11134, 11136, 11138, 11140, 11142, 11144, 11146 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int tier = (npc.getId() - 11126) / 2;

		int damage = 0;
		if (usingSpecial) {
			damage = getRandomMaxHit(npc, (int) (npc.getMaxHit() * (1.05 * tier)), NPCCombatDefinitions.MAGE, target);
			if (Utils.random(11 - tier) == 0)
				target.setFreezeDelay(8); // Five seconds cannot move.
		} else
			damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target);
		npc.gfx(new Graphics(2591));
		npc.animate(new Animation(13620));
		World.sendElementalProjectile(npc, target, 2592);//2593
		delayHit(npc, 2, target, getRangeHit(npc, damage));
		if (damage > 0) {
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					target.gfx(new Graphics(2593));
				}
			}, 2);
		}
		return npc.getAttackSpeed();
	}
}
