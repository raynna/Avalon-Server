package com.rs.java.game.player;

import com.rs.java.game.Entity;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.kotlin.game.player.interfaces.TimerOverlay;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;


public class TickManager {

    private final Map<TickKeys, Integer> tickTimers = new HashMap<>();
    private transient Map<TickKeys, Runnable> tickCallbacks = new HashMap<>();
    private transient Entity entity;
    private String entityName;


    public void init() {
        this.tickCallbacks = new HashMap<>();
    }

    public TickManager(Entity entity) {
        setEntity(entity);
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        if (this.entity instanceof Player player) {
            this.entityName = player.getUsername();
            this.entity = player;
        }
    }

    /**
     * Called when a timer expires. Pass the entity so the subclass can act on it
     */

    private static final Map<TickKeys, TimerOverlay.TimerType> TICK_TO_OVERLAY = Map.ofEntries(
            Map.entry(TickKeys.OVERLOAD_TICKS, TimerOverlay.TimerType.OVERLOAD),
            Map.entry(TickKeys.PRAYER_RENEWAL_TICKS, TimerOverlay.TimerType.RENEWAL),
            Map.entry(TickKeys.ANTI_FIRE_TICKS, TimerOverlay.TimerType.ANTIFIRE),
            Map.entry(TickKeys.SUPER_ANTI_FIRE_TICKS, TimerOverlay.TimerType.SUPER_ANTIFIRE),
            Map.entry(TickKeys.TELEPORT_BLOCK, TimerOverlay.TimerType.TELEBLOCK),
            Map.entry(TickKeys.FREEZE_TICKS, TimerOverlay.TimerType.FREEZE),
            Map.entry(TickKeys.VENGEANCE_COOLDOWN, TimerOverlay.TimerType.VENGEANCE),
            Map.entry(TickKeys.POISON_IMMUNE_TICKS, TimerOverlay.TimerType.ANTIPOISON),
            Map.entry(TickKeys.CHARGE_SPELL, TimerOverlay.TimerType.CHARGE),
            Map.entry(TickKeys.MIASMIC_EFFECT, TimerOverlay.TimerType.MIASMIC),
            Map.entry(TickKeys.ENTITY_LOCK_TICK, TimerOverlay.TimerType.LOCKED)
    );

    private void syncOverlay(TickKeys key, int ticks) {
        if (entity instanceof Player player) {
            TimerOverlay.TimerType type = TICK_TO_OVERLAY.get(key);
            if (type != null) {
                player.timerOverlay.startTimer(player, type, ticks, false);
            }
        }
    }

    public void addTicks(TickKeys key, int ticks, Runnable callback) {
        tickTimers.put(key, ticks);
        if (callback != null) {
            tickCallbacks.put(key, callback);
        }
        syncOverlay(key, ticks);
    }

    public void rebuildOverlay() {
        for (Map.Entry<TickKeys, Integer> e : tickTimers.entrySet()) {
            int ticks = e.getValue();
            if (ticks > 0) {
                syncOverlay(e.getKey(), ticks);
            }
        }
    }

    public void addTicks(TickKeys key, int ticks) {
        addTicks(key, ticks, null);
    }

    public void addSeconds(TickKeys key, int seconds, Runnable callback) {
        int ticks = (int) Math.ceil(seconds / 0.6);
        tickTimers.put(key, ticks);
        if (callback != null) {
            tickCallbacks.put(key, callback);
        }
        syncOverlay(key, ticks);
    }

    public void addSeconds(TickKeys key, int seconds) {
        addSeconds(key, seconds, null);
    }


    public void addMinutes(TickKeys key, int minutes, Runnable callback) {
        int ticks = (int) Math.ceil((minutes * 60) / 0.6);
        tickTimers.put(key, ticks);
        if (callback != null) {
            tickCallbacks.put(key, callback);
        }
        syncOverlay(key, ticks);
    }

    public void addMinutes(TickKeys key, int minutes) {
        addMinutes(key, minutes, null);
    }

    public static final Map<TimerOverlay.TimerType, TickKeys> OVERLAY_TO_TICK =
            TICK_TO_OVERLAY.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));


    public void tick() {
        Iterator<Map.Entry<TickKeys, Integer>> it = tickTimers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<TickKeys, Integer> entry = it.next();
            int ticksLeft = entry.getValue() - 1;
            if (ticksLeft >= 1000000)//avoid infinite timers to run
                it.remove();
            if (ticksLeft <= 0) {
                it.remove();

                Runnable callback = tickCallbacks.remove(entry.getKey());
                if (callback != null) {
                    callback.run();
                }

            } else {
                entry.setValue(ticksLeft);
            }
        }
    }

    /**
     * Removes a tick timer and its associated callback.
     */
    public void remove(TickKeys key) {
        tickTimers.remove(key);
        tickCallbacks.remove(key);
    }

    /**
     * Resets a tick timer to its default value.
     * If the timer doesn't exist, it will be initialized with the default value.
     */
    public void reset() {
        tickTimers.clear();
        tickCallbacks.clear();
    }

    public boolean isActive(TickKeys key) {
        return tickTimers.getOrDefault(key, 0) > 0;
    }

    public int getTicksLeft(TickKeys key) {
        return tickTimers.getOrDefault(key, 0);
    }

    public int getSecondsLeft(TickKeys key) {
        int ticks = getTicksLeft(key);
        return (int) Math.ceil(ticks * 0.6);
    }

    public String getTimeLeft(TickKeys key) {
        int totalSeconds = getSecondsLeft(key);

        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public enum TickKeys {

        /**
         * Entity timers
         */

        ENTITY_LOCK_TICK(1, 0),
        FREEZE_TICKS(2, 0),
        FREEZE_IMMUNE_TICKS(3, 0),
        VENGEANCE_COOLDOWN(4, 0),
        POISON_TICKS(5, 0),
        POISON_SEVERITY(6, 0),
        PJ_TIMER(7, 0),
        FLINCH_TICKS(8, 0),
        LAST_INTERACTION_TARGET(9, 0),
        VENGEANCE_TICKS(10, 0),
        LAST_ATTACK_TICK(11, 0),
        LAST_ATTACKED_TICK(12, 0),
        STAFF_OF_LIGHT_EFFECT(13, 0),
        FOOD_LOCK_TICK(14, 0),
        SPECIAL_FOOD_LOCK_TICK(15, 0),
        POT_LOCK_TICK(16, 0),
        DISABLED_PROTECTION_PRAYER_TICK(17, 0),
        TELEPORTING_TICK(18, 0),
        CHARGE_SPELL(19, 0),
        MIASMIC_EFFECT(20, 0),
        GRANITE_MAUL_TIMER(21, 0),


        /**
         * Potion timers
         */

        OVERLOAD_TICKS(50, 0),
        RENEWAL_TICKS(51, 0),
        POISON_IMMUNE_TICKS(52, 0),
        ANTI_FIRE_TICKS(53, 0),
        SUPER_ANTI_FIRE_TICKS(54, 0),
        PRAYER_RENEWAL_TICKS(55, 0),

        DISRUPTION_SHIELD(11, 0),
        TELEPORT_BLOCK(11, 0),
        TELEPORT_BLOCK_IMMUNITY(12, 0);
        private final int uid;
        private final int defaultValue;

        TickKeys(int uid, int defaultValue) {
            this.uid = uid;
            this.defaultValue = defaultValue;
        }

        public int getUID() {
            return uid;
        }

        public int getDefaultValue() {
            return defaultValue;
        }
    }
}