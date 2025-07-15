package com.rs.java.game.player.content.dungeoneering.rooms;

import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;

public interface RoomEvent {

	public void openRoom(DungeonManager dungeon, RoomReference reference);
}
