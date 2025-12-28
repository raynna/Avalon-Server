package com.rs.java.game;

import com.rs.kotlin.Rscm;

public final class Animation {

	private int[] ids;
	private int speed;

	public Animation(int id) {
		this(id, 0);
	}

	private static String normalizeAnimationKey(String animation) {
		return animation.startsWith("animation.") ? animation : "animation." + animation;
	}

	public static int getId(String name) {
		return Rscm.lookup(normalizeAnimationKey(name));
	}

	public Animation(String animation) {
		this(Rscm.lookup(normalizeAnimationKey(animation)), 0);
	}

	public Animation(int id, int speed) {
		this(id, id, id, id, speed); 
	} 

	public Animation(int id1, int id2, int id3, int id4, int speed) {
		this.ids = new int[] { id1, id2, id3, id4 };
		this.speed = speed;
	}

	public int[] getIds() {
		return ids;
	}

	public int getSpeed() {
		return speed;
	}
}
