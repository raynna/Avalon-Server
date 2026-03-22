package raynna.game.npc.combat.impl;

import raynna.game.*;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.player.Player;
import raynna.game.player.prayer.AncientPrayer;
import raynna.game.player.prayer.NormalPrayer;
import raynna.util.Utils;
import raynna.game.world.projectile.Projectile;
import raynna.game.world.projectile.ProjectileManager;

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
