package raynna.core.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import raynna.app.Settings;
import raynna.core.tasks.WorldTasksManager;
import raynna.game.World;
import raynna.game.item.ground.AutomaticGroundItem;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.game.player.bot.PlayerBotManager;
import raynna.util.Logger;
import raynna.util.Utils;
import raynna.game.player.grandexchange.GrandExchange;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.GlobalScope;

public final class WorldThread extends Thread {
    private static final long SLOW_TICK_LOG_MS = 120L;
    private static final int SLOW_TICK_LOG_COOLDOWN = 25;
    private static int lastSlowTickLog = -SLOW_TICK_LOG_COOLDOWN;
    private static final int TOP_SLOW_ENTRIES = 3;

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

    private static void recordTop(List<SlowEntry> entries, String label, long nanos) {
        if (label == null || nanos <= 0L) {
            return;
        }
        entries.add(new SlowEntry(label, nanos));
        entries.sort((a, b) -> Long.compare(b.nanos, a.nanos));
        if (entries.size() > TOP_SLOW_ENTRIES) {
            entries.remove(entries.size() - 1);
        }
    }

    private static String summarizeTop(String phase, List<SlowEntry> entries) {
        if (entries.isEmpty()) {
            return phase + "=none";
        }
        StringBuilder builder = new StringBuilder(phase).append('=');
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            SlowEntry entry = entries.get(i);
            builder.append(entry.label)
                    .append(':')
                    .append(TimeUnit.NANOSECONDS.toMillis(entry.nanos))
                    .append("ms");
        }
        return builder.toString();
    }

    @Override
    public void run() {
        while (!CoresManager.shutdown) {
            long cycleStart = Utils.currentTimeMillis();
            List<SlowEntry> queuedPacketEntries = new ArrayList<>();
            List<SlowEntry> npcProcessEntries = new ArrayList<>();
            List<SlowEntry> playerProcessEntries = new ArrayList<>();
            List<SlowEntry> localUpdateEntries = new ArrayList<>();
            long phaseStart = System.currentTimeMillis();
            WorldTasksManager.processTasks();
            long worldTasksMs = System.currentTimeMillis() - phaseStart;

            phaseStart = System.currentTimeMillis();
            try {
                BuildersKt.runBlocking(
                        GlobalScope.INSTANCE.getCoroutineContext(),
                        (scope, cont) -> raynna.game.world.task.WorldTasksHandler.INSTANCE.processTick(cont)
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
                    long start = System.nanoTime();
                    player.processQueuedWorldPackets();
                    recordTop(queuedPacketEntries, player.getDisplayName(), System.nanoTime() - start);
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
                    long start = System.nanoTime();
                    npc.processEntity();
                    recordTop(npcProcessEntries, npc.getName() + "#" + npc.getId(), System.nanoTime() - start);
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
                    long start = System.nanoTime();
                    player.processEntity();
                    recordTop(playerProcessEntries, player.getDisplayName(), System.nanoTime() - start);
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }
            long playerProcessMs = System.currentTimeMillis() - phaseStart;
            phaseStart = System.currentTimeMillis();
            PlayerBotManager.processTick();
            long botProcessMs = System.currentTimeMillis() - phaseStart;
            String botProfileSummary = PlayerBotManager.consumeLastProfileSummary();

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

                    long start = System.nanoTime();
                    player.getPackets().sendLocalPlayersUpdate();
                    player.getPackets().sendLocalNPCsUpdate();
                    recordTop(localUpdateEntries, player.getDisplayName(), System.nanoTime() - start);
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
                System.out.println("[WorldThread HOT] "
                        + summarizeTop("logicTop", queuedPacketEntries)
                        + " | " + summarizeTop("npcTop", npcProcessEntries)
                        + " | " + summarizeTop("playerTop", playerProcessEntries)
                        + " | " + summarizeTop("localTop", localUpdateEntries));
                if (botProfileSummary != null && !botProfileSummary.isBlank()) {
                    System.out.println("[WorldThread BOT] " + botProfileSummary);
                }
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

    private static final class SlowEntry {
        private final String label;
        private final long nanos;

        private SlowEntry(String label, long nanos) {
            this.label = label;
            this.nanos = nanos;
        }
    }
}
