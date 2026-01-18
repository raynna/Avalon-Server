package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class AhrimCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2025 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		npc.gfx(2728);
		Hit mageHit = npc.magicHit(target, npc.getMaxHit());
		ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, 2735, npc, target);
		if (mageHit.getDamage() != 0 && target instanceof Player player && Utils.roll(1, 3)) {
			target.gfx(400, 100);
            int currentLevel = player.getSkills().getLevel(Skills.STRENGTH);
			player.getSkills().set(Skills.STRENGTH, currentLevel < 5 ? 0 : currentLevel - 5);
		}
		npc.playSound(npc.getCombatDefinitions().getAttackSound(), 1);
		delayHit(npc, target, npc.getHitDelay(npc, target), mageHit);
		return npc.getAttackSpeed();
	}
}
