package com.rs.java.game.cityhandler;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.rs.java.game.WorldObject;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;

public final class CityEventHandler {

    private static final Map<Integer, CityEvent> cityEvents = new HashMap<Integer, CityEvent>();

    private static final Logger logger = Logger.getLogger(CityEventHandler.class
            .getCanonicalName());

    private static final String PATH = Paths.get(
            System.getProperty("user.dir"),
            "src",
            "com",
            "rs",
            "java",
            "game",
            "cityhandler",
            "impl"
    ).toString();

    public static boolean registerCitys() {

        File directory = new File(PATH);
        if (!directory.exists()) {
            System.out.println("Directory does not exist: " + PATH);
            return false;
        }
        if (!directory.isDirectory()) {
            System.out.println("Provided PATH is not a directory: " + PATH);
            return false;
        }
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No files found in directory: " + PATH);
            return false;
        }

        for (File file : files) {
            try {
                String className = file.getName().replace(".java", "");
                CityEvent event = (CityEvent) Class.forName("com.rs.java.game.cityhandler.impl." + className).newInstance();
                if (!event.init()) {
                    return false;
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[CityEventHandler]: " + files.length + " city plugins were loaded.");
        return true;
    }


    public static boolean reload() throws Throwable {
        cityEvents.clear();
        return registerCitys();
    }

    public static boolean handleNPCClick(Player player, NPC npc, int npcId) {
        CityEvent cityEvent = cityEvents.get(npcId);
        if (cityEvent == null)
            return false;
        return cityEvent.handleNPCClick(player, npc);
    }

    public static boolean handleNPCClick2(Player player, NPC npc, int npcId) {
        CityEvent cityEvent = cityEvents.get(npcId);
        if (cityEvent == null)
            return false;
        return cityEvent.handleNPCClick2(player, npc);
    }

    public static boolean handleNPCClick3(Player player, NPC npc, int npcId) {
        CityEvent cityEvent = cityEvents.get(npcId);
        if (cityEvent == null)
            return false;
        return cityEvent.handleNPCClick3(player, npc);
    }

    public static boolean handleNPCClick4(Player player, NPC npc, int npcId) {
        CityEvent cityEvent = cityEvents.get(npcId);
        if (cityEvent == null)
            return false;
        return cityEvent.handleNPCClick4(player, npc);
    }

    public static boolean handleObjectClick(Player player, WorldObject object, int objectId) {
        CityEvent cityEvent = cityEvents.get(objectId);
        if (cityEvent == null)
            return false;
        return cityEvent.handleObjectClick(player, object);
    }

    public static boolean handleObjectClick2(Player player,
    		WorldObject object, int objectId) {
        CityEvent cityEvent = cityEvents.get(objectId);
        if (cityEvent == null)
            return false;
        return cityEvent.handleObjectClick2(player, object);
    }

    public static boolean handleObjectClick3(Player player,
    		WorldObject object, int objectId) {
        CityEvent cityEvent = cityEvents.get(objectId);
        if (cityEvent == null)
            return false;
        return cityEvent.handleObjectClick3(player, object);
    }

    public static boolean handleObjectClick4(Player player,
    		WorldObject object, int objectId) {
        CityEvent cityEvent = cityEvents.get(objectId);
        if (cityEvent == null)
            return false;
        return cityEvent.handleObjectClick4(player, object);
    }

    public static boolean handleObjectClick5(Player player,
    		WorldObject object, int objectId) {
        CityEvent cityEvent = cityEvents.get(objectId);
        if (cityEvent == null)
            return false;
        return cityEvent.handleObjectClick5(player, object);
    }

    public static boolean registerNPCs(int npcId, CityEvent cityEvent) {
        if (cityEvents.containsKey(npcId)) {
            logger.info("City: " + cityEvent.getClass().getCanonicalName()
                    + " Id => " + npcId+ ", already registered with "
                    + cityEvents.get(npcId).getClass().getCanonicalName() + ".");
            return false;
        }
        cityEvents.put(npcId, cityEvent);
        return true;
    }

    public static boolean registerObjects(int objectId, CityEvent cityEvent) {
        if (cityEvents.containsKey(objectId)) {
            logger.info("City: " + cityEvent.getClass().getCanonicalName()
                    + " Id => " + objectId + " already registered with "
                    + cityEvents.get(objectId).getClass().getCanonicalName()
                    + ".");
            return false;
        }
        cityEvents.put(objectId, cityEvent);
        return true;
    }

	public static boolean handleItemOnObject(Player player, WorldObject object, Item item) {
		 CityEvent cityEvent = cityEvents.get(object.getId());
	        if (cityEvent == null)
	            return false;
	        return cityEvent.handleItemOnObject(player, object, item);
	}
}