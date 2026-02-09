package com.rs.java.tools;

public final class HexToDecimal {

    /**
     * Converts a hex string (e.g. "00a9", "FF", "1c4") to decimal.
     */
    public static int convert(String hex) {
        hex = hex.trim().toUpperCase();

        int result = 0;
        int power = 1; // 16^0

        for (int i = hex.length() - 1; i >= 0; i--) {
            char c = hex.charAt(i);

            int value;
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = 10 + (c - 'A');
            } else {
                throw new IllegalArgumentException("Invalid hex character: " + c);
            }

            result += value * power;
            power *= 16;
        }

        return result;
    }

    // Example usage
    public static void main(String[] args) {
        System.out.println(convert("00a9")); // 169
        System.out.println(convert("00ff")); // 255
        System.out.println(convert("01c4")); // 452
    }
}
