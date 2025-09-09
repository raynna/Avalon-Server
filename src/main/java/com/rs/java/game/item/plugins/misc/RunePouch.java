package com.rs.java.game.item.plugins.misc;

import com.rs.java.game.World;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.item.meta.RunePouchMetaData;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.combat.Magic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunePouch extends ItemPlugin {

    public static final int INTERFACEID = 3049;
    public static final int INVENTORY_INTERFACE = 670;
    public static final int TAKE_ALL_COMPONENT = 37;
    public static final int RUNE_CONTAINER = 18;
    public static final int INVENTORY_CONTAINER = 29;
    public static final int RUNE_1_COMPONENT = 30;
    public static final int RUNE_2_COMPONENT = 31;

    @Override
    public Object[] getKeys() {
        return new Object[]{24510};
    }

    private RunePouchMetaData getRunePouchMetaData(Item runePouch) {
        if (runePouch == null) return null;

        ItemMetadata meta = runePouch.getMetadata();
        if (!(meta instanceof RunePouchMetaData)) {
            meta = new RunePouchMetaData();
            runePouch.setMetadata(meta);
        }
        return (RunePouchMetaData) meta;
    }


    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        switch (option) {
            case "open":
                openRunePouch(player, slotId);
                return true;
            case "withdraw-all":
                withdrawAll(player, slotId);
                return true;
        }
        return false;
    }

    @Override
    public boolean processItemOnItem(Player player, Item item, Item item2, int itemUsed, int usedWith) {
        if (!Magic.isRune(item.getId())) {
            player.getPackets().sendGameMessage("You can't store " + item.getName() + " in the rune pouch.");
            return true;
        }

        RunePouchMetaData runePouchMeta = getRunePouchMetaData(item2);
        if (runePouchMeta == null) {
            player.getPackets().sendGameMessage("You need a rune pouch in your inventory.");
            return true;
        }

        if (runePouchMeta.getRunes().getOrDefault(item.getId(), 0) == runePouchMeta.getMaxValue()) {
            player.getPackets().sendGameMessage("You can't have more than 16,000 of each rune in the rune pouch.");
            return true;
        }

        if (runePouchMeta.getRunes().size() == runePouchMeta.getMaxEntries()
                && !runePouchMeta.getRunes().containsKey(item.getId())) {
            player.message("You can't store more than 3 types of runes in the rune pouch.");
            return true;
        }

        int amount = item.getAmount();
        int currentAmount = runePouchMeta.getRunes().getOrDefault(item.getId(), 0);
        if (currentAmount + amount > runePouchMeta.getMaxValue()) {
            amount = runePouchMeta.getMaxValue() - currentAmount;
        }

        runePouchMeta.addRune(item.getId(), amount);
        player.getInventory().deleteItem(item.getId(), amount);
        player.getInventory().refresh();

        player.message("You stored " + amount + " x " + item.getName() + " in the rune pouch.");
        return true;
    }

    @Override
    public boolean processDestroy(Player player, Item item, int slotId) {
        ItemMetadata meta = item.getMetadata();
        if (meta instanceof RunePouchMetaData pouchMeta) {
            for (Map.Entry<Integer, Integer> entry : pouchMeta.getRunes().entrySet()) {
                int runeId = entry.getKey();
                int amount = entry.getValue();
                if (amount > 0) {
                    World.updateGroundItem(new Item(runeId, amount), player.getLocation(), player);
                }
            }
            player.getRunePouch().reset();
            player.getPackets().sendGameMessage("All your runes in your rune pouch were dropped on the floor.");
        }

        player.getInventory().dropItem(slotId, item, false);
        return true;
    }

    public static void withdrawAll(Player player, int slotId) {
        Item runePouch = player.getInventory().getItem(slotId);
        if (runePouch == null) {
            player.message("You don't have a rune pouch.");
            return;
        }

        ItemMetadata meta = runePouch.getMetadata();
        if (!(meta instanceof RunePouchMetaData runePouchMeta)) {
            player.message("Your rune pouch is empty.");
            return;
        }

        if (runePouchMeta.getRunes().isEmpty()) {
            player.message("Your rune pouch is empty.");
            return;
        }

        List<Integer> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : runePouchMeta.getRunes().entrySet()) {
            int runeId = entry.getKey();
            int amount = entry.getValue();

            if (!player.getInventory().hasFreeSlots() && !player.getInventory().containsOneItem(runeId)) {
                player.message("You don't have enough inventory spaces.");
                continue;
            }

            player.getInventory().addItem(runeId, amount);
            toRemove.add(runeId);
        }
        for (Integer runeId : toRemove) {
            runePouchMeta.removeRune(runeId, runePouchMeta.getRunes().get(runeId));
            refreshRunePouch(player, runePouchMeta.getRunesToArray());
        }
        if (runePouchMeta.isEmpty()) {
            runePouch.setMetadata(null);
        }
        player.getInventory().refresh();
    }


    public static void storeRunePouch(Player player, Item item, int amount) {
        Item runePouchItem = player.getInventory().getItem((int) player.getTemporaryAttributtes().get("rune_pouch_slot"));
        if (runePouchItem == null) {
            player.message("You don't have a rune pouch.");
            return;
        }
        if (!Magic.isRune(item.getId())) {
            player.getPackets().sendGameMessage("You can't store " + item.getName() + " in the rune pouch.");
            return;
        }

        RunePouchMetaData runePouchMeta;
        ItemMetadata meta = runePouchItem.getMetadata();
        if (meta instanceof RunePouchMetaData) {
            runePouchMeta = (RunePouchMetaData) meta;
        } else {
            runePouchMeta = new RunePouchMetaData();
            runePouchItem.setMetadata(runePouchMeta);
        }

        if (item.getAmount() < amount) {
            amount = item.getAmount();
        }
        int currentAmount = runePouchMeta.getRunes().getOrDefault(item.getId(), 0);
        if (currentAmount == 16000) {
            player.getPackets().sendGameMessage("You can't have more than 16,000 of each rune in the rune pouch.");
            return;
        }
        if (runePouchMeta.getRunes().size() == runePouchMeta.getMaxEntries()
                && !runePouchMeta.getRunes().containsKey(item.getId())) {
            player.getPackets().sendGameMessage("You can't store more than 3 types of runes in the rune pouch.");
            return;
        }
        if (amount + currentAmount > runePouchMeta.getMaxValue())
            amount = runePouchMeta.getMaxValue() - currentAmount;

        player.getInventory().deleteItem(item.getId(), amount);
        runePouchMeta.addRune(item.getId(), amount);
        refreshRunePouch(player, runePouchMeta.getRunesToArray());
        player.getInventory().refresh();

        player.getPackets().sendGameMessage("You store " + amount + " x " + item.getName() + "s in the rune pouch.");
    }


    public static void withdrawRunePouch(Player player, int slotId, int amount) {
        Item runePouchItem = player.getInventory().getItem((int) player.getTemporaryAttributtes().get("rune_pouch_slot"));
        if (runePouchItem == null) {
            player.message("You don't have a rune pouch.");
            return;
        }
        RunePouchMetaData runePouchMeta = null;
        ItemMetadata meta = runePouchItem.getMetadata();
        if (meta instanceof RunePouchMetaData) {
            runePouchMeta = (RunePouchMetaData) meta;
        } else {
            player.message("Your rune pouch is empty.");
            return;
        }

        Item runeAtSlot = runePouchMeta.getRuneAtSlot(slotId);
        if (runeAtSlot == null) {
            player.message("Invalid rune slot.");
            return;
        }
        if (player.getInventory().getFreeSlots() == 0
                && !player.getInventory().containsItem(runeAtSlot.getId(), 1)) {
            player.getPackets().sendGameMessage("You don't have enough inventory spaces.");
            return;
        }

        if (amount > runeAtSlot.getAmount()) {
            amount = runeAtSlot.getAmount();
        }

        runePouchMeta.removeRune(runeAtSlot.getId(), amount);
        refreshRunePouch(player, runePouchMeta.getRunesToArray());
        if (runePouchMeta.isEmpty()) {
            runePouchItem.setMetadata(null);
        }
        player.getInventory().addItem(runeAtSlot.getId(), amount);
        player.getInventory().refresh();
        player.getPackets().sendGameMessage("You withdraw " + amount + " x " + runeAtSlot.getName() + "s from the rune pouch.");
    }

    private static void sendRunePouchInterface(Player player, Item runePouch) {
        ItemMetadata meta = runePouch.getMetadata();
        Item[] items = new Item[3];

        if (meta instanceof RunePouchMetaData runePouchMeta) {
            Map<Integer, Integer> runes = runePouchMeta.getRunes();
            int i = 0;
            for (Map.Entry<Integer, Integer> entry : runes.entrySet()) {
                items[i++] = new Item(entry.getKey(), entry.getValue());
            }
            for (; i < items.length; i++) {
                items[i] = null;
            }
        }

        if (!player.getInterfaceManager().containsInterface(INTERFACEID))
            player.getInterfaceManager().sendInterface(INTERFACEID);
        player.getInterfaceManager().sendInventoryInterface(INVENTORY_INTERFACE);
        //inventory
        player.getPackets().sendInterSetItemsOptionsScript(INVENTORY_INTERFACE, 0, 93, 4, 7, "Store 1", "Store 10", "Store 100", "Store-All");
        player.getPackets().sendUnlockOptions(INVENTORY_INTERFACE, 0, 0, 27, 0, 1, 2, 3);
        //runes on interface
        player.getPackets().sendInterSetItemsOptionsScript(INTERFACEID, RUNE_CONTAINER, 100, 3, 1, "Withdraw 1", "Withdraw 10", "Withdraw 100", "Withdraw-All");
        player.getPackets().sendUnlockOptions(INTERFACEID, RUNE_CONTAINER, 0, 3, 0, 1, 2, 3);
        player.getPackets().sendItems(100, items);
        player.getPackets().sendUpdateItems(100, items, 3);
        //inventory in interface
        player.getPackets().sendInterSetItemsOptionsScript(INTERFACEID, INVENTORY_CONTAINER, 93, 7, 4, "Store 1", "Store 10", "Store 100", "Store-All");
        player.getPackets().sendUnlockOptions(INTERFACEID, INVENTORY_CONTAINER, 0, 27, 0, 1, 2, 3);
        player.getPackets().sendItems(93, player.getInventory().items.getItemsCopy());
        player.getPackets().sendUpdateItems(93, player.getInventory().items.getItemsCopy(), 27);
    }


    public static void openRunePouch(Player player, int slotId) {
        Item runePouch = player.getInventory().getItem(slotId);
        if (runePouch == null) {
            player.message("You don't have a rune pouch.");
            return;
        }
        player.temporaryAttribute().put("rune_pouch_slot", slotId);
        sendRunePouchInterface(player, runePouch);
        player.setCloseInterfacesEvent(() -> {
            player.temporaryAttribute().remove("rune_pouch_slot");
        });
    }

    public static void refreshRunePouch(Player player, Item[] items) {
        Integer slotId = (Integer) player.temporaryAttribute().get("rune_pouch_slot");
        if (slotId == null)
            return;

        Item runePouch = player.getInventory().getItem(slotId);
        if (runePouch == null)
            return;

        player.getPackets().sendItems(100, items);
        player.getPackets().sendUpdateItems(100, items, 3);
        player.getPackets().sendItems(93, player.inventory.items.getItemsCopy());
        player.getPackets().sendUpdateItems(93, player.inventory.items.getItemsCopy(), 27);
    }
}
