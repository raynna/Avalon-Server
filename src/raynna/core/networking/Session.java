package raynna.core.networking;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import raynna.core.packets.OutputStream;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import raynna.core.packets.InputStream;
import raynna.core.packets.decode.*;
import raynna.core.packets.encode.*;
import raynna.game.player.Player;
import raynna.game.player.content.Commands;
import raynna.util.IPBanL;
import raynna.util.Logger;
import raynna.util.Utils;

public class Session {

	private transient volatile Player player;

	public Player getPlayer() { return player; }
	public void setPlayer(Player player) { this.player = player; }
	public static final Set<Session> ACTIVE_SESSIONS = ConcurrentHashMap.newKeySet();

	private Channel channel;
	private volatile Decoder decoder;
	private volatile Encoder encoder;

	private static final String PATH = System.getProperty("user.dir") + "/data/iplog/log.txt";

	public Session(Channel channel) {
		this.channel = channel;
		ACTIVE_SESSIONS.add(this);

		if (IPBanL.isBanned(getIP())) {
			try {
				channel.disconnect();
			} finally {
				close();
			}
			return;
		}

		setDecoder(0);
		// logIp(this);
	}

	public void close() {
		ACTIVE_SESSIONS.remove(this);
		player = null;
	}

	public void logIp(Session session) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH, true))) {
			writer.write("[" + Commands.currentTime("dd MMMMM yyyy 'at' hh:mm:ss z") + "] - " + session.getIP());
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processIncomingData(byte[] data) {
		if (data == null || data.length == 0) return;
		if (channel == null || !channel.isConnected()) return;

		Decoder d = this.decoder;
		if (d == null) {
			Logger.log("SESSION", "Decoder NULL, dropping data for IP=" + getIP());
			return;
		}

		try {
			d.decode(new InputStream(data));
		} catch (Throwable t) {
			Logger.log("SESSION", "Decoder exception in " + d.getClass().getSimpleName() + " -> closing channel for IP=" + getIP());
			Logger.handle(t);
			try { channel.close(); } catch (Throwable ignored) {}
		}
	}

	public void setDecoder(int stage) {
		setDecoder(stage, null);
	}

	public void setDecoder(int stage, Object attachment) {
		switch (stage) {
			case 0 -> this.decoder = new ClientPacketsDecoder(this);
			case 1 -> this.decoder = new GrabPacketsDecoder(this);
			case 2 -> this.decoder = new LoginPacketsDecoder(this);
			case 3 -> {
				Player p = (Player) attachment;
				this.player = p;
				this.decoder = new WorldPacketsDecoder(this, p);
				if (p != null) {
					p.setPacketsDecoderPing(Utils.currentTimeMillis());
				}
			}
			default -> this.decoder = null;
		}
	}

	public void setEncoder(int stage) {
		setEncoder(stage, null);
	}

	public void setEncoder(int stage, Object attachment) {
		switch (stage) {
			case 0 -> this.encoder = new GrabPacketsEncoder(this);
			case 1 -> this.encoder = new LoginPacketsEncoder(this);
			case 2 -> {
				Player p = (Player) attachment;
				this.player = p;
				this.encoder = new WorldPacketsEncoder(this, p);
			}
			default -> this.encoder = null;
		}
	}

	public LoginPacketsEncoder getLoginPackets() {
		if (encoder instanceof LoginPacketsEncoder l)
			return l;
		throw new IllegalStateException("getLoginPackets() called but encoder=" +
				(encoder == null ? "null" : encoder.getClass().getSimpleName()));
	}

	public GrabPacketsEncoder getGrabPackets() {
		if (encoder instanceof GrabPacketsEncoder g)
			return g;
		throw new IllegalStateException("getGrabPackets() called but encoder=" +
				(encoder == null ? "null" : encoder.getClass().getSimpleName()));
	}

	public WorldPacketsEncoder getWorldPacketsEncoder() {
		return encoder instanceof WorldPacketsEncoder w ? w : null;
	}

	public WorldPacketsDecoder getWorldPackets() {
		return decoder instanceof WorldPacketsDecoder w ? w : null;
	}

	public ChannelFuture write(OutputStream outStream) {
		if (channel == null || !channel.isConnected() || outStream == null)
			return null;
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(outStream.getBuffer(), 0, outStream.getOffset());
		return writeNow(buffer);
	}

	public ChannelFuture write(ChannelBuffer outStream) {
		if (outStream == null) return null;
		if (channel == null || !channel.isConnected())
			return null;
		return writeNow(outStream);
	}

	public ChannelFuture writeNow(OutputStream outStream) {
		if (channel == null || !channel.isConnected() || outStream == null)
			return null;
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(outStream.getBuffer(), 0, outStream.getOffset());
		return writeNow(buffer);
	}

	public ChannelFuture writeNow(ChannelBuffer outStream) {
		if (outStream == null || channel == null || !channel.isConnected())
			return null;
		synchronized (channel) {
			return channel.write(outStream);
		}
	}

	public Channel getChannel() {
		return channel;
	}

	public Decoder getDecoder() {
		return decoder;
	}

	public Encoder getEncoder() {
		return encoder;
	}

	public String getIP() {
		return channel == null ? "" :
				channel.getRemoteAddress().toString().split(":")[0].replace("/", "");
	}

	public String getLocalAddress() {
		return channel == null ? "" : channel.getLocalAddress().toString();
	}
}
