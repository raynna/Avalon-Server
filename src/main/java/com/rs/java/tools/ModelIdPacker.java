package com.rs.java.tools;

public final class ModelIdPacker {

    private static final long BASE = 0L;

    /**
     * Packs a normal item id into 32-bit unsigned space.
     */
    public static long pack(int itemId) {
        return BASE + ((long) itemId << 16);
    }

    /**
     * Unpacks the packed value back into original item id.
     */
    public static int unpack(long packedValue) {
        return (int) ((packedValue - BASE) >> 16);
    }

    /**
     * Analyze high/low 16-bit sections.
     */
    public static void analyze(long packedValue) {
        long high = packedValue >> 16;
        long low = packedValue & 0xFFFF;

        System.out.println("Packed: " + packedValue);
        System.out.println("High 16 bits: " + high);
        System.out.println("Low 16 bits: " + low);
    }

    public static void main(String[] args) {

        int normalId = 42577;

        long packed = pack(normalId);
        System.out.println("Packed value: " + packed);

        int unpacked = unpack(packed);
        System.out.println("Unpacked value: " + unpacked);
    }
}