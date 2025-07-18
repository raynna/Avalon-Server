package com.rs.java.game;

import java.io.Serializable;

import com.rs.Settings;
import com.rs.java.utils.Utils;

public class WorldTile implements Serializable {

	private static final long serialVersionUID = -6567346497259686765L;

	private short x, y;
	private byte plane;

	public WorldTile(int x, int y, int plane) {
		this.x = (short) x;
		this.y = (short) y;
		this.plane = (byte) plane;
	}

	public WorldTile(int x, int y, int plane, int size) {
		this.x = (short) getCoordFaceX(x, size, size, -1);
		this.y = (short) getCoordFaceY(y, size, size, -1);
		this.plane = (byte) plane;
	}

	public WorldTile(WorldTile tile) {
		this.x = tile.x;
		this.y = tile.y;
		this.plane = tile.plane;
	}

	public WorldTile(WorldTile tile, int randomize) {
		this.x = (short) (tile.x + Utils.getRandom(randomize * 2) - randomize);
		this.y = (short) (tile.y + Utils.getRandom(randomize * 2) - randomize);
		this.plane = tile.plane;
	}

	public WorldTile(int hash) {
		this.x = (short) (hash >> 14 & 0x3fff);
		this.y = (short) (hash & 0x3fff);
		this.plane = (byte) (hash >> 28);
	}

	public void moveLocation(int xOffset, int yOffset, int planeOffset) {
		x += xOffset;
		y += yOffset;
		plane += planeOffset;
	}

	public final void setLocation(WorldTile tile) {
		setLocation(tile.x, tile.y, tile.plane);
	}

	public final void setLocation(int x, int y, int plane) {
		this.x = (short) x;
		this.y = (short) y;
		this.plane = (byte) plane;
	}

	public int getX() {
		return x;
	}

	public int getXInRegion() {
		return x & 0x3F;
	}

	public int getYInRegion() {
		return y & 0x3F;
	}

	public int getXInChunk() {
		return x & 0x7;
	}

	public int getYInChunk() {
		return y & 0x7;
	}

	public int getY() {
		return y;
	}

	public int getPlane() {
		if (plane > 3)
			return 3;
		return plane;
	}
	
	public void setX(int x) {
		this.x = (short) x;
	}
	
	public void setY(int y) {
		this.y = (short) y;
	}
	
	public void setPlane(int z) {
		this.plane = (byte) z;
	}

	public int getChunkX() {
		return (x >> 3);
	}

	public int getChunkY() {
		return (y >> 3);
	}

	public int getRegionX() {
		return (x >> 6);
	}

	public int getRegionY() {
		return (y >> 6);
	}

	public int getRegionId() {
		return ((getRegionX() << 8) + getRegionY());
	}

	public int getLocalX(WorldTile tile, int mapSize) {
		return x - 8 * (tile.getChunkX() - (Settings.MAP_SIZES[mapSize] >> 4));
	}

	public int getLocalY(WorldTile tile, int mapSize) {
		return y - 8 * (tile.getChunkY() - (Settings.MAP_SIZES[mapSize] >> 4));
	}

	public int getLocalX(WorldTile tile) {
		return getLocalX(tile, 0);
	}

	public int getLocalY(WorldTile tile) {
		return getLocalY(tile, 0);
	}

	public int getLocalX() {
		return getLocalX(this);
	}

	public int getLocalY() {
		return getLocalY(this);
	}

	public int getRegionHash() {
		return getRegionY() + (getRegionX() << 8) + (plane << 16);
	}

	public int getTileHash() {
		return y + (x << 14) + (plane << 28);
	}

	public boolean withinDistance(WorldTile tile, int distance) {
		if (tile.plane != plane)
			return false;
		int deltaX = tile.x - x, deltaY = tile.y - y;
		return deltaX <= distance && deltaX >= -distance && deltaY <= distance && deltaY >= -distance;
	}

	public boolean withinDistance(WorldTile tile) {
		if (tile.plane != plane)
			return false;
		return Math.abs(tile.x - x) <= 14 && Math.abs(tile.y - y) <= 14;
	}

	public int getCoordFaceX(int sizeX) {
		return getCoordFaceX(sizeX, -1, -1);
	}

	public static final int getCoordFaceX(int x, int sizeX, int sizeY, int rotation) {
		return x + ((rotation == 1 || rotation == 3 ? sizeY : sizeX) - 1) / 2;
	}

	public static final int getCoordFaceY(int y, int sizeX, int sizeY, int rotation) {
		return y + ((rotation == 1 || rotation == 3 ? sizeX : sizeY) - 1) / 2;
	}

	public int getCoordFaceX(int sizeX, int sizeY, int rotation) {
		return x + ((rotation == 1 || rotation == 3 ? sizeY : sizeX) - 1) / 2;
	}

	public int getCoordFaceY(int sizeY) {
		return getCoordFaceY(-1, sizeY, -1);
	}

	public int getCoordFaceY(int sizeX, int sizeY, int rotation) {
		return y + ((rotation == 1 || rotation == 3 ? sizeX : sizeY) - 1) / 2;
	}

	public WorldTile transform(int x, int y, int plane) {
		return new WorldTile(this.x + x, this.y + y, this.plane + plane);
	}

	/**
	 * Checks if this world tile's coordinates match the other world tile.
	 * 
	 * @param other
	 *            The world tile to compare with.
	 * @return {@code True} if so.
	 */
	public boolean matches(WorldTile other) {
		return x == other.x && y == other.y && plane == other.plane;
	}

	public boolean withinArea(int a, int b, int c, int d) {
		return getX() >= a && getY() >= b && getX() <= c && getY() <= d;
	}

	public long getFixedUniqueId() {
		return x* 2311 + x *277 + plane *2371;
	}


	public int getChunkXInScene(Entity entity) {
		return getChunkX() - getChunkXSceneOffset(entity);
	}

	public int getChunkXSceneOffset(Entity entity) {
		return entity.getLastLoadedMapRegionTile().getChunkX() - Settings.MAP_SIZES[entity.getMapSize()] / 16;
	}

	public int getChunkYInScene(Entity entity) {
		return getChunkY() - getChunkYSceneOffset(entity);
	}

	public int getChunkYSceneOffset(Entity entity) {
		return entity.getLastLoadedMapRegionTile().getChunkY() - Settings.MAP_SIZES[entity.getMapSize()] / 16;
	}
	
	public WorldTile getStartWorldTile() {
		return new WorldTile(this.x, this.y, this.plane);
	}
	
	/**
     * returs the tile, idk why this is not here
     * @return
     */
    public WorldTile getTile() {
    	return new WorldTile(x,y,plane);
      }
}
