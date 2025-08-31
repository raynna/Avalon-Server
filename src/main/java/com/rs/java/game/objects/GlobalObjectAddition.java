package com.rs.java.game.objects;

import java.util.ArrayList;

import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;

public class GlobalObjectAddition {

    private static ArrayList<WorldObject> objects = new ArrayList<WorldObject>();

    public static void init() {
        try {
            readToStoreCollection();
        } catch (Exception ignored) {
        }
    }

    private static void readToStoreCollection() throws Exception {

        objects.add(new WorldObject(37548, 4, 1, 3082, 3513, 0));

        /**
         * Carpet in edgeville bank
         */
        objects.add(new WorldObject(39639, 22, 2, 3092, 3488, 0));

        /**
         * Border in Edgeville Bank
        */
        objects.add(new WorldObject(24397, 0, 2, 3095, 3489, 0));
        objects.add(new WorldObject(24397, 0, 2, 3095, 3490, 0));
        objects.add(new WorldObject(24397, 0, 2, 3095, 3491, 0));
        objects.add(new WorldObject(24397, 0, 2, 3095, 3492, 0));
        objects.add(new WorldObject(24397, 0, 2, 3095, 3493, 0));
        objects.add(new WorldObject(24397, 0, 2, 3095, 3494, 0));
        objects.add(new WorldObject(24397, 0, 3, 3098, 3495, 0));
        objects.add(new WorldObject(24397, 0, 3, 3097, 3495, 0));
        objects.add(new WorldObject(24397, 0, 3, 3096, 3495, 0));

    }

    public static ArrayList<WorldObject> getObjects() {
        return objects;
    }

    public static void reloadObjects() {
        try {
            objects.clear();
            readToStoreCollection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean removableObjects(WorldTile tile) {
        for (int i = 0; i < objects.size(); i++) {
            if (tile == objects.get(i)) {
                return true;
            }
        }
        return false;
    }

}
