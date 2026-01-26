package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.world.projectile.ProjectileManager;
import com.rs.kotlin.game.world.projectile.Projectile;

public class KarilCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2028 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackAnim()));
		Hit rangeHit = npc.rangedHit(target, defs.getMaxHit());
		ProjectileManager.send(Projectile.BOLT, defs.getAttackProjectile(), npc, target, () -> {
			applyRegisteredHit(npc, target, rangeHit);
			if (rangeHit.getDamage() != 0 && target instanceof Player && Utils.random(3) == 0) {
				target.gfx(new Graphics(401, 0, 100));
				Player targetPlayer = (Player) target;
				int drain = (int) (targetPlayer.getSkills().getLevelForXp(Skills.AGILITY) * 0.2);
				int currentLevel = targetPlayer.getSkills().getLevel(Skills.AGILITY);
				targetPlayer.getSkills().set(Skills.AGILITY, currentLevel < drain ? 0 : currentLevel - drain);
			}
		});
		return npc.getAttackSpeed();
	}
}
