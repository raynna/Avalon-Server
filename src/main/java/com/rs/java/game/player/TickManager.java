package com.rs.java.game.player;

import com.rs.java.game.Entity;
import com.rs.java.game.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class TickManager {

    private final Map<TickKeys, Integer> tickTimers = new HashMap<>();
    private transient Map<TickKeys, Runnable> tickCallbacks = new HashMap<>();
    private transient Entity entity;
    private String entityName;


    public void init() {
        this.tickCallbacks = new HashMap<>();
        this.entity = World.getPlayer(this.entityName);
    }

    public TickManager(Entity entity) {
        setEntity(entity);
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        if (this.entity instanceof Player) {
            this.entity = World.getPlayer(this.entityName);
        }
    }

    /**
     * Called when a timer expires. Pass the entity so the subclass can act on it
     */

    public void addTicks(TickKeys key, int ticks, Runnable callback) {
        tickTimers.put(key, ticks);
        if (callback != null) {
            tickCallbacks.put(key, callback);
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
    }

    public void addMinutes(TickKeys key, int minutes) {
        addMinutes(key, minutes, null);
    }

    public void tick() {
        Iterator<Map.Entry<TickKeys, Integer>> it = tickTimers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<TickKeys, Integer> entry = it.next();
            int ticksLeft = entry.getValue() - 1;
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
        return tickTimers.containsKey(key);
    }

    public int getTicksLeft(TickKeys key) {
        return tickTimers.getOrDefault(key, 0);
    }

    public enum TickKeys {

        /**
         * Entity timers
         */

        ENTITY_LOCK_TICK(0, 0),
        FREEZE_TICKS(2, 0),
        FREEZE_IMMUNE_TICKS(3, 0),
        VENGEANCE_COOLDOWN(4, 0),
        POISON_TICKS(5, 0),
        POISON_SEVERITY(6, 0),
        VENGEANCE_TICKS(10, 0),
        LAST_ATTACK_TICK(11, 0),
        LAST_ATTACKED_TICK(12, 0),
        STAFF_OF_LIGHT_EFFECT(13, 0),
        FOOD_LOCK_TICK(14, 0),
        SPECIAL_FOOD_LOCK_TICK(15, 0),
        POT_LOCK_TICK(16, 0),
        DISABLED_PROTECTION_PRAYER_TICK(17, 0),
        TELEPORTING_TICK(18, 0),


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