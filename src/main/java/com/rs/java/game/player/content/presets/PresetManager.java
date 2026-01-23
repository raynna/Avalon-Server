package com.rs.java.game.player.content.presets;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.rs.Settings;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.meta.DragonFireShieldMetaData;
import com.rs.java.game.item.meta.GreaterRunicStaffMetaData;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.summoning.Summoning;
import com.rs.java.game.player.controlers.EdgevillePvPControler;
import com.rs.java.utils.EconomyPrices;
import com.rs.kotlin.game.player.command.CommandRegistry;
import com.rs.kotlin.game.world.util.Msg;

public final class PresetManager implements Serializable {

    private static final long serialVersionUID = -2928476953478619103L;

    private transient Player player;
    public HashMap<String, Preset> PRESET_SETUPS;

    public void setPlayer(Player player) {
        this.player = player;
    }

    public PresetManager() {
        PRESET_SETUPS = new HashMap<>();
    }

    private int getMaxSize() {
        return 28;
    }

    public void reset() {
        PRESET_SETUPS.clear();
        Msg.info(player, "All of your sets have been cleared. You now have " + getMaxSize() + " available slots.");
    }

    public void removePreset(String name) {
        if (name == "")
            return;
        name = name.toLowerCase();
        if (PRESET_SETUPS.remove(name) == null) {
            Msg.warn(player, "No set was found for the query: " + name);
        } else {
            Msg.success(player, "Successfully removed the set: " + name);
        }
    }

    public void savePreset(String name) {
        final int size = PRESET_SETUPS.size(), max = getMaxSize();
        if (size >= max) {
            Msg.warn(player, "You were unable to store the set " + name
                    + " as your maximum capacity (" + max + ") has been reached.");
            return;
        }
        if (name == "")
            return;
        name = name.toLowerCase();
        final Preset set = PRESET_SETUPS.get(name);
        if (set != null) {
            Msg.warn(player, "You were unable to store the set " + name + " as it already exists.");
            return;
        }
        final Item[] inventory =
                player.getInventory().getItems().getItemsCopy(),
                equipment = player.getEquipment().getItems().getItemsCopy(),
                runes = player.getRunePouch().getContainerItems();
        Familiar familiar = player.getFamiliar();
        Summoning.Pouch pouch = (familiar != null && familiar.getPouch() != null) ? familiar.getPouch() : null;
        PRESET_SETUPS.put(name,
                new Preset(name, inventory, equipment, player.getPrayer().isAncientCurses(),
                        player.getCombatDefinitions().spellBook, (Arrays.copyOf(player.getSkills().getXp(), 7)), runes, pouch));
        Msg.success(player, "You've successfully stored the set " + name + ".");
    }

    public void printPresets() {
        final int size = PRESET_SETUPS.size();
        Msg.info(player, "You have used " + size + "/" + getMaxSize() + " available setups.");
        if (size > 0) {
            Msg.info(player, "<col=ff0000>Your available setups are:");
            for (final String key : PRESET_SETUPS.keySet()) {
                player.message(key, true);
            }
        }
    }

    private boolean requiresBankItem(Item item) {
        int id = item.getId();

        if (!item.getDefinitions().isTradeable() && Settings.ECONOMY_MODE < Settings.FULL_SPAWN) {
            return true;
        }

        if (Settings.ECONOMY_MODE == Settings.HALF_ECONOMY &&
                (EconomyPrices.getPrice(id) >= 0 || isSpecialNonSpawnable(item))) {
            return true;
        }

        return Settings.ECONOMY_MODE == Settings.FULL_ECONOMY;
    }

    private boolean isSpecialNonSpawnable(Item item) {
        int id = item.getId();
        return id == 995 || id == Item.getId("item.pvp_token") || !item.getDefinitions().isTradeable();
    }

    /**
     * Attempts to take item (or partial) from bank.
     * Returns a copy of the item with correct amount, or null if none could be withdrawn.
     */
    private Item takeFromBankOrFail(Player player, Item original) {
        Item bankItem = player.getBank().getItem(original.getId());

        if (bankItem == null) {
            Msg.warn(player, "Couldn't find any " + original.getName() + " in your bank.");
            return null;
        }

        int available = bankItem.getAmount();
        int requested = original.getAmount();
        int toRemove = Math.min(available, requested);

        if (toRemove <= 0) {
            return null;
        }

        int[] slot = player.getBank().getItemSlot(original.getId());
        player.getBank().removeItem2(slot, toRemove, true, false);

        Item copy = new Item(original); // deep copy
        copy.setAmount(toRemove);

        if (available < requested) {
            Msg.warn(player, "You only had " + available + " x " + original.getName() + " in your bank.");
        }

        return copy;
    }

    private void handleForcedCharges(Item item) {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            return; // does not work in full eco
        }

        if (item.isAnyOf("item.dragonfire_shield_charged", "item.dragonfire_shield_uncharged")) {
            if (item.getMetadata() == null) {
                item.setMetadata(new DragonFireShieldMetaData(50));
                if (item.isItem("item.dragonfire_shield_uncharged")) {
                    item.setId(Item.getId("item.dragonfire_shield_charged"));
                }
            } else {
                item.getMetadata().setValue(50);
            }
        }

