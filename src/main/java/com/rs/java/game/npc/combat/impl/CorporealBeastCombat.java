package com.rs.java.game.npc.combat.impl;

import java.util.List;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.corporeal.CorporealBeast;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.worldboss.WorldBossNPC;
import com.rs.kotlin.game.npc.worldboss.WorldCorporealBeast;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class CorporealBeastCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 8133 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		spawnDarkCoreIfNeeded(npc);

		if (handleStompAttack(npc)) {
			return npc.getAttackSpeed();
		}
		int attackStyle = decideAttackStyle(npc, target);

		switch (attackStyle) {
			case 0:
			case 1:
				handleMeleeAttack(npc, target, attackStyle);
				break;
			case 2:
				handleMagicSpikyBall(npc, target);
				break;
			case 3:
				handleMagicDrainBall(npc, target);
				break;
			case 4:
				handleAoEExplosion(npc, target);
				break;
		}

		return npc.getAttackSpeed();
	}

	private void spawnDarkCoreIfNeeded(NPC npc) {
		if (npc instanceof CorporealBeast beast) {
			beast.spawnDarkEnergyCore();
		}
		if (npc instanceof WorldBossNPC boss) {
			if (boss instanceof WorldCorporealBeast corporealBeast) {
				corporealBeast.spawnDarkEnergyCore();
			}
		}
	}

	private boolean handleStompAttack(NPC npc) {
		List<Entity> possibleTargets = npc.getPossibleTargets();
		int size = npc.getSize();
		boolean stomped = false;

		for (Entity t : possibleTargets) {
			if (isWithinNpcSize(npc, t, size)) {
				stomped = true;
				Hit aoeHit = npc.meleeHit(t, npc.getMaxHit());
				delayHit(npc, t, 0, aoeHit);
			}
		}

		if (stomped) {
			npc.animate(10496);
			npc.gfx(1834);
		}

		return stomped;
	}

	private boolean isWithinNpcSize(NPC npc, Entity target, int size) {
		int dx = target.getX() - npc.getX();
		int dy = target.getY() - npc.getY();
		return dx < size && dx > -1 && dy < size && dy > -1;
	}

	private int decideAttackStyle(NPC npc, Entity target) {
		int style = Utils.getRandom(5);
		if (target instanceof Player player)
			player.message("Style: " + style);

		if (style <= 1) {
			if (!isInMeleeRange(npc, target)) {
				style = 2 + Utils.getRandom(2);
			}
		}
		return style;
	}

	private boolean isInMeleeRange(NPC npc, Entity target) {
		int dx = target.getX() - npc.getX();
		int dy = target.getY() - npc.getY();
		int size = npc.getSize();
		return !(dx > size || dx < -1 || dy > size || dy < -1);
	}

	private void handleMeleeAttack(NPC npc, Entity target, int style) {
		npc.animate(style == 0 ? npc.getAttackAnimation() : 10058);
		Hit meleeHit = npc.meleeHit(target, npc.getMaxHit());
		delayHit(npc, target, 0, meleeHit);
	}

	private void handleMagicSpikyBall(NPC npc, Entity target) {
		npc.animate(10410);
		Hit magicHit = npc.magicHit(target, 650);
		ProjectileManager.send(Projectile.STANDARD_MAGIC_INSTANT, 1825, npc, target, () -> {
			delayHit(npc, target, 0, magicHit);
		});
	}

	private void handleMagicDrainBall(NPC npc, Entity target) {
		npc.animate(10410);
		Hit magicHit = npc.magicHit(target, 550);
		ProjectileManager.send(Projectile.STANDARD_MAGIC_INSTANT, 1823, npc, target, () -> {
			delayHit(npc, target, 0, magicHit);
			if (target instanceof Player p2) {
				drainRandomSkill(p2);
			}
		});
	}

	private void handleAoEExplosion(NPC npc, Entity target) {
		npc.animate(10410);
		final WorldTile impactTile = new WorldTile(target);

		ProjectileManager.send(
				Projectile.STANDARD_MAGIC_INSTANT,
				1824,
				npc,
				target,
				() -> performAoEProjectiles(npc, impactTile)
		);
	}



	private void drainRandomSkill(Player player) {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				int roll = Utils.getRandom(2);
				int skill = (roll == 0 ? Skills.MAGIC : roll == 1 ? Skills.SUMMONING : Skills.PRAYER);

				if (skill == Skills.PRAYER) {
					player.getPrayer().drainPrayer(10 + Utils.getRandom(40));
				} else {
					int lvl = player.getSkills().getLevel(skill);
					player.getSkills().set(skill, Math.max(0, lvl - (1 + Utils.getRandom(4))));
				}

				player.message("Your " + Skills.SKILL_NAME[skill] + " has been slightly drained!");
			}
		}, 1);
	}

	private void performAoEProjectiles(NPC npc, WorldTile baseTile) {
		List<Entity> targets = npc.getPossibleTargets();

		for (int i = 0; i < 3; i++) {
			final WorldTile aoeTile = new WorldTile(baseTile, 3);

			if (!World.canMoveNPC(aoeTile.getPlane(), aoeTile.getX(), aoeTile.getY(), 1))
				continue;

			ProjectileManager.sendToTile(
					Projectile.STANDARD_MAGIC_INSTANT,
					1824,
					baseTile,
					aoeTile,
					() -> {
						for (Entity t : npc.getPossibleTargets()) {
							if (Utils.getDistance(t, aoeTile) <= 1 &&
									t.clipedProjectile(aoeTile, false)) {
								delayHit(npc, t, 0, npc.magicHit(t, 350));
							}
						}
						World.sendGraphics(npc, new Graphics(1806), aoeTile);
					}
			);
			}
		}
}
