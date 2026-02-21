package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class StrykewyrmCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]{9463, 9465, 9467};
	}

	private enum StrykewyrmAttack {
		MELEE,
		MAGIC,
		BURY
	}

	@Override
	public int attack(final NPC npc, final Entity target) {

		NpcCombatDefinition defs = npc.getCombatDefinitions();

		StrykewyrmAttack attack = selectAttack(npc, target);

		switch (attack) {

			case MELEE:
				performMelee(npc, target, defs);
				break;

			case MAGIC:
				performMagic(npc, target, defs);
				break;

			case BURY:
				performBury(npc, target);
				break;
		}

		return npc.getAttackSpeed();
	}

	private StrykewyrmAttack selectAttack(NPC npc, Entity target) {

		boolean inMelee = npc.isWithinMeleeRange(target);
		if (npc.temporaryAttribute().containsKey("strykewyrm_bury_cd")) {
			return inMelee ? StrykewyrmAttack.MELEE : StrykewyrmAttack.MAGIC;
		}
		if (inMelee) {
			return Utils.randomWeighted(
					StrykewyrmAttack.MELEE, 45,
					StrykewyrmAttack.MAGIC, 45,
					StrykewyrmAttack.BURY, 10
			);
		}

		return Utils.randomWeighted(
				StrykewyrmAttack.MAGIC, 90,
				StrykewyrmAttack.BURY, 10
		);
	}

	private void performMelee(NPC npc, Entity target, NpcCombatDefinition defs) {

		npc.animate(new Animation(defs.getAttackAnim()));

		Hit hit = npc.meleeHit(target, defs.getMaxHit());
		delayHit(npc, target, 0, hit);
	}

	private void performMagic(NPC npc, Entity target, NpcCombatDefinition defs) {

		npc.animate(new Animation(12794));

		Hit hit = npc.magicHit(target, defs.getMaxHit());

		ProjectileManager.send(
				Projectile.STANDARD_MAGIC_FAST,
				defs.getAttackProjectile(),
				npc,
				target,
				() -> {
					applyRegisteredHit(npc, target, hit);
					if (npc.getId() == 9463) {
						if (Utils.roll(1, 10) && !target.isFrozen()) {
							target.setFreezeDelay(5);
							target.gfx(new Graphics(369));
							if (target instanceof Player player) {
								player.stopAll();
							}
							return;
						}
						target.gfx(new Graphics(2315));
					}
				}
		);
	}


	private void performBury(final NPC npc, final Entity target) {
		npc.temporaryAttribute().put("strykewyrm_bury_cd", true);
		final WorldTile tile = new WorldTile(target);
		tile.moveLocation(-1, -1, 0);

		npc.animate(new Animation(12796));
		npc.setCantInteract(true);

		npc.resetCombat();
		npc.setAttackedByDelay(16);

		final int combatId = npc.getId();
		final int digId = combatId - 1;

		WorldTasksManager.schedule(new WorldTask() {

			int stage = 0;

			@Override
			public void run() {
				if (stage > 0 && stage != 4 && stage != 5) {
					if (npc.isWithinMeleeRange(target)) {
						stage = 4;
					}
				}
				switch (stage) {
					case 0:
						npc.transformIntoNPC(digId);
						npc.setForceWalk(tile);
						break;

					case 4:
						npc.transformIntoNPC(combatId);
						npc.animate(new Animation(12795));

						if (npc.isWithinMeleeRange(target)) {
							delayHit(npc, target, 0,
									new Hit(npc, 300, HitLook.REGULAR_DAMAGE));
						}
						break;

					case 5:
						npc.getCombat().setCombatDelay(npc.getAttackSpeed());
						npc.setTarget(target);
						npc.setCantInteract(false);
						npc.getCombat().addAttackDelay(4);
						WorldTasksManager.schedule(12, () -> npc.temporaryAttribute().remove("strykewyrm_bury_cd"));
						stop();
						return;
				}

				stage++;
			}

		}, 1, 1);
	}
}