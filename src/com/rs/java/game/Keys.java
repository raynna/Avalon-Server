package com.rs.java.game;

public class Keys {

    public enum IntKey {

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

        COAL_STORED(15, 0),


        HIGHEST_ATTACK_LEVEL(20, 0),
        HIGHEST_STRENGTH_LEVEL(21,0),
        HIGHEST_DEFENCE_LEVEL(22, 0),
        HIGHEST_RANGED_LEVEL(23, 0),
        HIGHEST_PRAYER_LEVEL(24, 0),
        HIGHEST_MAGIC_LEVEL(25, 0), DISRUPTION_SHIELD(11, 0), TELEPORT_BLOCK(11, 0), TELEPORT_BLOCK_IMMUNITY(12, 0), KILLCOUNT(30, 0), DEATHCOUNT(31, 0), EP(32, 0), PK_POINTS(33, 0), KILLSTREAK(34, 0), KILLSTREAK_RECORD(35, 0);
        private final int uid;
        private final int defaultValue;

        IntKey(int uid, int defaultValue) {
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

    public enum BooleanKey {

        BANK_PIN(1, false),

        DISRUPTION_ACTIVE(2, false), VENGEANCE_ACTIVE(3, false), TALKED_TO_MARV(4, false), TALKED_TO_ESTOCADA(5, false), TALKED_TO_GILES(6, false), TALKED_TO_MR_EX(7, false);
        private final int uid;
        private final boolean defaultValue;

        BooleanKey(int uid, boolean defaultValue) {
            this.uid = uid;
            this.defaultValue = defaultValue;
        }

        public int getUID() {
            return uid;
        }

        public boolean getDefaultValue() {
            return defaultValue;
        }
    }

    public enum LongKey {

        OVERLOAD_EFFECT(1, -1),
        PRAYER_RENEWAL_EFFECT(2, -1),
        HIGHEST_VALUE_DROP(20, 0),

        ;
        private final int uid;
        private final long defaultValue;

        LongKey(int uid, long defaultValue) {
            this.uid = uid;
            this.defaultValue = defaultValue;
        }

        public int getUID() {
            return uid;
        }

        public long getDefaultValue() {
            return defaultValue;
        }
    }

    public enum StringKey {

        EXAMPLE_STRING(1, ""),

        ;
        private final int uid;
        private final String defaultValue;

        StringKey(int uid, String defaultValue) {
            this.uid = uid;
            this.defaultValue = defaultValue;
        }

        public int getUID() {
            return uid;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }
}
