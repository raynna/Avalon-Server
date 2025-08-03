package com.rs.java.game.player.dialogues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.ForceMovement;
import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.controlers.EdgevillePvPControler;
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
			player.lock(4);
			player.animate(new Animation(6132));
			final WorldTile toTile = new WorldTile(
					ditch.getRotation() == 3 || ditch.getRotation() == 1 ? ditch.getX() - 1 : player.getX(),
					ditch.getRotation() == 0 || ditch.getRotation() == 2 ? ditch.getY() + 2 : player.getY(),
					ditch.getPlane());
			player.setNextForceMovement(new ForceMovement(new WorldTile(player), 1, toTile, 2,
					ditch.getRotation() == 0 || ditch.getRotation() == 2 ? ForceMovement.NORTH : ForceMovement.WEST));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(toTile);
					player.faceObject(ditch);
					if (!EdgevillePvPControler.isAtPvP(player)) {
						player.getControlerManager().startControler("WildernessControler");
						player.resetReceivedDamage();
						playersOn.add(player);
					}
				}
			}, 2);
		} else
			player.closeInterfaces();
		playersOn.remove(player);
		end();
	}

	@Override
	public void finish() {

	}

}
