package raynna.game.npc.combat.impl;

import raynna.game.*;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.familiar.Familiar;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;
import raynna.game.world.projectile.Projectile;
import raynna.game.world.projectile.ProjectileManager;

public class GeyserTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7340, 7339 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		boolean distant = false;
		int size = npc.getSize();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)
			distant = true;
		if (usingSpecial) {
			npc.animate(new Animation(7883));
			npc.gfx(new Graphics(1373));
			if (distant) {// range hit
				if (Utils.randomBoolean()) {
					Hit rangeHit = npc.rangedHit(target, 300);
					delayHit(npc, target, 1, rangeHit);
				} else {
					Hit mageHit = npc.magicHit(target, 300);
					delayHit(npc, target, 1, mageHit);
				}
			} else {// melee hit
				Hit meleeHit = npc.meleeHit(target, 300);
				delayHit(npc, target, 0, meleeHit);
			}
			World.sendElementalProjectile(npc, target, 1376);
		} else {
			if (distant) {// range
				npc.animate(new Animation(7883));
				npc.gfx(new Graphics(1375));
				Hit rangeHit = npc.rangedHit(target, 244);
				ProjectileManager.send(Projectile.ELEMENTAL_SPELL, 1374, npc, target, () -> {
					applyRegisteredHit(npc, target, rangeHit);
				});
			} else {// melee
				npc.animate(new Animation(7879));
				Hit meleeHit = npc.meleeHit(target, 244);
				delayHit(npc, target, 0, meleeHit);
			}
		}
		return npc.getAttackSpeed();
	}
}
