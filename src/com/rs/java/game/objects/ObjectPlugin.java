package com.rs.java.game.objects;

import com.rs.java.game.WorldObject;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.Player;

public abstract class ObjectPlugin {
	
	public abstract Object[] getKeys();

	public boolean processObject(Player player, WorldObject object) {
		return false;
	}
	
	public boolean processObject2(Player player, WorldObject object) {
		return false;
	}
	
	public boolean processObject3(Player player, WorldObject object) {
		return false;
	}
	
	public boolean processObject4(Player player, WorldObject object) {
		return false;
	}
	
	public boolean processObject5(Player player, WorldObject object) {
		return false;
	}
	
	public boolean processItemOnObject(Player player, WorldObject object, Item item) {
		return false;
	}
	

	public int getDistance() {
		return 0;
	}

}
