package com.rs.java.tools;

public class InventoryRotationTranslator {

    private static final int osrsX = 1510;
    private static final int osrsY = 1785;
    private static final int osrsZ = 821;

    private static int convertInvX(int osrsX, int osrsZ) {
        return (int)(osrsX * 0.6 + osrsZ * 0.12);
    }

    private static int convertInvY(int osrsY, int osrsZ) {
        return (int)(osrsY * 0.9 + osrsZ * 0.14);
    }

    public static void main(String[] args) {
        try {
            System.out.println("TranslatedX: " + convertInvX(osrsX, osrsZ));
            System.out.println("TranslatedY: " + convertInvY(osrsY, osrsZ));

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
