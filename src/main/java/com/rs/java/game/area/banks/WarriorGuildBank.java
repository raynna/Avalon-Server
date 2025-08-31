package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class WarriorGuildBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Warrior Guild Bank";
	}

	public WarriorGuildBank() {
		super(
				new Polygon(new WorldTile[] {
				new WorldTile(2843, 3533, 0),
				new WorldTile(2843, 3537, 0),
				new WorldTile(2841, 3537, 0),
				new WorldTile(2841, 3540, 0),
				new WorldTile(2848, 3540, 0),
				new WorldTile(2848, 3533, 0),
				new WorldTile(2843, 3533, 0),
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
