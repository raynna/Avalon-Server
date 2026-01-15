package com.rs.core.networking;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.rs.Settings;
import com.rs.core.thread.CoresManager;
import com.rs.java.utils.Logger;

public final class ServerChannelHandler extends SimpleChannelHandler {

	private static ChannelGroup channels;
	private static ServerBootstrap bootstrap;

	public static void init() {
		new ServerChannelHandler();
	}

	public static int getConnectedChannelsSize() {
		return channels == null ? 0 : channels.size();
	}

	private ServerChannelHandler() {
		channels = new DefaultChannelGroup("server-channels");

		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				CoresManager.serverBossChannelExecutor,
				CoresManager.serverWorkerChannelExecutor,
				CoresManager.serverWorkersCount
		));

		bootstrap.getPipeline().addLast("handler", this);

		bootstrap.setOption("reuseAddress", true);
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);

		bootstrap.bind(new InetSocketAddress(Settings.PORT_ID));
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
		channels.add(e.getChannel());
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
		channels.remove(e.getChannel());
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Logger.log("NET", "Channel connected: " + e.getChannel());
		Session session = new Session(e.getChannel());
		ctx.setAttachment(session);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Logger.log("NET", "Channel disconnected: " + e.getChannel());

		Object attachment = ctx.getAttachment();
		if (attachment instanceof Session session) {
			Logger.log("NET", "Session closed for IP=" + session.getIP());
			try {
				if (session.getWorldPacketsEncoder() != null &&
						session.getWorldPacketsEncoder().getPlayer() != null) {
					Logger.log("NET", "Finishing player: " +
							session.getWorldPacketsEncoder().getPlayer().getUsername());
					session.getWorldPacketsEncoder().getPlayer().finish();
				}
			} catch (Throwable t) {
				Logger.handle(t);
			}
			session.close();
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		if (!(e.getMessage() instanceof ChannelBuffer buf))
			return;

		Object attachment = ctx.getAttachment();
		if (!(attachment instanceof Session session))
			return;

		int avail = buf.readableBytes();

		if (avail < 1 || avail > Settings.RECEIVE_DATA_LIMIT)
			return;

		byte[] data = new byte[avail];
		buf.readBytes(data);

		session.enqueueIncoming(data);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent ee) {
		try {
			ctx.getChannel().close();
		} catch (Throwable ignored) {}
	}

	public static void shutdown() {
		if (channels != null)
			channels.close().awaitUninterruptibly();
		if (bootstrap != null)
			bootstrap.releaseExternalResources();
	}
}
