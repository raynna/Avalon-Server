package raynna.game.npc.combat.impl.dung;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;

public class AnimatedBookCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 10744 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		boolean meleeAttack = Utils.random(2) == 0;
		if (meleeAttack) { // melee
			if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
				magicAttack(npc, target);
				return npc.getAttackSpeed();
			} else {
				meleeAttack(npc, target);
				return npc.getAttackSpeed();
			}
		} else {
			magicAttack(npc, target);
			return npc.getAttackSpeed();
		}
	}

	private void meleeAttack(NPC npc, Entity target) {
		npc.animate(new Animation(13479));
		delayHit(npc, target, 0, npc.meleeHit(npc, 100));
	}

	private void magicAttack(NPC npc, final Entity target) {
		npc.animate(new Animation(13480));
		npc.gfx(new Graphics(2728));
		delayHit(npc, target, 1, npc.magicHit(npc, 100));
		World.sendProjectileToTile(npc, target, 2731);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				target.gfx(new Graphics(2738, 0, 80));
			}
		}, 2);
	}
}
