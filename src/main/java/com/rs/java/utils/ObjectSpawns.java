package com.rs.java.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.rs.java.game.World;
import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;

public final class ObjectSpawns {

	private static final String PATH = System.getProperty("user.dir") + "/data/map/packedSpawns";
	private static final String TXT_PATH = System.getProperty("user.dir") + "/data/map/unpackedSpawnsList.txt";

	public static void init() {
		File packedDir = new File(PATH);
		if (!packedDir.exists()) {
			if (!packedDir.mkdirs()) {
				throw new RuntimeException("Couldn't create packedSpawns directory: " + PATH);
			}
			packObjectSpawns();
		}
	}

	private static void packObjectSpawns() {
		Logger.log("ObjectSpawns", "Packing object spawns...");
		File txtFile = new File(TXT_PATH);
		if (!txtFile.exists()) {
			throw new RuntimeException("Missing unpacked spawns file: " + TXT_PATH);
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("//")) {
					continue;
				}

				String[] split = line.split(" - ");
				if (split.length != 2) {
					Logger.log("ObjectSpawns", "Skipping malformed line (missing -): " + line);
					continue;
				}

				String[] objectData = split[0].split(" ");
				String[] tileData = split[1].split(" ");

				if (objectData.length != 3 || tileData.length != 4) {
					Logger.log("ObjectSpawns", "Skipping malformed line (bad format): " + line);
					continue;
				}

				try {
					int objectId = Integer.parseInt(objectData[0]);
					int type = Integer.parseInt(objectData[1]);
					int rotation = Integer.parseInt(objectData[2]);

					int x = Integer.parseInt(tileData[0]);
					int y = Integer.parseInt(tileData[1]);
					int plane = Integer.parseInt(tileData[2]);
					boolean clipped = Boolean.parseBoolean(tileData[3]);

					WorldTile tile = new WorldTile(x, y, plane);
					addObjectSpawn(objectId, type, rotation, tile.getRegionId(), tile, clipped);
				} catch (NumberFormatException e) {
					Logger.log("ObjectSpawns", "Skipping malformed number in line: " + line);
				}
			}
		} catch (IOException e) {
			Logger.handle(e);
		}
	}

	public static void loadObjectSpawns(int regionId) {
		File file = new File(PATH + "/" + regionId + ".os");
		if (!file.exists()) {
			return;
		}

		try (RandomAccessFile raf = new RandomAccessFile(file, "r");
			 FileChannel channel = raf.getChannel()) {

			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());

			while (buffer.hasRemaining()) {
				int objectId = buffer.getShort() & 0xFFFF;
				int type = buffer.get() & 0xFF;
				int rotation = buffer.get() & 0xFF;
				int plane = buffer.get() & 0xFF;
				int x = buffer.getShort() & 0xFFFF;
				int y = buffer.getShort() & 0xFFFF;
				boolean clipped = buffer.get() == 1;

				World.spawnObject(new WorldObject(objectId, type, rotation, x, y, plane));
			}
		} catch (IOException e) {
			Logger.handle(e);
		}
	}

	private static void addObjectSpawn(int objectId, int type, int rotation, int regionId, WorldTile tile, boolean clipped) {
		File outFile = new File(PATH + "/" + regionId + ".os");

		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(outFile, true))) {
			out.writeShort(objectId);
			out.writeByte(type);
			out.writeByte(rotation);
			out.writeByte(tile.getPlane());
			out.writeShort(tile.getX());
			out.writeShort(tile.getY());
			out.writeBoolean(clipped);
		} catch (IOException e) {
			Logger.handle(e);
		}
	}

	private ObjectSpawns() {
		// Utility class – no instantiation
	}
}
