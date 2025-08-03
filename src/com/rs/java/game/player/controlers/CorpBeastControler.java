package com.rs.java.game.player.controlers;

import com.rs.Settings;
import com.rs.java.game.Animation;
import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;

public class CorpBeastControler extends Controler {

	@Override
	public void start() {

	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 37929 || object.getId() == 38811) {
			removeControler();
			player.stopAll();
			player.setNextWorldTile(new WorldTile(2970, 4384, player.getPlane()));
			return false;
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeControler();
	}

	@Override
	public boolean sendDeath() {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.animate(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					Player killer = player.getMostDamageReceivedSourcePlayer();
					if (killer != null) {
						killer.removeDamage(player);
						killer.increaseKillCount(player);
					}
					player.sendItemsOnDeath(player, true);
					player.getEquipment().init();
					player.getInventory().init();
					player.reset();
					player.setNextWorldTile(new WorldTile(Settings.RESPAWN_PLAYER_LOCATION));
					player.animate(new Animation(-1));
				} else if (loop == 4) {
					removeControler();
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean login() {
		return false; // so doesnt remove script
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

}
