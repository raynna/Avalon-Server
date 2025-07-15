package com.rs.java.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;

import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;

public final class NPCSpawns {

	private static final Object lock = new Object();
	private static final String PACKED_PATH = System.getProperty("user.dir") + "/data/npcs/packedSpawns/";
	private static final String UNPACKED_PATH = System.getProperty("user.dir") + "/data/npcs/unpackedSpawnsList.txt";
	private static BufferedWriter writer;
	private static BufferedReader in;
	private static BufferedWriter writer2;
	private static BufferedReader in2;

	public static boolean removeSpawn(NPC npc) throws Throwable {
		synchronized (lock) {
			List<String> page = new ArrayList<String>();
			File file = new File(UNPACKED_PATH);
			in = new BufferedReader(new FileReader(file));
			String line;
			boolean removed = false;
			int id = npc.getId();
			WorldTile tile = npc.getRespawnTile();
			while ((line = in.readLine()) != null) {
				if (line.equals(id + " - " + tile.getX() + " " + tile.getY() + " " + tile.getPlane())) {
					page.remove(page.get(page.size() - 1)); // description
					removed = true;
					continue;
				}
				page.add(line);
			}
			if (!removed)
				return false;
			file.delete();
			writer2 = new BufferedWriter(new FileWriter(file));
			for (String l : page) {
				writer2.write(l);
				writer2.newLine();
				writer2.flush();
			}
			npc.finish();
			return true;
		}
	}

	public static final void init() {
		if (!new File(PACKED_PATH).exists())
			packNPCSpawns();
	}

	private static final void packNPCSpawns() {
		Logger.log("NPCSpawns", "Packing npc spawns...");
		if (!new File(PACKED_PATH).mkdir())
			throw new RuntimeException("Couldn't create packedSpawns directory.");
		try {
			in2 = new BufferedReader(new FileReader(UNPACKED_PATH));
			while (true) {
				String line = in2.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				if (line.startsWith("RSBOT"))
					continue;
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length != 2)
					throw new RuntimeException("Invalid NPC Spawn line: " + line);
				int npcId = Integer.parseInt(splitedLine[0]);
				String[] splitedLine2 = splitedLine[1].split(" ", 5);
				if (splitedLine2.length != 3 && splitedLine2.length != 5)
					throw new RuntimeException("Invalid NPC Spawn line: " + line);
				WorldTile tile = new WorldTile(Integer.parseInt(splitedLine2[0]), Integer.parseInt(splitedLine2[1]),
						Integer.parseInt(splitedLine2[2]));
				int mapAreaNameHash = -1;
				boolean canBeAttackFromOutOfArea = true;
				if (splitedLine2.length == 5) {
					mapAreaNameHash = Utils.getNameHash(splitedLine2[3]);
					canBeAttackFromOutOfArea = Boolean.parseBoolean(splitedLine2[4]);
				}
				addNPCSpawn(npcId, tile.getRegionId(), tile, mapAreaNameHash, canBeAttackFromOutOfArea);
			}
			in2.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void loadNPCSpawns(int regionId) {
		File file = new File(PACKED_PATH + regionId + ".ns");
		if (!file.exists())
			return;
		try {
			RandomAccessFile in = new RandomAccessFile(file, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			while (buffer.hasRemaining()) {
				int npcId = buffer.getShort() & 0xffff;
				int plane = buffer.get() & 0xff;
				int x = buffer.getShort() & 0xffff;
				int y = buffer.getShort() & 0xffff;
				boolean hashExtraInformation = buffer.get() == 1;
				int mapAreaNameHash = -1;
				boolean canBeAttackFromOutOfArea = true;
				if (hashExtraInformation) {
					mapAreaNameHash = buffer.getInt();
					canBeAttackFromOutOfArea = buffer.get() == 1;
				}
				World.spawnNPC(npcId, new WorldTile(x, y, plane), mapAreaNameHash, canBeAttackFromOutOfArea);
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static final void addNPCSpawn(int npcId, int regionId, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		try {
			DataOutputStream out = new DataOutputStream(
					new FileOutputStream(PACKED_PATH + regionId + ".ns", true));
			out.writeShort(npcId);
			out.writeByte(tile.getPlane());
			out.writeShort(tile.getX());
			out.writeShort(tile.getY());
			out.writeBoolean(mapAreaNameHash != -1);
			if (mapAreaNameHash != -1) {
				out.writeInt(mapAreaNameHash);
				out.writeBoolean(canBeAttackFromOutOfArea);
			}
			out.flush();
			out.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private NPCSpawns() {
	}
}
