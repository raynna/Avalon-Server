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
 * Represents a default fire breath attack.
 * 
 * @author Emperor
 *
 */
public final class FireBreathAttack implements QueenAttack {

	/**
	 * The animation of the attack.
	 */
	private static final Animation ANIMATION = new Animation(16721);

	/**
	 * The graphic of the attack.
	 */
	private static final Graphics GRAPHIC = new Graphics(3143);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		npc.animate(ANIMATION);
		npc.gfx(GRAPHIC);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				super.stop();
				String message = getProtectMessage(victim);
				int hit;
				if (message != null) {
					hit = Utils.random(60 + Utils.random(150), message.contains("prayer") ? 460 : 235);
					victim.getPackets().sendGameMessage(message);
				} else {
					hit = Utils.random(400, 710);
					victim.getPackets().sendGameMessage("You are horribly burned by the dragon's breath!");
				}
				victim.animate(new Animation(Combat.getDefenceEmote(victim)));
				victim.applyHit(new Hit(npc, hit, HitLook.REGULAR_DAMAGE));
			}
		}, 1);
		return Utils.random(4, 15); // Attack delay seems to be random a lot.
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return true;
	}

	/**
	 * Gets the dragonfire protect message.
	 * 
	 * @param player
	 *            The player.
	 * @return The message to send, or {@code null} if the player was
	 *         unprotected.
	 */
	public static final String getProtectMessage(Player player) {
		if (Combat.hasAntiDragProtection(player)) {
			return "Your shield absorbs most of the dragon's breath!";
		}
		if (player.getAntifire() > Utils.currentTimeMillis() || player.getSuperAntifire() > Utils.currentTimeMillis()) {
			return "Your potion absorbs most of the dragon's breath!";
		}
		if (player.getPrayer().isActive(NormalPrayer.PROTECT_FROM_MAGIC) || player.getPrayer().isActive(AncientPrayer.DEFLECT_MAGIC)) {
			return "Your prayer absorbs most of the dragon's breath!";
		}
		return null;
	}
}