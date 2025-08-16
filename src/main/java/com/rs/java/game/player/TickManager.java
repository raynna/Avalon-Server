package com.rs.java.game.player;

import com.rs.java.game.Entity;
import com.rs.java.game.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class TickManager {

    private final Map<Keys, Integer> tickTimers = new HashMap<>();
    private transient Map<Keys, Runnable> tickCallbacks = new HashMap<>();
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

    public void addTicks(Keys key, int ticks, Runnable callback) {
        tickTimers.put(key, ticks);
        if (callback != null) {
            tickCallbacks.put(key, callback);
        }
    }

    public void addTicks(Keys key, int ticks) {
        addTicks(key, ticks, null);
    }

    public void addSeconds(Keys key, int seconds, Runnable callback) {
        int ticks = (int) Math.ceil(seconds / 0.6);
        tickTimers.put(key, ticks);
        if (callback != null) {
            tickCallbacks.put(key, callback);
        }
    }

    public void addSeconds(Keys key, int seconds) {
        addSeconds(key, seconds, null);
    }


    public void addMinutes(Keys key, int minutes, Runnable callback) {
        int ticks = (int) Math.ceil((minutes * 60) / 0.6);
        tickTimers.put(key, ticks);
        if (callback != null) {
            tickCallbacks.put(key, callback);
        }
    }

    public void addMinutes(Keys key, int minutes) {
        addMinutes(key, minutes, null);
    }

    public void tick() {
        Iterator<Map.Entry<Keys, Integer>> it = tickTimers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Keys, Integer> entry = it.next();
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

    public boolean isActive(Keys key) {
        return tickTimers.containsKey(key);
    }

    public int getTicksLeft(Keys key) {
        return tickTimers.getOrDefault(key, 0);
    }

    public enum Keys {

        KEYS_OPENED(1, 0),
        FREEZE_TICKS(2, 0),
        FREEZE_IMMUNE_TICKS(3, 0),
        VENGEANCE_COOLDOWN(4, 0),
        POISON_TICKS(5, 0),
        POISON_SEVERITY(6, 0),
        POISON_IMMUNE_TICKS(7, 0),
        OVERLOAD_TICKS(8, 0),
        RENEWAL_TICKS(9, 0),
        VENGEANCE_TICKS(10, 0),
        LAST_ATTACK_TICK(11, 0),
        STAFF_OF_LIGHT_EFFECT(12, 0),
        FOOD_LOCK_TICK(13, 0),
        SPECIAL_FOOD_LOCK_TICK(14, 0),
        POT_LOCK_TICK(15, 0),

        COAL_STORED(18, 0),


        HIGHEST_ATTACK_LEVEL(20, 0),
        HIGHEST_STRENGTH_LEVEL(21,0),
        HIGHEST_DEFENCE_LEVEL(22, 0),
        HIGHEST_RANGED_LEVEL(23, 0),
        HIGHEST_PRAYER_LEVEL(24, 0),
        HIGHEST_MAGIC_LEVEL(25, 0), DISRUPTION_SHIELD(11, 0), TELEPORT_BLOCK(11, 0), TELEPORT_BLOCK_IMMUNITY(12, 0), KILLCOUNT(30, 0), DEATHCOUNT(31, 0), EP(32, 0), PK_POINTS(33, 0), KILLSTREAK(34, 0), KILLSTREAK_RECORD(35, 0);
        private final int uid;
        private final int defaultValue;

        Keys(int uid, int defaultValue) {
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