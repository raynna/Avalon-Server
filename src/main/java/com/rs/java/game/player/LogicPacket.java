package com.rs.java.game.player;

import com.rs.core.packets.InputStream;

public class LogicPacket {

	private final int id;
	private final byte[] data;
	private final boolean network; // source flag

	public LogicPacket(int id, byte[] data, boolean network) {
		this.id = id;
		this.network = network;
		this.data = data;
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
