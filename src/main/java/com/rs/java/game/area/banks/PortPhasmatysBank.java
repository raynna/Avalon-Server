package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class PortPhasmatysBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Port Phasmatys Bank";
	}

	public PortPhasmatysBank() {
		super(
				new Polygon(new WorldTile[] {
				new WorldTile(3686, 3461, 0),
				new WorldTile(3686, 3471, 0),
				new WorldTile(3699, 3471, 0),
				new WorldTile(3699, 3461, 0),
				new WorldTile(3686, 3461, 0),
		}) );
	}

	@Override
	public boolean member() {
		return false;
	}

	@Override
	public Environment environment() {
		return Environment.SAFEZONE;
	}

}
