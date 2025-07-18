package com.rs.java.game.npc.qbd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.player.Player;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

/**
 * Represents the Queen Black Dragon's fire wall attack.
 * 
 * @author Emperor
 *
 */
public final class FireWallAttack implements QueenAttack {

	/**
	 * The wall graphic ids.
	 */
	private static final int[] WALL_GRAPHIC_IDS = { 3158, 3159, 3160 };

	/**
	 * The animation.
	 */
	private static final Animation ANIMATION = new Animation(16746);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		int waves = npc.getPhase();
		if (waves > 3) {
			waves = 3;
		}
		npc.animate(ANIMATION);
		final List<Integer> wallIds = new ArrayList<Integer>();
		for (int id : WALL_GRAPHIC_IDS) {
			wallIds.add(id);
		}
		Collections.shuffle(wallIds);
		victim.getPackets().sendGameMessage("<col=FF9900>The Queen Black Dragon takes a huge breath.</col>");
		final int wallCount = waves;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				for (int i = 0; i < wallCount; i++) {
					final int wallId = wallIds.get(i);
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							for (int j = 0; j < 2; j++) {
								final boolean second = j == 1;
								WorldTasksManager.schedule(new WorldTask() {
									int y = 37 + (second ? 1 : 0);

									@Override
									public void run() {
										if (!((wallId == 3158 && victim.getX() == npc.getBase().getX() + 28)
												|| (wallId == 3159 && victim.getX() == npc.getBase().getX() + 37)
												|| (wallId == 3160 && victim.getX() == npc.getBase().getX() + 32))) {
											if (victim.getY() == npc.getBase().getY() + y) {
												int hit;
												String message = FireBreathAttack.getProtectMessage(victim);
												if (message == null) {
													victim.getPackets().sendGameMessage(
															"You are horribly burned by the fire wall!");
													hit = Utils.random(400, 765);
												} else {
													victim.getPackets().sendGameMessage(message);
													hit = Utils.random(200, message.contains("prayer") ? 450 : 260);
												}
												victim.applyHit(new Hit(npc, hit, HitLook.REGULAR_DAMAGE));
											}
										}
										if (--y == 19) {
											stop();
										}
									}
								}, 0, 0);
							}
							victim.getPackets().sendProjectile(null, npc.getBase().transform(33, 38, 0),
									npc.getBase().transform(33, 19, 0), wallId, 0, 0, 18, 46, 0, 0, 0);
						}

					}, (i * 7) + 1);
				}
			}
		}, 1);
		npc.temporaryAttribute().put("fire_wall_tick_", npc.getTicks() + Utils.random((waves * 7) + 5, 60)); // Don't
																												// make
																												// it
																												// too
																												// often.
		return 8 + (waves * 2);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		Integer tick = (Integer) npc.temporaryAttribute().get("fire_wall_tick_");
		return tick == null || tick < npc.getTicks();
	}

}