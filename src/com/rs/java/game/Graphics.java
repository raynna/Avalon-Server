package com.rs.java.game;

public final class Graphics {

	private int id, height, speed, rotation;

	public Graphics(int id) {
		this(id, 0, 0, 0);
	}

	public Graphics(int id, int height) {
		this(id, 0, height, 0);
	}

	public Graphics(int id, int rotation, int height) {
		this(id, 0, height, rotation);
	}


	public Graphics(int id, int speed, int height, int rotation) {
		this.id = id;
		this.speed = speed;
		this.height = height;
		this.rotation = rotation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + id;
		result = prime * result + rotation;
		result = prime * result + speed;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Graphics other = (Graphics) obj;
		if (height != other.height)
			return false;
		if (id != other.id)
			return false;
		if (rotation != other.rotation)
			return false;
		if (speed != other.speed)
			return false;
		return true;
	}

	public int getId() {
		if (id == 0)
			return -1;
		return id;
	}

	public int getSettingsHash() {
		return (speed & 0xffff) | (height << 16);
	}

	public int getSettings2Hash() {
		int hash = 0;
		hash |= rotation & 0x7;
		return hash;
	}

	public int getSpeed() {
		return speed;
	}

	public int getHeight() {
		return height;
	}

	public int getRotation() { return rotation; }
}
