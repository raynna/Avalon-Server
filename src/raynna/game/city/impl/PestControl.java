package raynna.game.city.impl;

import raynna.game.WorldObject;
import raynna.game.city.CityEvent;
import raynna.game.city.CityEventHandler;
import raynna.game.item.Item;
import raynna.game.minigames.pest.CommendationExchange;
import raynna.game.npc.NPC;
import raynna.game.player.Player;

public class PestControl implements CityEvent {

	@Override
	public boolean init() {
		return registerNPC(5956, this);
	}

	@Override
	public boolean handleNPCClick(Player player, NPC npc) {
		switch(npc.getId()) {
		/**
		 * Knight
		 */
		case 5956:
			CommendationExchange.openExchangeShop(player);
			break;
		}
		return false;
	}

	@Override
	public boolean handleNPCClick2(Player player, NPC npc) {
		switch(npc.getId()) {
		/**
		 * Void Knights
		 */
		case 5956:
			CommendationExchange.openExchangeShop(player);
			break;
		}
		return false;
	}

	@Override
	public boolean handleNPCClick3(Player player, NPC npc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleNPCClick4(Player player, NPC npc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, WorldObject object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleObjectClick2(Player player, WorldObject object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleObjectClick3(Player player, WorldObject object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleObjectClick4(Player player, WorldObject object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleObjectClick5(Player player, WorldObject object) {
		// TODO Auto-generated method stub
		return false;
	}

	boolean registerNPC(int npcId, CityEvent cityevent) {
		return CityEventHandler.registerNPCs(npcId, this);
	}

	boolean registerObject(int objectId, CityEvent cityEvent) {
		return CityEventHandler.registerObjects(objectId, this);
	}

	@Override
	public boolean handleItemOnObject(Player player, WorldObject object, Item item) {
		// TODO Auto-generated method stub
		return false;
	}

}
