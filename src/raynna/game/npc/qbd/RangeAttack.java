package raynna.game.npc.qbd;

import raynna.game.Animation;
import raynna.game.Graphics;
import raynna.game.Hit;
import raynna.game.Hit.HitLook;
import raynna.game.player.Player;
import raynna.game.player.actions.combat.Combat;
import raynna.game.player.prayer.AncientPrayer;
import raynna.game.player.prayer.NormalPrayer;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;

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