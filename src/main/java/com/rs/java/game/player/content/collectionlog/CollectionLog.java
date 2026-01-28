package com.rs.java.game.player.content.collectionlog;

import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemsContainer;
import com.rs.java.game.player.Player;
import com.rs.java.utils.HexColours;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionLog implements Serializable {
    private static final long serialVersionUID = 4602692016264458065L;

    public static final int ID = 3004;

    public static CollectionLog MASTER = new CollectionLog();

    public static void init() {
    	CollectionLogBuilder.build(MASTER);
    }

    private static final int CAT_SPRITE_START = 19, TAB_BTN_START = 51,
                            BOSS_NAME_STRING = 40, OBTAINED_STRING = 41, KILLS_STRING = 42;

    private static final int MAX_TABS = 53;

    private static final int COLLECTED_ITEM_CONTAINER = 213;
    private static final int GHOST_ITEM_CONTAINER = 45;
    public static final int ITEM_CONTAINER_KEY = 1000;
    public static final int GHOST_CONTAINER_KEY = 1001;
    public static final int ITEM_WIDTH = 6, ITEM_HEIGHT = 20;

    private transient int tabId = 0;
    private transient CategoryType category = CategoryType.BOSSES;

    private transient ArrayList<String> tabs = new ArrayList<String>();

    private LogCategory bosses, clues, minigames, others, slayers;

    public LogCategory getBosses() { return bosses; }

    public LogCategory getClues() { return clues; }

    public LogCategory getMinigames() { return minigames; }

    public LogCategory getOthers() { return others; }

    public LogCategory getSlayers() { return slayers; }

    transient Player player = null;

    public CollectionLog() {
        bosses = new LogCategory(CategoryType.BOSSES);
        clues = new LogCategory(CategoryType.CLUES);
        minigames = new LogCategory(CategoryType.MINIGAMES);
        others = new LogCategory(CategoryType.OTHERS);
        slayers = new LogCategory(CategoryType.SLAYER);
    }

    public void init(Player player) {
        this.player = player;
        this.category = CategoryType.BOSSES;
        tabs = new ArrayList<>();
        CollectionLog.MASTER.player = player;
    }

    public void open() {
        sendComponentOps();
        writeCategory();
        player.getInterfaceManager().sendInterface(ID);
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                player.getPackets().sendRunScript(6255);
            }
        });
        player.setCloseInterfacesEvent(() -> {
            tabId = 0;
            category = CategoryType.BOSSES;
        });
    }



    public void buttonClick(int componentId) {
        CategoryType previousCategory = category;
        int previousTab = tabId;

        if(componentId > TAB_BTN_START && componentId < TAB_BTN_START + MAX_TABS*3) {
            if((componentId + 1 - TAB_BTN_START) % 3 == 0) {
                tabId = (componentId - TAB_BTN_START) / 3;
                if(tabId != previousTab)
                    writeCategory();
                return;
            }
        }

        switch(componentId) {
            case 18:
                category = CategoryType.BOSSES;
                break;
            case 21:
                category = CategoryType.SLAYER;
                break;
            case 24:
                category = CategoryType.CLUES;
                break;
            case 27:
                category = CategoryType.MINIGAMES;
                break;
            case 30:
                category = CategoryType.OTHERS;
                break;
        }

        if(previousCategory != category) {
            tabId = 0;
            writeCategory();
        }
    }

    private void writeCategory() {
        if (MASTER.getCategory(category).getDrops().isEmpty()) {
            player.message("Collection log data not loaded.");
            return;
        }
        writeTabs();
        writeGhostItems();
        writeCollectedItems();
        writeDetails();

        for(int i = 0; i < CategoryType.values().length; i++) {
            player.getPackets().sendIComponentSprite(ID, CAT_SPRITE_START + i*3, category == CategoryType.values()[i] ? 952 : 953);
        }
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                player.getPackets().sendRunScript(6255);
            }
        });
    }

    private void writeDetails() {
        if (tabs.isEmpty())
            return;
        String key = tabs.get(tabId);
        String kills  = getKills(key);
        player.getPackets().sendTextOnComponent(ID, BOSS_NAME_STRING, key);
        player.getPackets().sendTextOnComponent(ID, KILLS_STRING, kills == null ? "" : getKillString(key, kills));
        player.getPackets().sendTextOnComponent(ID, OBTAINED_STRING, getCompletion(category, key));
        player.getPackets().sendTextOnComponent(ID, BOSS_NAME_STRING, tabs.get(tabId));
    }

    /*
     * null hides string
     */
    private String getKills(String key) {
        Integer lookup;

        switch(category) {
            case BOSSES:
                lookup = player.getBossKillcount().get(key.toLowerCase());
                return "" + (lookup == null ? 0 : lookup);
            case SLAYER:
                switch(key) {
                    case "Theatre of Blood":
                        lookup = player.getBossKillcount().get("Theatre of Blood".toLowerCase());
                        return "" + (lookup == null ? 0 : lookup);
                }
                break;
            case CLUES:
                switch(key) {
                }
                break;
            case MINIGAMES:
                switch(key) {
                    case "Barrows":
                        lookup = player.getBossKillcount().get("Barrows Chests".toLowerCase());
                        return "" + (lookup == null ? 0 : lookup);

                }
                break;
            //case OTHERS:
            //  return null;
		default:
			break;
        }

        return null;
    }

    public String getCompletion(CategoryType category, String key) {
        Map<Integer, Integer> lootTab = getCategory(category).obtainedDrops.get(key);
        Map<Integer, Integer> masterTab = MASTER.getCategory(category).obtainedDrops.get(key);

        int completion = lootTab == null ? 0 : lootTab.size();

        String prefix = "";
        if(completion == masterTab.size())
            // completed tab = numbers green
            prefix = "<col=00ff00>";
        else if (completion == 0)
        	 prefix = "<col=FF0000>";
        else
        	prefix = "<col=FFFF00>";

        return "Obtained: " + prefix + completion + " / " + masterTab.size();
    }

    /**
     * Checks if a type and key has been complete or not
     * @param type
     * @param key
     * @return
     */
    private boolean hasComplete(CategoryType type, String key) {
        Map<Integer, Integer> lootTab = getCategory(category).obtainedDrops.get(key);
        Map<Integer, Integer> masterTab = MASTER.getCategory(category).obtainedDrops.get(key);
        int completion = lootTab == null ? 0 : lootTab.size();

        if (masterTab == null) return false;

        if(completion == masterTab.size()) {
            return true;
        }

        return false;
    }


    private void sendComponentOps() {

        player.getPackets().sendUnlockOptions(
                ID, GHOST_ITEM_CONTAINER, 0, ITEM_WIDTH * ITEM_HEIGHT - 1, 0, 1
        );

        player.getPackets().sendInterSetItemsOptionsScript(
                ID, GHOST_ITEM_CONTAINER,
                GHOST_CONTAINER_KEY, ITEM_WIDTH, ITEM_HEIGHT,
                "Examine"
        );

        player.getPackets().sendUnlockOptions(
                ID, COLLECTED_ITEM_CONTAINER, 0, ITEM_WIDTH * ITEM_HEIGHT - 1, 0);

        player.getPackets().sendInterSetItemsOptionsScript(
                ID, COLLECTED_ITEM_CONTAINER,
                ITEM_CONTAINER_KEY, ITEM_WIDTH, ITEM_HEIGHT);
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                player.getPackets().sendRunScript(6255);
            }
        });
    }


    private void writeGhostItems() {

        ItemsContainer<Item> masterLog =
                MASTER.getCategory(category)
                        .getCollectionList(tabs.get(tabId));

        ItemsContainer<Item> ghosts =
                new ItemsContainer<>(ITEM_WIDTH * ITEM_HEIGHT, false);

        Item[] masterItems = masterLog.getItemsCopy();

        for (int i = 0; i < masterItems.length; i++) {
            Item master = masterItems[i];
            if (master != null) {
                ghosts.set(i, new Item(master.getId(), 1));
            }
        }
        player.getPackets().sendItems(GHOST_CONTAINER_KEY, ghosts);
    }



    private void writeCollectedItems() {
        ItemsContainer<Item> masterLog =
                MASTER.getCategory(category)
                        .getCollectionList(tabs.get(tabId));

        ItemsContainer<Item> log =
                getCategory(category)
                        .getCollectionList(tabs.get(tabId));

        ItemsContainer<Item> real =
                new ItemsContainer<>(ITEM_WIDTH * ITEM_HEIGHT, false);

        Item[] masterItems = masterLog.getItemsCopy();
        Item[] playerItems = log.getItemsCopy();

        for (int i = 0; i < masterItems.length; i++) {
            Item master = masterItems[i];
            if (master == null)
                continue;

            for (Item it : playerItems) {
                if (it != null && it.getId() == master.getId()) {
                    real.set(i, new Item(it));
                    break;
                }
            }
        }

        player.getPackets().sendItems(ITEM_CONTAINER_KEY, real);
    }



    public LogCategory getCategory(CategoryType type) {
        switch(type) {
            default:
            case BOSSES:
                return getBosses();
            case CLUES:
                return getClues();
            case MINIGAMES:
                return getMinigames();
            case OTHERS:
                return getOthers();
            case SLAYER:
                return getSlayers();
        }
    }

    private void writeTabs() {
        tabs.clear();

        // get tab names to display
        for(String s : MASTER.getCategory(category).obtainedDrops.keySet())
            if(tabs.size() == MAX_TABS)
                System.err.println("Error: Too many tabs in " + category.name + " category, cannot display " + s + "!");
            else
                tabs.add(s);

        Collections.sort(tabs);

        String tabName;
        int component = TAB_BTN_START;
        for(int i = 0; i < MAX_TABS; i++, component += 3) {
            boolean hidden = i >= tabs.size();
            tabName = !hidden ? tabs.get(i) : "";

            // Here we check if it has been complete or not
            if (hasComplete(category, tabName)) {
                tabName = "<col=00ff00>" + tabName;
            } else if(i == tabId) {
                tabName = "<col=f85515>" + tabName;
            } else {
                tabName = "<col=ff981f>" + tabName;
            }
            player.getPackets().sendHideIComponent(ID, component, hidden);
            player.getPackets().sendTextOnComponent(ID, component+2, tabName);
        }
    }

    public String getKillString(String tabName, String kills) {
        if(category.killString == null)
            return "";
        return "<col=ff981f>" +tabName + " " + category.killString + " <col=FFFFFF>" + kills;
    }

    
    public void add(String tab, Item item) {
    	for (CategoryType type : CategoryType.values()) {
    		LogCategory log = MASTER.getCategory(type);
    		for (String name : log.getDrops().keySet()) {
    			if (name.equalsIgnoreCase(tab)) {
    				add(type, name, item, true);
    				break;
    			}
    		}
    	}
    }

    public void add(CategoryType category, String tab, Item item, boolean showMessage) {
        getCategory(category).addToLog(tab, item, showMessage);
    }

    public void addItem(Item item) {
        boolean shown = false;

        // Add to Bosses
        for (Map.Entry<String, Map<Integer, Integer>> entry :
                MASTER.getBosses().getDrops().entrySet()) {

            String bossName = entry.getKey();
            Map<Integer, Integer> masterTab = entry.getValue();

            if (masterTab.containsKey(item.getId())) {
                add(CategoryType.BOSSES, bossName, item, !shown);
                shown = true;
            }
        }

        // Add to Slayer
        for (Map.Entry<String, Map<Integer, Integer>> entry :
                MASTER.getSlayers().getDrops().entrySet()) {

            String slayerName = entry.getKey();
            Map<Integer, Integer> masterTab = entry.getValue();

            if (masterTab.containsKey(item.getId())) {
                add(CategoryType.SLAYER, slayerName, item, !shown);
                shown = true;
            }
        }
    }


    // generally used by minigame reward shops
    public void shopPurchase(Item item) {
        for(Map.Entry<String, Map<Integer, Integer>> tab : getMinigames().getDrops().entrySet()) {
            if(tab.getValue().keySet().contains(item.getId())) {
                // this tab has the item from the shop, increment collected
                add(CategoryType.MINIGAMES, tab.getKey(), item, true);
            }
        }
    }
}

