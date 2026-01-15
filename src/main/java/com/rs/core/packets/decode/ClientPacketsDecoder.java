package com.rs.core.packets.decode;

import com.rs.Settings;
import com.rs.core.packets.InputStream;
import com.rs.core.networking.Session;

public final class ClientPacketsDecoder extends Decoder {

	public ClientPacketsDecoder(Session connection) {
		super(connection);
	}

	@Override
	public void decode(InputStream stream) {
		int packetId = stream.readUnsignedByte();

		switch (packetId) {
			case 14 -> decodeLogin(stream);
			case 15 -> decodeGrab(stream);
			default -> {
				session.getChannel().close();
			}
		}
	}

	private void decodeLogin(InputStream stream) {
		if (stream.getRemaining() != 0) {
			session.getChannel().close();
			return;
		}

		session.setDecoder(2);
		session.setEncoder(1);
		session.getLoginPackets().sendStartUpPacket();
	}

	private void decodeGrab(InputStream stream) {
		int size = stream.readUnsignedByte();
		if (stream.getRemaining() < size) {
			session.getChannel().close();
			return;
		}

		session.setEncoder(0);

		if (stream.readInt() != Settings.CLIENT_BUILD ||
				stream.readInt() != Settings.CUSTOM_CLIENT_BUILD) {
			session.getGrabPackets().sendOutdatedClientPacket();
			return;
		}

		session.setDecoder(1);
		session.getGrabPackets().sendStartUpPacket();
	}
}
