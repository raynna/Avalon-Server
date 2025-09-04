package com.rs.java.game.player.content.presets;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rs.Settings;
import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.meta.DragonFireShieldMetaData;
import com.rs.java.game.item.meta.GreaterRunicStaffMetaData;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.summoning.Summoning;
import com.rs.java.game.player.controlers.EdgevillePvPControler;
import com.rs.java.utils.EconomyPrices;
import com.rs.java.utils.HexColours;
import com.rs.java.utils.HexColours.Colour;
import com.rs.kotlin.game.player.command.CommandRegistry;
import com.rs.kotlin.game.player.command.commands.HealCommand;

public final class PresetManager implements Serializable {

	private static final long serialVersionUID = -2928476953478619103L;
	/** Instantiated variables below **/
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
		player.message("All of your sets have been cleared. You now have " + getMaxSize() + " available slots.");
	}

	public void removePreset(String name) {
		if (name == "")
			return;
		name = name.toLowerCase();
		player.message((PRESET_SETUPS.remove(name) == null ? "No set was found for the query: " + name
						: "Successfully removed the set: " + name) + ".");
	}

	public void savePreset(String name) {
		final int size = PRESET_SETUPS.size(), max = getMaxSize();
		if (size >= max) {
			player.message("You were unable to store the set " + name
					+ " as your maximum capacity (" + max + ") has been reached.", true);
			return;
		}
		if (name == "")
			return;
		name = name.toLowerCase();
		final Preset set = PRESET_SETUPS.get(name);
		if (set != null) {
			player.message("You were unable to store the set " + name + " as it already exists.",
					true);
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
		player.message("You've successfully stored the set " + name + ".", true);
	}

	public void printPresets() {
		final int size = PRESET_SETUPS.size();
		player.message("You have used " + size + "/" + getMaxSize() + " available setups.", true);
		if (size > 0) {
			player.message("<col=ff0000>Your available setups are:", true);
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
				(EconomyPrices.getPrice(id) >= Settings.LOWPRICE_LIMIT || isSpecialNonSpawnable(item))) {
			return true;
		}

		return Settings.ECONOMY_MODE == Settings.FULL_ECONOMY;
	}

	private boolean isSpecialNonSpawnable(Item item) {
		int id = item.getId();
		return id == 995 || id == 12852 || !item.getDefinitions().isTradeable();
	}

	private boolean takeFromBankOrFail(Player player, Item item) {
		Item bankItem = player.getBank().getItem(item.getId());
		if (bankItem != null && bankItem.getAmount() >= item.getAmount()) {
			int[] slot = player.getBank().getItemSlot(item.getId());
			player.getBank().removeItem2(slot, item.getAmount(), true, false);
			return true;
		} else {
			player.message("Couldn't find item " + item.getAmount() + " x " + item.getName() + " in bank.");
			return false;
		}
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
			player.message(HexColours.getMessage(Colour.RED, "You can't load gear presets in player killing areas."));
			return;
		}
		if (EdgevillePvPControler.isAtPvP(player) && !EdgevillePvPControler.isAtBank(player)) {
			player.message(HexColours.getMessage(Colour.RED, "You can't load gear presets in pvp area."));
			return;
		}

		name = name.toLowerCase();
		final Preset set = (p2 != null ? p2.getPresetManager().PRESET_SETUPS.get(name) : PRESET_SETUPS.get(name));
		if (set == null) {
			player.message("You were unable to load the set " + name + " as it does not exist.", true);
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
						player.message("Added " + item.getName() + " x " + item.getAmount() + " to your bank.");
						player.getBank().addItem(item, true);
					}
				}
			}
		}

		// Reset player state
		player.getRunePouch().reset();
		player.getInventory().reset();
		player.getInventory().refresh();
		player.getEquipment().reset();
		player.getEquipment().refresh();
		player.getAppearence().generateAppearenceData();
		player.refreshHitPoints();
		player.getPrayer().reset();

		// Skills
		double[] presetXp = set.getLevels();
		if (presetXp != null && presetXp.length >= 7) {
			for (int i = 0; i < 7; i++) {
				player.getSkills().setXp(i, presetXp[i]);
				player.getSkills().set(i, player.getSkills().getLevelForXp(i));
				player.getSkills().refresh(i);
			}
		}

		Item[] data = set.getEquipment();
		if (data != null) {
			skip: for (int i = 0; i < data.length; i++) {
				final Item item = data[i];
				if (item == null) continue;

				// Wearing requirements
				Map<Integer, Integer> requirements = item.getDefinitions().getWearingSkillRequiriments();
				if (requirements != null) {
					for (Map.Entry<Integer, Integer> req : requirements.entrySet()) {
						int skillId = req.getKey(), level = req.getValue();
						if (skillId >= 0 && skillId <= 24 && level >= 0 && level <= 120) {
							if (player.getSkills().getLevelForXp(skillId) < level) {
								player.message("You were unable to equip your " + item.getName().toLowerCase()
										+ ", as you don't meet the requirements to wear them.", true);
								continue skip;
							}
						}
					}
				}

				if (!force && requiresBankItem(item) && !takeFromBankOrFail(player, item)) {
					continue;
				}

				handleForcedCharges(item);

				player.getEquipment().getItems().set(i, item);
				player.getEquipment().refresh(i);
			}
		}

		data = set.getInventory();
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				final Item item = data[i];
				if (item == null) {
					player.getInventory().addItem(0, 1);
					continue;
				}

				if (!force && requiresBankItem(item) && !takeFromBankOrFail(player, item)) {
					player.getInventory().addItem(0, 1);
					continue;
				}

				handleForcedCharges(item);
				player.getInventory().addItem(item);
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
					player.message("Couldn't find " + pouchItem.getName() + " in your bank.");
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
		player.message("Loaded setup: " + name + ".");
	}

	public void copyPreset(Player p2) {
		if (player.inPkingArea()) {
			player.message(HexColours.getMessage(Colour.RED, "You can't load gear presets in player killing areas."));
			return;
		}
		if (EdgevillePvPControler.isAtPvP(player) && !EdgevillePvPControler.isAtBank(player)) {
			player.message(HexColours.getMessage(Colour.RED, "You can't load gear presets in pvp area."));
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
						player.message("Added " + item.getName() + " x " + item.getAmount() + " to your bank.");
						player.getBank().addItem(item, true);
					}
				}
			}
		}

		// Reset player state
		player.getRunePouch().reset();
		player.getInventory().reset();
		player.getInventory().refresh();
		player.getEquipment().reset();
		player.getEquipment().refresh();
		player.getAppearence().generateAppearenceData();
		player.refreshHitPoints();
		player.getPrayer().reset();

		for (int skillId = 0; skillId < 6; skillId++) {
			double xp = p2.getSkills().getXp(skillId);
			player.getSkills().setXp(skillId, xp);
			player.getSkills().set(skillId, p2.getSkills().getLevel(skillId));
			player.getSkills().refresh(skillId);
		}

		Item[] data = p2.getEquipment().getItems().getContainerItems();
		if (data != null) {
			skip: for (int i = 0; i < data.length; i++) {
				final Item item = data[i];
				if (item == null) continue;

				// Wearing requirements
				Map<Integer, Integer> requirements = item.getDefinitions().getWearingSkillRequiriments();
				if (requirements != null) {
					for (Map.Entry<Integer, Integer> req : requirements.entrySet()) {
						int skillId = req.getKey(), level = req.getValue();
						if (skillId >= 0 && skillId <= 24 && level >= 0 && level <= 120) {
							if (player.getSkills().getLevelForXp(skillId) < level) {
								player.message("You were unable to equip your " + item.getName().toLowerCase()
										+ ", as you don't meet the requirements to wear them.", true);
								continue skip;
							}
						}
					}
				}

				if (requiresBankItem(item) && !takeFromBankOrFail(player, item)) {
					continue;
				}

				handleForcedCharges(item);

				player.getEquipment().getItems().set(i, item);
				player.getEquipment().refresh(i);
			}
		}

		data = p2.getInventory().getItems().getContainerItems();
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				final Item item = data[i];
				if (item == null) {
					player.getInventory().addItem(0, 1);
					continue;
				}

				if (requiresBankItem(item) && !takeFromBankOrFail(player, item)) {
					player.getInventory().addItem(0, 1);
					continue;
				}

				handleForcedCharges(item);
				player.getInventory().addItem(item);
			}
		}

		Summoning.Pouch pouch = p2.getFamiliar().getPouch();
		if (pouch != null) {
			Item pouchItem = new Item(pouch.getRealPouchId());
			if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
				if (!player.getBank().containsOneItem(pouch.getRealPouchId())) {
					player.message("Couldn't find " + pouchItem.getName() + " in your bank.");
				} else if (Summoning.spawnFamiliar(player, pouch, true)) {
					player.getBank().removeItem(pouch.getRealPouchId());
				}
			} else {
				Summoning.spawnFamiliar(player, pouch, true);
			}
		}
		player.getInventory().deleteItem(0, 28);
		player.getCombatDefinitions().setSpellBook(p2.combatDefinitions.getSpellId(), false);
		player.getPrayer().setPrayerBook(p2.getPrayer().isAncientCurses());
		player.getAppearence().generateAppearenceData();
		player.getSkills().switchXPPopup(true);
		player.getSkills().switchXPPopup(true);
		CommandRegistry.execute(player, "heal");
		player.message("You copied " + p2.getDisplayName() + " current preset.");
	}

}