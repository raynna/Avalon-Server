package com.rs.core.thread;

import java.util.ArrayList;
import java.util.List;

import com.rs.Settings;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.game.World;
import com.rs.java.game.item.ground.AutomaticGroundItem;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.bot.PlayerBotManager;
import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.player.grandexchange.GrandExchange;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.GlobalScope;

public final class WorldThread extends Thread {
    private static final long SLOW_TICK_LOG_MS = 120L;
    private static final int SLOW_TICK_LOG_COOLDOWN = 25;
    private static int lastSlowTickLog = -SLOW_TICK_LOG_COOLDOWN;

    WorldThread() {
        setPriority(Thread.MAX_PRIORITY);
        setName("World Thread");
    }

    public static int WORLD_TICK = 0;

    public static long LAST_CYCLE_CTM;

    public static long getLastCycleTime() {
        return LAST_CYCLE_CTM;
    }

    public static long getCycleIndex() {
        return LAST_CYCLE_CTM / Settings.WORLD_CYCLE_TIME;
    }

    @Override
    public void run() {
        while (!CoresManager.shutdown) {
            long cycleStart = Utils.currentTimeMillis();
            long phaseStart = System.currentTimeMillis();
            WorldTasksManager.processTasks();
            long worldTasksMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
            try {
                BuildersKt.runBlocking(
                        GlobalScope.INSTANCE.getCoroutineContext(),
                        (scope, cont) -> com.rs.kotlin.game.world.task.WorldTasksHandler.INSTANCE.processTick(cont)
                );
            } catch (InterruptedException e) {
                Logger.handle(e);
            }
            long coroutineTasksMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
            GrandExchange.INSTANCE.processTickMatches();
            long grandExchangeMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
            AutomaticGroundItem.processGameTick();
            long groundItemsMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
            for (Player player : World.getPlayers()) {
                try {
                    if (player == null || !player.hasStarted() || player.hasFinished())
                        continue;
                    player.processLogicPackets();
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }
            long logicPacketsMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
            for (NPC npc : World.getNPCs()) {
                try {
                    if (npc == null || npc.hasFinished())
                        continue;
                    npc.processEntity();
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }
            long npcProcessMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
            for (Player player : World.getPlayers()) {
                try {
                    if (player == null || !player.hasStarted() || player.hasFinished())
                        continue;
                    player.processEntity();
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }
            long playerProcessMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
            PlayerBotManager.processTick();
            long botProcessMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
            for (Player player : World.getPlayers()) {
                try {
                    if (player == null || !player.hasStarted() || player.hasFinished())
                        continue;
                    player.processProjectiles();
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }
            long projectileMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
            for (Player player : World.getPlayers()) {
                try {
                    if (player == null || !player.hasStarted() || player.hasFinished())
                        continue;

                    player.getPackets().sendLocalPlayersUpdate();
                    player.getPackets().sendLocalNPCsUpdate();
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }
            long localUpdateMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
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
            long resetMasksMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
            List<Player> toCloseChannels = new ArrayList<>();
            for (Player player : World.getPlayers()) {
                if (player == null || !player.hasStarted() || player.hasFinished())
                    continue;

                //player.processLogicPackets();

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
            }
            for (Player p : toCloseChannels) {
                try {
                    if (p.getSession() != null && p.getSession().getChannel() != null) {
                        p.getSession().getChannel().close();
                        System.out.println("Player loggged out due to ping timeout.");
                    }
                } catch (Exception e) {
                    Logger.handle(e);
                }
            }
            long pingSweepMs = System.currentTimeMillis() - phaseStart;
            LAST_CYCLE_CTM = Utils.currentTimeMillis();
            WORLD_TICK++;
            long elapsed = LAST_CYCLE_CTM - cycleStart;
            if (elapsed >= SLOW_TICK_LOG_MS && WORLD_TICK - lastSlowTickLog >= SLOW_TICK_LOG_COOLDOWN) {
                lastSlowTickLog = WORLD_TICK;
                System.out.println("[WorldThread SLOW] tick=" + WORLD_TICK
                        + " total=" + elapsed + "ms"
                        + " players=" + World.getPlayers().size()
                        + " npcs=" + World.getNPCs().size()
                        + " bots=" + PlayerBotManager.getPlayers().size()
                        + " tasks=" + worldTasksMs
                        + " coroutines=" + coroutineTasksMs
                        + " ge=" + grandExchangeMs
                        + " ground=" + groundItemsMs
                        + " logic=" + logicPacketsMs
                        + " npcProc=" + npcProcessMs
                        + " playerProc=" + playerProcessMs
                        + " botProc=" + botProcessMs
                        + " projectiles=" + projectileMs
                        + " localUpd=" + localUpdateMs
                        + " masks=" + resetMasksMs
                        + " ping=" + pingSweepMs);
            }
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
