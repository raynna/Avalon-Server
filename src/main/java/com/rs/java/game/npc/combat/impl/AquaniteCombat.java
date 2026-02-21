package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.prayer.AncientPrayer;
import com.rs.java.game.player.prayer.NormalPrayer;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class AquaniteCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Aquanite" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		npc.gfx(npc.getAttackGfx());

		Hit mageHit = npc.magicHit(target, npc.getMaxHit());
		ProjectileManager.send(Projectile.STANDARD_MAGIC_FAST, npc.getProjectileId(), npc, target, () -> {
			applyRegisteredHit(npc, target, mageHit);
			if (target instanceof Player p2) {
				if (Utils.roll(1, 10)) {
					if (p2.getPrayer().isActive(NormalPrayer.PROTECT_FROM_MAGIC) || p2.getPrayer().isActive(AncientPrayer.DEFLECT_MAGIC)) {
						p2.getPrayer().closeAllPrayers();
						p2.message("The creature's attack turns off your " + (p2.getPrayer().isActive(AncientPrayer.DEFLECT_MAGIC) ? "Deflect from Magic" : "Protect from Magic") +" prayer!");
					}
				}
			}
		});
		return npc.getAttackSpeed();
	}

}
