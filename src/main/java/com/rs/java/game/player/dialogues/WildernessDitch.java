package com.rs.java.game.player.dialogues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.ForceMovement;
import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.controllers.EdgevillePvPController;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;

public class WildernessDitch extends Dialogue {

	private transient static final List<Player> playersOn = Collections.synchronizedList(new ArrayList<Player>());

	private WorldObject ditch;

	public static int getPlayersCount() {
		return playersOn.size();
	}

	@Override
	public void start() {
		ditch = (WorldObject) parameters[0];
		player.getInterfaceManager().sendInterface(382);
	}

	public static void removePlayer(Player player) {
		playersOn.remove(player);
	}

	public static void addPlayer(Player player) {
		playersOn.add(player);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (interfaceId == 382 && componentId == 19) {
			player.stopAll();
			player.lock(3);
			player.animate(new Animation(6132));

			int dx = 0, dy = 0;
			switch (ditch.getRotation()) {
				case 0: // facing south
					dy = (player.getY() > ditch.getY()) ? -3 : +3;
					break;
				case 2: // facing north
					dy = (player.getY() < ditch.getY()) ? +3 : -3;
					break;
				case 1: // facing west
					dx = (player.getX() < ditch.getX()) ? +3 : -3;
					break;
				case 3: // facing east
					dx = (player.getX() > ditch.getX()) ? -3 : +3;
					break;
			}

			final WorldTile toTile = new WorldTile(
					player.getX() + dx,
					player.getY() + dy,
					ditch.getPlane()
			);

			int direction;
			if (dx > 0) direction = ForceMovement.EAST;
			else if (dx < 0) direction = ForceMovement.WEST;
			else if (dy > 0) direction = ForceMovement.NORTH;
			else direction = ForceMovement.SOUTH;

			player.setNextForceMovement(
					new ForceMovement(new WorldTile(player), 1, toTile, 2, direction)
			);

			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(toTile);
					player.faceObject(ditch);
					if (!EdgevillePvPController.isAtPvP(player)) {
						player.getControlerManager().startControler("WildernessControler");
						player.resetReceivedDamage();
						playersOn.add(player);
					}
				}
			}, 2);
		} else {
			player.closeInterfaces();
		}
		playersOn.remove(player);
		end();
	}


	@Override
	public void finish() {

	}

}
