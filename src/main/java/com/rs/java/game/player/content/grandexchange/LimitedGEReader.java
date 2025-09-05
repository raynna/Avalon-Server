package com.rs.java.game.player.content.grandexchange;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.core.cache.defintions.ItemDefinitions.FileUtilities;
import com.rs.java.game.item.Item;

public class LimitedGEReader {

	private static ArrayList<Integer> items = new ArrayList<Integer>();
	private final static String TXT_PATH = "./data/GE/limitedItems.txt";
	private static FileReader fr;

	public static void init() {
		try {
			initReaders();
			readToStoreCollection();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private static void readToStoreCollection() throws Exception {
		//StringBuffer names = new StringBuffer();
		for (String lines : FileUtilities.readFile(TXT_PATH)) {
			items.add(Integer.parseInt(lines));
			//	names.append("\n[Limited Items]"+ItemDefinitions.getItemDefinitions(Integer.parseInt(lines)).getName()).append(", ");
		}
		//System.out.println("[Launcher] Initiated " + items.size() + " Limited items.");
		//System.out.println("[Launcher] Initiated items: " + names.replace(names.length() - 2, names.length(), "").toString());
		items.add(Item.getId("item.rocktail"));
		items.add(Item.getId("item.overload_4"));
		items.add(Item.getId("item.super_prayer_4"));
		items.add(Item.getId("item.super_combat_potion_4"));
		items.add(Item.getId("item.prayer_renewal_4"));
		items.add(Item.getId("item.super_antifire_4"));
		items.add(Item.getId("item.purple_sweets_2"));
		items.add(Item.getId("item.potion_flask"));
		items.add(Item.getId("item.morrigan_s_throwing_axe"));
		items.add(Item.getId("item.morrigan_s_javelin"));
		items.add(8848);
		items.add(8849);
		items.add(8850);
		items.add(Item.getId("item.royal_d_hide_body"));
		items.add(Item.getId("item.royal_d_hide_chaps"));
		items.add(Item.getId("item.dragon_arrow"));
		items.add(Item.getId("item.dragon_dart"));
		items.add(Item.getId("item.dragon_bolts_e"));
		items.add(Item.getId("item.zanik_s_crossbow"));
		for (int i = 4695; i <= 4699; i++)
			items.add(i);
		items.add(21773);
		// System.out.println("[Launcher] Initiated " + items.size() + " limited
		// items.");
	}
	
	private static int getId(String name) {
		return ItemDefinitions.getId(name);
	}

	private static void initReaders() throws Exception {
		fr = new FileReader(TXT_PATH);
		new BufferedReader(fr);
	}

	public static ArrayList<Integer> getLimitedItems() {
		return items;
	}

	public static void reloadLimiteditems() {
		try {
			items.clear();
			reloadReaders();
			readToStoreCollection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void reloadReaders() throws Exception {
		fr = null;
		initReaders();

	}

	public static boolean itemIsLimited(int itemId) {
		for (int i = 0; i < items.size(); i++) {
			if (itemId == items.get(i)) {
				return true;
			}
		}
		return false;
	}

}
