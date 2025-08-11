package com.rs.java.game.cityhandler;

import com.rs.java.game.WorldObject;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;

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