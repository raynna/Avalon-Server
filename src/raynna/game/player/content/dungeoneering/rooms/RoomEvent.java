package raynna.game.player.content.dungeoneering.rooms;

import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.game.player.content.dungeoneering.RoomReference;

public interface RoomEvent {

	public void openRoom(DungeonManager dungeon, RoomReference reference);
}
