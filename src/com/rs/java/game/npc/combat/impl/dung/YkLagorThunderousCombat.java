package com.rs.java.game.npc.combat.impl.dung;

import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.NewForceMovement;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.dungeonnering.YkLagorMage;
import com.rs.java.game.npc.dungeonnering.YkLagorThunderous;
import com.rs.java.game.player.Player;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class YkLagorThunderousCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
				{ 11872 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final YkLagorThunderous boss = (YkLagorThunderous) npc;
		boss.playMusic();
		if (boss.getNextAttack() < 2) {
			if (boss.getNextAttack() == 0) {
				boss.setNextForceTalk(new ForceTalk("Come closer!"));
				//boss.playSoundEffect(1930);
				WorldTasksManager.schedule(new WorldTask() {

					int cycles;

					@Override
					public void run() {
						cycles++;
						if (cycles == 3) {
							boss.animate(new Animation(14390));
							boss.gfx(new Graphics(2768));
						} else if (cycles == 7) {
							List<Entity> targets = boss.getPossibleTargets();
							boolean recovered = false;
							for (Player player : boss.getManager().getParty().getTeam()) {
								if (player.isDead() || !boss.getManager().isAtBossRoom(player))
									continue;
								if (targets.contains((Entity) player)) {
									sendPullAttack(boss.transform(2, 2, 0), player, true);
									player.getPackets().sendGameMessage("Yk'Lagor sees you and pulls you closer, energising him.");
									boss.heal((int) (boss.getMaxHitpoints() * 0.15));
									if (!recovered) {
										boss.setNextForceTalk(new ForceTalk("There is no escape!"));
										//boss.playSoundEffect(1934);
										recovered = true;
									}
								} else
									player.getPackets().sendGameMessage("Hiding behind the pillar manages to stop Yk'Lagor from pulling you in.");
							}
							stop();
							return;
						}
					}
				}, 0, 0);
			} else if (boss.getNextAttack() == 1) {//earthquake shit
				boss.setNextForceTalk(new ForceTalk("This is..."));
				//boss.playSoundEffect(1929);
				WorldTasksManager.schedule(new WorldTask() {

					int cycles;

					@Override
					public void run() {
						cycles++;

						if (cycles == 2) {
							boss.animate(new Animation(14384));
							boss.gfx(new Graphics(2776));
							for (Player player : boss.getManager().getParty().getTeam()) {
								if (player.isDead() || !boss.getManager().isAtBossRoom(player))
									continue;
								player.getPackets().sendCameraShake(3, 25, 50, 25, 50);
							}
						} else if (cycles == 5) {
							boss.setNextForceTalk(new ForceTalk("TRUE POWER!"));
							//boss.playSoundEffect(1936);
							boss.sendBrokenFloor();
						} else if (cycles == 7) {
							for (Player player : boss.getManager().getParty().getTeam()) {
								if (player.isDead() || !boss.getManager().isAtBossRoom(player))
									continue;
								player.getPackets().sendStopCameraShake();
							}
							for (Entity t : boss.getPossibleTargets())
								t.applyHit(new Hit(boss, Utils.random(t.getMaxHitpoints()) + 1, HitLook.REGULAR_DAMAGE));

							stop();
							return;
						}
					}
				}, 0, 0);
			}
			boss.increaseNextAttack((boss.getNextAttack() == 0 ? 3 : 1) + Utils.random(4, 10) * 2);
			return 10;
		}
		boss.increaseNextAttack(-2);
		boolean useMelee = false;
		boolean useMagic = false;
		if (Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
			useMelee = true;
		for (Entity t : npc.getPossibleTargets()) {
			if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), t.getX(), t.getY(), t.getSize(), 0))
				useMagic = true;

		}
		int style = !useMelee ? 1 : !useMagic ? 0 : Utils.random(2);
		switch (style) {
		case 0:
			npc.animate(new Animation(14392));
			int damage = 0;
			if (target instanceof Player) {
				Player player = (Player) target;
				if (player.getPrayer().getPrayerPoints() > 0 && damage > 0)
					player.getPrayer().drainPrayer((int) (damage * .5));
			}
			delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
			break;
		case 1:
			sendMagicalAttack(boss, false);
			break;
		}
		return 4;
	}

	public static void sendPullAttack(final WorldTile tile, final Player player, final boolean disablePrayers) {
		player.lock(3);
		player.resetWalkSteps();
		player.animate(new Animation(14388));
		player.gfx(new Graphics(2767));
		player.setNextForceMovement(new NewForceMovement(player, 0, tile, 2, Utils.getAngle(tile.getX() - player.getX(), tile.getY() - player.getY())));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.getActionManager().addActionDelay(10);
				player.setNextWorldTile(tile);
				player.setFreezeDelay(8);
				if (disablePrayers) {
					player.getPackets().sendGameMessage("You've been injured and you cannot use protective " + (player.getPrayer().isAncientCurses() ? "curses" : "protective prayers") + "!");
					player.setPrayerDelay(1000);
				}
			}
		}, 1);
	}

	public static void sendMagicalAttack(YkLagorThunderous npc, boolean specialAttack) {
		npc.animate(new Animation(14525));
		npc.gfx(new Graphics(2754));
		if (specialAttack) {
			npc.setNextForceTalk(new ForceTalk("You dare steal my power?!"));
			//npc.playSoundEffect(1926);
		} else if (Utils.random(5) == 0) {
			npc.setNextForceTalk(new ForceTalk("Fear my wrath!"));
			/////////npc.playSoundEffect(1927);
		}
		if (npc.getPossibleTargets().size() > 0) {
			for (Player player : npc.getManager().getParty().getTeam()) {
				if (player.isDead() || !npc.getManager().isAtBossRoom(player))
					continue;
				World.sendElementalProjectile(npc, player, 2733);
				delayHit(npc, 1, player, getMagicHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, player)));
				player.gfx(new Graphics(2755, 85, 0));
			}
		}
		if (specialAttack) {
			for (YkLagorMage mage : npc.getMages()) {
				if (mage.isDead() || mage.hasFinished())
					continue;
				mage.applyHit(new Hit(npc, mage.getMaxHitpoints(), HitLook.MAGIC_DAMAGE, 60));
				//delayHit(mage, 1, mage, getMagicHit(npc, mage.getMaxHitpoints()));
				mage.gfx(new Graphics(2755, 85, 0));
			}
			//for mages kill blablalb,we dont want to kill familiars lol
		}

	}
}
