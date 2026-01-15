package com.rs.core.thread;

import java.util.ArrayList;
import java.util.List;

import com.rs.Settings;
import com.rs.core.networking.Session;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.game.World;
import com.rs.java.game.item.ground.AutomaticGroundItem;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;

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
                for (Session s : Session.ACTIVE_SESSIONS) {
                    if (s != null)
                        s.processQueuedPacketsTick();
                }
                WorldTasksManager.processTasks();
                AutomaticGroundItem.processGameTick();

                List<Player> toCloseChannels = new ArrayList<>();

                for (Player player : World.getPlayers()) {
                    if (player == null || !player.hasStarted() || player.hasFinished())
                        continue;

                    long lastPing = player.getPacketsDecoderPing();

                    if (lastPing != 0) {
                        long pingDelay = cycleStart - lastPing;
                        if (pingDelay > Settings.MAX_PACKETS_DECODER_PING_DELAY
                                && player.getSession() != null
                                && player.getSession().getChannel() != null
                                && player.getSession().getChannel().isOpen()) {
                            toCloseChannels.add(player);
                        }
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
                        if (p.getSession() != null && p.getSession().getChannel() != null)
                            p.getSession().getChannel().close();
                    } catch (Exception e) {
                        Logger.handle(e);
                    }
                }

            } catch (Throwable e) {
                Logger.handle(e);
            }

            for (Player player : World.getPlayers()) {
                if (player == null || !player.hasStarted() || player.hasFinished())
                    continue;
                player.processProjectiles();
            }

            try {
                for (Player player : World.getPlayers()) {
                    if (player == null || !player.hasStarted() || player.hasFinished())
                        continue;

                    player.getPackets().sendLocalPlayersUpdate();
                    player.getPackets().sendLocalNPCsUpdate();
                }

                for (Player player : World.getPlayers()) {
                    if (player == null || !player.hasStarted() || player.hasFinished())
                        continue;
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
