package com.rs.java.game.player.content.presets;

import java.io.Serializable;
import java.util.Map.Entry;

import com.rs.java.game.item.Item;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.summoning.Summoning;

public final class Preset implements Serializable {

	private static final long serialVersionUID = 1385575955598546603L;

	private final Item[] inventory, equipment;
	private final boolean curses;
	private final byte spellBook;
	private final String name;
	private final double[] xp;
	private final Item[] runes;
	private final Summoning.Pouch familiarPouch;

	public Preset(String id, final Item[] inventory, final Item[] equipment, boolean curses, byte spellBook,
			double[] xp, Item[] runes, Summoning.Pouch pouch) {
		this.name = id;
		this.inventory = inventory;
		this.equipment = equipment;
		this.curses = curses;
		this.spellBook = spellBook;
		this.xp = xp;
		this.runes = runes;
		this.familiarPouch = pouch;
		/*int i = 0;
		for (Entry<Integer, Item[]> charges : runicStaff.entrySet()) {
			if (charges == null)
				continue;
			if (i != 0)
				continue;
			this.runicStaff.put(charges.getKey(), charges.getValue());
			i++;
		}
		Iterator<Map.Entry<Integer, Item[]>> iterator = this.runicStaff.entrySet().iterator();
		System.out.println("save this.runicStaff = runicStaff");
		if (!iterator.hasNext())
			System.out.println("failed to save this.runicStaff = runicStaff!");
		while (iterator.hasNext()) {
			Map.Entry<Integer, Item[]> pair = iterator.next();
			for (Item item : pair.getValue()) {
				if (item == null)
					continue;
				System.out
						.println("Preset name: " + name + "saved, rune: " + item.getName() + " x " + item.getAmount());
			}
			System.out.println("Preset name: " + name + "saved, spellId: " + pair.getKey());
		}*/
	}

	public Item[] getInventory() {
		return inventory;
	}

	public Item[] getEquipment() {
		return equipment;
	}

	public Item[] getRunes() {
		return runes;
	}

	public Summoning.Pouch getFamiliar() {
		return familiarPouch;
	}

	public boolean isAncientCurses() {
		return curses;
	}

	public byte getSpellBook() {
		return spellBook;
	}

	public double[] getLevels() {
		return xp;
	}

	public int getId(final Player player) {
		int i = 0;
		for (Entry<String, Preset> gear : player.getPresetManager().PRESET_SETUPS.entrySet()) {
			if (gear.getKey().toLowerCase().equals(name)) {
				return i;
			}
			i++;
		}
		throw new RuntimeException("failed to locate preset");
	}

}