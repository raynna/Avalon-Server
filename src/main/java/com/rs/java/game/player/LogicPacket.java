package com.rs.java.game.player;

import com.rs.core.packets.InputStream;

public class LogicPacket {

	private final int id;
	private final byte[] data;
	private final boolean network; // source flag

	public LogicPacket(int id, int size, InputStream stream, boolean network) {
		this.id = id;
		this.network = network;
		this.data = new byte[size];
		stream.getBytes(data, 0, size);
	}

	public int getId() {
		return id;
	}

	public byte[] getData() {
		return data;
	}

	public boolean isNetwork() {
		return network;
	}
}
