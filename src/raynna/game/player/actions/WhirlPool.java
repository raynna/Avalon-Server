package raynna.game.player.actions;

import raynna.core.thread.CoresManager;
import raynna.game.Animation;
import raynna.game.ForceMovement;
import raynna.game.Graphics;
import raynna.game.WorldTile;
import raynna.game.player.Player;
import raynna.game.player.content.FadingScreen;
import raynna.util.Logger;
import raynna.util.Utils;

/**
 * @author Phillip
 * @Improved Andreas
 *
 */

public class WhirlPool extends Action {

	private final int ANIMATION = 6723;
	private int currentTime;
	private WorldTile tile;

	public static final WorldTile WHIRLPOOL_LOC = new WorldTile(2512, 3508, 0), TELE_LOC = new WorldTile(1764, 5365, 1);

	public WhirlPool(WorldTile tile) {
		this.setTile(tile);
	}

	@Override
	public boolean start(final Player player) {
		if (!Utils.inCircle(new WorldTile(2512, 3508, 0), player, 20))
			return false;
		return process(player);
	}

	@Override
	public int processWithDelay(final Player player) {
		if (currentTime++ == 0) {
			player.lock();
			player.addWalkSteps(2512, 3517);
		} else if (currentTime == 6) {
			player.animate(new Animation(ANIMATION));
			player.setNextForceMovement(new ForceMovement(player, 1, WHIRLPOOL_LOC, 7, ForceMovement.SOUTH));
		} else if (currentTime == 11) {
			final long time = FadingScreen.fade(player);
			player.getPackets().sendGameMessage("You jump into the whirlpool!", true);
			CoresManager.getSlowExecutor().execute(() -> {
                try {
                    FadingScreen.unfade(player, time, () -> {
                    });
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            });
		} else if (currentTime == 13) {
			player.setNextWorldTile(TELE_LOC);
		} else if (currentTime == 14) {
			player.setNextWorldTile(TELE_LOC);
			player.unlock();
		} else if (currentTime == 15)
			return -1;
		return 0;
	}

	@Override
	public boolean process(Player player) {
		if (player.getAttackedByDelay() + 10000 > Utils.currentTimeMillis()) {
			return false;
		}
		return true;
	}

	@Override
	public void stop(Player player) {
		player.animate(new Animation(-1));
		player.gfx(new Graphics(-1));
	}

	public WorldTile getTile() {
		return tile;
	}

	public void setTile(WorldTile tile) {
		this.tile = tile;
	}
}