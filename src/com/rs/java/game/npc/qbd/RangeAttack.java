package com.rs.java.game.npc.qbd;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.prayer.AncientPrayer;
import com.rs.java.game.player.prayer.NormalPrayer;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

/**
 * Handles the Queen Black Dragon's range attack.
 * 
 * @author Emperor
 *
 */
public final class RangeAttack implements QueenAttack {

	/**
	 * The animation.
	 */
	private static final Animation ANIMATION = new Animation(16718);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		npc.animate(ANIMATION);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				stop();
				int hit;
				if (victim.getPrayer().isActive(AncientPrayer.DEFLECT_MISSILES)) {
					victim.animate(new Animation(12573));
					victim.gfx(new Graphics(2229));
					victim.getPackets().sendGameMessage("You are unable to reflect damage back to this creature.");
					hit = 0;
				} else if (victim.getPrayer().isActive(NormalPrayer.PROTECT_FROM_MISSILES)) {
					victim.animate(new Animation(Combat.getDefenceEmote(victim)));
					hit = 0;
				} else {
					hit = Utils.random(0 + Utils.random(150), 360);
					victim.animate(new Animation(Combat.getDefenceEmote(victim)));
				}
				victim.applyHit(new Hit(npc, hit, hit == 0 ? HitLook.MISSED : HitLook.RANGE_DAMAGE));
			}
		}, 1);
		return Utils.random(4, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return true;
	}

}