/**
 * Holds all data for category
 */
class LogCategory implements Serializable {

    private static long serialVersionUID = 248848120918481361L;

    /**
     * Tab<Name, Rewards<itemId, amount>
     */
    Map<String, Map<Integer, Integer>> obtainedDrops;

    CategoryType categoryType;

    public LogCategory(CategoryType category) {
        this.categoryType = category;
        obtainedDrops = new LinkedHashMap<>();
    }

    /**
     * Belongs to CollectionLog.MASTER
     */
    public boolean isMaster() {
        return CollectionLog.MASTER.getCategory(categoryType) == this;
    }

    /**
     * Call to add a drop to the log (ex. boss drops, clue reward, etc)
     */
    public void addToLog(String key, Item value, boolean showMessage) {

        Map<Integer, Integer> lootTab = obtainedDrops.get(key);

        if (lootTab == null) {
            lootTab = new HashMap<>();
            obtainedDrops.put(key, lootTab);
        }

        boolean firstTime =
                !lootTab.containsKey(value.getId())
                        || lootTab.get(value.getId()) == 0;

        lootTab.merge(value.getId(), value.getAmount(), Integer::sum);

        // Only one tab is allowed to show message
        if (firstTime && showMessage && !isMaster()) {
            Player player = CollectionLog.MASTER.player;

            if (player != null) {

                player.message(
                        "New item added to your collection log: <col=" +
                                HexColours.Colour.RED.getHex() + value.getName()
                );

                player.getPackets().sendTextOnComponent(
                        1073, 10, "Collection Log"
                );

                player.getPackets().sendTextOnComponent(
                        1073, 11,
                        "<col=" + HexColours.Colour.ORANGE.getHex() +
                                "New item:<br>" + value.getName()
                );

                player.getInterfaceManager().sendOverlay(1073, false);

                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        player.getInterfaceManager().closeOverlay(false);
                        stop();
                    }
                }, 6);
            }
        }
    }




    /**
     * Init a list of items
     */
    public void init(String key, Item[] val) {
        for(Item i : val) init(key, i.getId());
    }

    /**
     * Init a list of items
     */
    public void init(String key, Integer[] val) {
        for(int i : val) init(key, i);
    }

    /**
     * Init a list of items
     */
    public void init(String key, int[] val) {
        for(int i : val) init(key, i);
    }

    /**
     * On login init all drops, if more are added in the future
     * the log will rebuilt with missing indexes
     */
    public void init(String key, int value) {
        Map<Integer, Integer> lootTab = obtainedDrops.get(key);

        if (lootTab == null) {
            // if tab doesn't exist, create
            lootTab = new LinkedHashMap<Integer, Integer>();
            obtainedDrops.put(key, lootTab);
        }

        lootTab.putIfAbsent(value, 0);
    }

    /**
     * Used when merging MASTER list and personal list
     */
    public ItemsContainer<Item> getCollectionList(String lootTabKey) {
        Map<Integer, Integer> lootTab;
        ItemsContainer<Item> con = new ItemsContainer<Item>(CollectionLog.ITEM_WIDTH * CollectionLog.ITEM_HEIGHT,true);

        try {
            lootTab = obtainedDrops.get(lootTabKey);

            if (lootTab == null && isMaster()) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException arg) {
            lootTab = null;
            System.err.println("Could not find loot table! category=" + categoryType + " tab=" + lootTabKey);
            arg.printStackTrace();
        }

        if(lootTab == null) {
            // player hasn't saved any items from this loot tab yet
            return con;
        }

        lootTab.forEach((item, amt) -> {
            if (isMaster())
                con.add(new Item(item, 0, true, null));
            else
                con.add(new Item(item, amt));
        });;
        return con;
    }

    public Map<String, Map<Integer, Integer>> getDrops() {
        return obtainedDrops;
    }
}


