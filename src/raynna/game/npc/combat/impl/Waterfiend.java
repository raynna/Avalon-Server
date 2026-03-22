package raynna.game.npc.combat.impl;

import raynna.game.Entity;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.util.Utils;
import raynna.game.world.projectile.Projectile;
import raynna.game.world.projectile.ProjectileManager;

public class Waterfiend extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 5361 };
	}

	enum WaterfiendAttack { RANGE, MAGIC }

	@Override
	public int attack(final NPC npc, final Entity target) {
		WaterfiendAttack attack = Utils.randomWeighted(WaterfiendAttack.RANGE, 50, WaterfiendAttack.MAGIC, 50);
		switch (attack) {
			case RANGE -> {
				npc.animate(npc.getAttackAnimation());
				Hit rangeHit = npc.rangedHit(target, npc.getMaxHit());
				ProjectileManager.send(Projectile.STANDARD_MAGIC_FAST, 12, 32, npc, target, () -> {
					applyRegisteredHit(npc, target, rangeHit);
				});
			}
			case MAGIC -> {
				npc.animate(npc.getAttackAnimation());
				Hit magicHit = npc.magicHit(target, npc.getMaxHit());
				ProjectileManager.send(Projectile.STANDARD_MAGIC_FAST, 2706, 32, npc, target, () -> {
					applyRegisteredHit(npc, target, magicHit);
				});
			}
		}
		return npc.getAttackSpeed();
	}
}
