package com.rs.java.game.area.multi;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.shapes.Polygon;
import com.rs.java.game.area.shapes.Rectangle;

public class FaladorMulti extends Area {

	public FaladorMulti() {
		super(
				// Regions
				new Integer[]{11829, 11828, 11827},
				// West of farm
				new Rectangle(new WorldTile(3008, 3304, 0), new WorldTile(3015, 3327, 0)),
				// Pigpen
				new Rectangle(new WorldTile(3014, 3303, 0), new WorldTile(3020, 3311, 0)),
				//Cow pen
				new Polygon(new WorldTile[]{
						new WorldTile(3014, 3303, 0),
						new WorldTile(3021, 3312, 0),
						new WorldTile(3022, 3313, 0),
						new WorldTile(3042, 3313, 0),
						new WorldTile(3043, 3312, 0),
						new WorldTile(3043, 3299, 0),
						new WorldTile(3041, 3297, 0),
						new WorldTile(3014, 3303, 0),
				})
		);
	}

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Falador Multi";
	}

	@Override
	public boolean member() {
		return false;
	}

	@Override
	public Environment environment() {
		return Environment.MULTI;
	}
}