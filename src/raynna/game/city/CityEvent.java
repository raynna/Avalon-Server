package raynna.game.city;

import raynna.game.WorldObject;
import raynna.game.item.Item;
import raynna.game.npc.NPC;
import raynna.game.player.Player;

/**
 *
 * @author Austin
 *
 */

public interface CityEvent {

    public boolean init();

    public boolean handleNPCClick(Player player, NPC npc);

    public boolean handleNPCClick2(Player player, NPC npc);

    public boolean handleNPCClick3(Player player, NPC npc);

    public boolean handleNPCClick4(Player player, NPC npc);

    public boolean handleObjectClick(Player player, WorldObject object);

    public boolean handleObjectClick2(Player player, WorldObject object);

    public boolean handleObjectClick3(Player player, WorldObject object);

    public boolean handleObjectClick4(Player player, WorldObject object);

    public boolean handleObjectClick5(Player player, WorldObject object);

	public boolean handleItemOnObject(Player player, WorldObject object, Item item);


}