        if (item.isAnyOf("item.greater_runic_staff_charged", "item.greater_runic_staff_uncharged")) {
            if (item.isItem("item.greater_runic_staff_uncharged")) {
                item.setId(Item.getId("item.greater_runic_staff_charged"));
            }
            GreaterRunicStaffMetaData staffData = (GreaterRunicStaffMetaData) item.getMetadata();
            if (staffData == null) {
                item.setMetadata(new GreaterRunicStaffMetaData(23, 250));
            } else if (staffData.getCharges() == 0 || staffData.getSpellId() == -1) {
                staffData.setSpellId(23);
                staffData.setValue(250);
            }
        }
    }

    public void loadPreset(String name, Player p2) {
        loadPreset(name, p2, false);
    }

    public void loadPreset(String name, Player p2, boolean force) {
        if (name.isEmpty()) return;

        if (player.inPkingArea()) {
            Msg.warn(player, "You can't load gear presets in player killing areas.");
            return;
        }
        if (EdgevillePvPControler.isAtPvP(player) && !EdgevillePvPControler.isAtBank(player)) {
            Msg.warn(player, "You can't load gear presets in pvp area.");
            return;
        }

        name = name.toLowerCase();
        final Preset set = (p2 != null ? p2.getPresetManager().PRESET_SETUPS.get(name) : PRESET_SETUPS.get(name));
        if (set == null) {
            Msg.warn(player, "You were unable to load the set " + name + " as it does not exist.");
            return;
        }

        for (Item item : player.getInventory().getItems().getItemsCopy()) {
            if (item != null) {
                player.getBank().addItem(item, true);
            }
        }
        for (Item item : player.getEquipment().getItems().getItemsCopy()) {
            if (item != null) {
                player.getBank().addItem(item, true);
            }
        }

        player.getRunePouch().reset();
        player.getInventory().reset();
        player.getEquipment().reset();
        player.getInventory().refresh();
        player.getEquipment().refresh();
        player.getAppearence().generateAppearenceData();
        player.refreshHitPoints();
        player.getPrayer().reset();


        if (Settings.ECONOMY_MODE != Settings.FULL_ECONOMY) {
            double[] presetXp = set.getLevels();
            if (presetXp != null && presetXp.length >= 7) {
                for (int i = 0; i < 7; i++) {
                    player.getSkills().setXp(i, presetXp[i]);
                    player.getSkills().set(i, player.getSkills().getLevelForXp(i));
                    player.getSkills().refresh(i);
                }
            }
        }

        Item[] data = set.getEquipment();
        if (data != null) {
            skip:
            for (int i = 0; i < data.length; i++) {
                Item item = data[i];
                if (item == null) continue;

                Map<Integer, Integer> requirements = item.getDefinitions().getWearingSkillRequiriments();
                if (requirements != null) {
                    for (Map.Entry<Integer, Integer> req : requirements.entrySet()) {
                        int skillId = req.getKey(), level = req.getValue();
                        if (player.getSkills().getLevelForXp(skillId) < level) {
                            Msg.warn(player, "You were unable to equip your " + item.getName().toLowerCase()
                                    + ", as you don't meet the requirements to wear them.");
                            continue skip;
                        }
                    }
                }

                Item toUse = item.copy();
                if (!force && requiresBankItem(item)) {
                    toUse = takeFromBankOrFail(player, item);
                    if (toUse == null) continue;
                }

                handleForcedCharges(toUse);
                player.getEquipment().getItems().set(i, toUse);
                player.getEquipment().refresh(i);
            }
        }

        data = set.getInventory();
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                Item item = data[i];
                if (item == null) {
                    player.getInventory().addItem(0, 1);
                    continue;
                }

                Item toUse = item.copy();
                if (!force && requiresBankItem(item)) {
                    toUse = takeFromBankOrFail(player, item);
                    if (toUse == null) {
                        player.getInventory().addItem(0, 1);
                        continue;
                    }
                }

                handleForcedCharges(toUse);
                player.getInventory().addItem(toUse);
            }
        }

        data = set.getRunes();
        if (data != null) {
            for (Item item : data) {
                if (item != null) {
                    player.getRunePouch().add(item);
                    player.getRunePouch().shift();
                }
            }
        }

        Summoning.Pouch pouch = set.getFamiliar();
        if (pouch != null) {
            Item pouchItem = new Item(pouch.getRealPouchId());
            if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
                if (!player.getBank().containsOneItem(pouch.getRealPouchId())) {
                    Msg.warn(player, "Couldn't find " + pouchItem.getName() + " in your bank.");
                } else if (Summoning.spawnFamiliar(player, pouch, true)) {
                    player.getBank().removeItem(pouch.getRealPouchId());
                }
            } else {
                Summoning.spawnFamiliar(player, pouch, true);
            }
        }

        player.getInventory().deleteItem(0, 28);
        player.getCombatDefinitions().setSpellBook(set.getSpellBook(), false);
        player.getPrayer().setPrayerBook(set.isAncientCurses());
        player.getAppearence().generateAppearenceData();
        player.getSkills().switchXPPopup(true);
        player.getSkills().switchXPPopup(true);
        CommandRegistry.execute(player, "heal");
        Msg.info(player, "Loaded setup: " + name + ".");
    }

    public void copyPreset(Player p2) {
        if (p2 == null)
            return;
        if (player.inPkingArea()) {
            Msg.warn(player, "You can't load gear presets in player killing areas.");
            return;
        }
        if (EdgevillePvPControler.isAtPvP(player) && !EdgevillePvPControler.isAtBank(player)) {
            Msg.warn(player, "You can't load gear presets in pvp area.");
            return;
        }

        for (Item item : player.getInventory().getItems().getItemsCopy()) {
            if (item != null && (Settings.ECONOMY_MODE != Settings.FULL_SPAWN)) {
                player.getBank().addItem(item, true);
            }
        }
        for (Item item : player.getEquipment().getItems().getItemsCopy()) {
            if (item != null && (Settings.ECONOMY_MODE != Settings.FULL_SPAWN)) {
                player.getBank().addItem(item, true);
            }
        }

        for (Map.Entry<Integer, Item[]> entry : player.getStaffCharges().entrySet()) {
            if (entry.getValue() != null) {
                for (Item item : entry.getValue()) {
                    if (item != null) {
                        Msg.info(player, "Added " + item.getName() + " x " + item.getAmount() + " to your bank.");
                        player.getBank().addItem(item, true);
                    }
                }
            }
        }

        player.getRunePouch().reset();
        player.getInventory().reset();
        player.getEquipment().reset();
        player.getInventory().refresh();
        player.getEquipment().refresh();
        player.getAppearence().generateAppearenceData();
        player.refreshHitPoints();
        player.getPrayer().reset();

        if (Settings.ECONOMY_MODE != Settings.FULL_ECONOMY) {
            for (int skillId = 0; skillId < 6; skillId++) {
                double xp = p2.getSkills().getXp(skillId);
                player.getSkills().setXp(skillId, xp);
                player.getSkills().set(skillId, p2.getSkills().getLevel(skillId));
                player.getSkills().refresh(skillId);
            }
        }

        Item[] data = p2.getEquipment().getItems().getContainerItems();
        if (data != null) {
            skip:
            for (int i = 0; i < data.length; i++) {
                Item item = data[i];
                if (item == null) continue;

                Map<Integer, Integer> requirements = item.getDefinitions().getWearingSkillRequiriments();
                if (requirements != null) {
                    for (Map.Entry<Integer, Integer> req : requirements.entrySet()) {
                        int skillId = req.getKey(), level = req.getValue();
                        if (player.getSkills().getLevelForXp(skillId) < level) {
                            Msg.warn(player, "You were unable to equip your " + item.getName().toLowerCase()
                                    + ", as you don't meet the requirements to wear them.");
                            continue skip;
                        }
                    }
                }

                Item toUse = item;
                if (requiresBankItem(item)) {
                    toUse = takeFromBankOrFail(player, item);
                    if (toUse == null) continue;
                }

                handleForcedCharges(toUse);
                player.getEquipment().getItems().set(i, toUse);
                player.getEquipment().refresh(i);
            }
        }

        data = p2.getInventory().getItems().getContainerItems();
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                Item item = data[i];
                if (item == null) {
                    player.getInventory().addItem(0, 1);
                    continue;
                }

                Item toUse = item;
                if (requiresBankItem(item)) {
                    toUse = takeFromBankOrFail(player, item);
                    if (toUse == null) {
                        player.getInventory().addItem(0, 1);
                        continue;
                    }
                }

                handleForcedCharges(toUse);
                player.getInventory().addItem(toUse);
            }
        }

        Familiar familiar = p2.getFamiliar();
        if (familiar != null && familiar.getPouch() != null) {
            Summoning.Pouch pouch = familiar.getPouch();
            Item pouchItem = new Item(pouch.getRealPouchId());
            if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
                if (!player.getBank().containsOneItem(pouch.getRealPouchId())) {
                    Msg.warn(player, "Couldn't find " + pouchItem.getName() + " in your bank.");
                } else if (Summoning.spawnFamiliar(player, pouch, true)) {
                    player.getBank().removeItem(pouch.getRealPouchId());
                }
            } else {
                Summoning.spawnFamiliar(player, pouch, true);
            }
        }
        player.getInventory().deleteItem(0, 28);
        int spellBook = p2.getCombatDefinitions().spellBook;
        player.getCombatDefinitions().setSpellBook(spellBook == 0 ? 0 : spellBook == 1 ? 1 : 2);
        player.getPrayer().setPrayerBook(p2.getPrayer().isAncientCurses());
        player.getAppearence().generateAppearenceData();
        player.getSkills().switchXPPopup(true);
        player.getSkills().switchXPPopup(true);
        player.inventory.refresh();
        player.equipment.refresh();
        CommandRegistry.execute(player, "heal");
        Msg.info(player, "You copied " + p2.getDisplayName() + " current preset.");
    }
}
