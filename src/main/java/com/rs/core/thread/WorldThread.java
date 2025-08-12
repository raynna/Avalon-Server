package com.rs.core.thread;

import com.rs.Settings;
import com.rs.java.game.World;
import com.rs.java.game.item.ground.AutomaticGroundItem;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public final class WorldThread extends Thread {

	WorldThread() {
		setPriority(Thread.MAX_PRIORITY);
		setName("World Thread");
	}

	public static long LAST_CYCLE_CTM;

	public static long getLastCycleTime() {
		return LAST_CYCLE_CTM;
	}

	@Override
	public void run() {
		while (!CoresManager.shutdown) {
			long cycleStart = Utils.currentTimeMillis();

			try {
				WorldTasksManager.processTasks();
				AutomaticGroundItem.processGameTick();

				List<Player> toCloseChannels = new ArrayList<>();

				for (Player player : World.getPlayers()) {
					if (player == null || !player.hasStarted() || player.hasFinished())
						continue;

					long pingDelay = cycleStart - player.getPacketsDecoderPing();
					if (pingDelay > Settings.MAX_PACKETS_DECODER_PING_DELAY
							&& player.getSession().getChannel().isOpen()) {
						toCloseChannels.add(player);
					}

					player.processEntity();
				}

				for (NPC npc : World.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					npc.processEntity();
				}

				for (Player p : toCloseChannels) {
					try {
						p.getSession().getChannel().close();
					} catch (Exception e) {
						Logger.handle(e);
					}
				}

			} catch (Throwable e) {
				Logger.handle(e);
			}

			try {
				for (Player player : World.getPlayers()) {
					if (player == null || !player.hasStarted() || player.hasFinished())
						continue;

					player.getPackets().sendLocalPlayersUpdate();
					player.getPackets().sendLocalNPCsUpdate();
					player.resetMasks();
				}

				for (NPC npc : World.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					npc.resetMasks();
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}

			LAST_CYCLE_CTM = Utils.currentTimeMillis();

			long elapsed = LAST_CYCLE_CTM - cycleStart;
			long sleepTime = Settings.WORLD_CYCLE_TIME - elapsed;
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					Logger.handle(e);
				}
			}
		}
	}
}
