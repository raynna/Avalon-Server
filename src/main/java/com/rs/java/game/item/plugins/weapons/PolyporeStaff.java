package com.rs.java.game.item.plugins.weapons;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.item.meta.PolyporeStaffMetaData;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;

public class PolyporeStaff extends ItemPlugin {

    private static final int POLYPORE_STAFF_ID = 22494; // Fully charged staff
    private static final int POLYPORE_STAFF_DEGRADED_ID = 22496; // Degraded staff
    private static final int FIRE_RUNE_ID = Item.getId("item.fire_rune");
    private static final int POLYPORE_SPORE_ID = Item.getId("item.polypore_spore");
    private static final int POLYPORE_STICK_ID = Item.getId("item.polypore_stick");
    private static final int MAX_CHARGES = 3000;

    @Override
    public Object[] getKeys() {
        return new Object[]{POLYPORE_STAFF_ID, POLYPORE_STAFF_DEGRADED_ID};
    }

    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        switch (option) {
            case "check":
                checkCharges(player, item);
                return true;
            case "clean":
                cleanStaff(player, item);
                return true;
        }
        return false;
    }

    @Override
    public boolean processItemOnItem(Player player, Item item1, Item item2, int fromSlot, int toSlot) {
        Item staff = findStaffItem(item1, item2);
        if (staff == null) {
            return false;
        }

        PolyporeStaffMetaData data = getOrCreateMetadata(staff);
        if (data == null) {
            return false;
        }

        if (!validateCharging(player, data, staff)) {
            return true;
        }

        int chargesToAdd = calculateMaxCharges(player, data);
        if (chargesToAdd == 0) {
            player.message("You don't have enough materials to charge your staff.");
            return true;
        }

        performCharging(player, staff, data, chargesToAdd);
        return true;
    }

    private Item findStaffItem(Item item1, Item item2) {
        if (item1.getId() == POLYPORE_STAFF_DEGRADED_ID) {
            return item1;
        }
        if (item2.getId() == POLYPORE_STAFF_DEGRADED_ID) {
            return item2;
        }
        return null;
    }

    private PolyporeStaffMetaData getOrCreateMetadata(Item staff) {
        ItemMetadata meta = staff.getMetadata();
        if (meta == null) {
            meta = new PolyporeStaffMetaData(MAX_CHARGES);
            staff.setMetadata(meta);
        }

        if (meta instanceof PolyporeStaffMetaData) {
            return (PolyporeStaffMetaData) meta;
        }
        return null;
    }

    private void checkCharges(Player player, Item item) {
        PolyporeStaffMetaData data = getOrCreateMetadata(item);
        if (data != null) {
            if (data.getValue() == data.getMaxValue()) {
                player.message("Your polypore staff is fully charged.");
                return;
            }
            player.message("Your " + item.getName() + " currently has " +
                    data.getValue() + " of " + data.getMaxValue() + " charges left.");
        }
    }

    private void cleanStaff(Player player, Item item) {
        if (item.getMetadata() != null) {
            player.getInventory().deleteItem(item);
            player.getInventory().addItem(new Item(POLYPORE_STICK_ID, 1));
            player.message("You have cleaned your polypore staff.");
        }
    }

    private boolean validateCharging(Player player, PolyporeStaffMetaData data, Item staff) {
        if (data.getValue() >= data.getMaxValue()) {
            player.message("Your polypore staff is already fully charged.");
            return false;
        }

        if (!player.getSkills().hasRequirements(Skills.FARMING, 80)) {
            player.message("You need level 80 Farming to charge your polypore staff.");
            return false;
        }

        if (staff.getId() == POLYPORE_STAFF_ID && data.getValue() > 0) {
            player.message("You can only charge a degraded polypore staff.");
            return false;
        }

        return true;
    }

    private int calculateMaxCharges(Player player, PolyporeStaffMetaData data) {
        int chargesNeeded = data.getMaxValue() - data.getValue();
        int availableSpores = player.getInventory().getAmountOf(POLYPORE_SPORE_ID);
        int availableFireRunes = player.getInventory().getAmountOf(FIRE_RUNE_ID);

        int maxFromSpores = availableSpores; // 1 per charge
        int maxFromFireRunes = availableFireRunes / 5; // 5 per charge

        int maxPossibleCharges = Math.min(maxFromSpores, maxFromFireRunes);

        return Math.min(chargesNeeded, maxPossibleCharges);
    }

    private void performCharging(Player player, Item staff, PolyporeStaffMetaData data, int chargesToAdd) {
        int sporesNeeded = chargesToAdd; // 1 per charge
        int fireRunesNeeded = chargesToAdd * 5; // 5 per charge

        player.getInventory().deleteItem(POLYPORE_SPORE_ID, sporesNeeded);
        player.getInventory().deleteItem(FIRE_RUNE_ID, fireRunesNeeded);
        player.getSkills().addXp(Skills.FARMING, chargesToAdd * 0.1);
        int newCharges = data.getValue() + chargesToAdd;
        data.setValue(newCharges);

        if (newCharges >= data.getMaxValue() && staff.getId() == POLYPORE_STAFF_DEGRADED_ID) {
            staff.setId(POLYPORE_STAFF_ID);
            player.getInventory().refresh();
            player.message("Your polypore staff is now fully charged.");
        } else {
            player.message("You add " + chargesToAdd + " charges to your staff. It now has " +
                    newCharges + " of " + data.getMaxValue() + " charges.");
        }
    }
}