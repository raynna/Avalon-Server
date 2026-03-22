package raynna.game.npc.combat.impl.dung;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.familiar.Familiar;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;

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
			damage = NpcCombatCalculations.getRandomMaxHit(npc, (int) (npc.getMaxHit() * (1.05 * tier)), NpcAttackStyle.MAGIC, target);
			if (Utils.random(11 - tier) == 0)
				target.setFreezeDelay(8); // Five seconds cannot move.
		} else
			damage = NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.RANGED, target);
		npc.gfx(new Graphics(2591));
		npc.animate(new Animation(13620));
		World.sendElementalProjectile(npc, target, 2592);//2593
		delayHit(npc, target, 2, getRangeHit(npc, damage));
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
