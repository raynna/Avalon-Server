package com.rs.java.game.player;

import com.rs.java.game.WorldObject;

public class PossessedObject extends WorldObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -543150569322118775L;

	private transient Player owner;

	public PossessedObject(Player owner, int id, int type, int rotation, int x, int y, int plane) {
		super(id, type, rotation, x, y, plane);
		setOwner(owner);
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}
}
