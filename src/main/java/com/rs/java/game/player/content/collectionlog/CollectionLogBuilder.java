package com.rs.java.game.player.content.collectionlog;

import com.rs.kotlin.Rscm;
import com.rs.kotlin.game.npc.drops.tables.BarrowsChestTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * This class builds the MASTER instance of CollectionLog.
 * To be referenced by Player CollectionLog instance.
 *
 * @author Simplex
 * @since May 08, 2020
 */
public class CollectionLogBuilder {
    public static ArrayList<Integer> pets = new ArrayList<Integer>();
    public static ArrayList<ArrayList<Integer>> bosses = new ArrayList<ArrayList<Integer>>();
    private static final Set<Integer> EXCLUDED_ITEMS = new HashSet<>();
    static {
        EXCLUDED_ITEMS.add(Rscm.lookup("item.long_bone"));
        EXCLUDED_ITEMS.add(Rscm.lookup("item.curved_bone"));
        EXCLUDED_ITEMS.add(Rscm.lookup("item.scroll_box_easy"));
        EXCLUDED_ITEMS.add(Rscm.lookup("item.scroll_box_medium"));
        EXCLUDED_ITEMS.add(Rscm.lookup("item.scroll_box_hard"));
        EXCLUDED_ITEMS.add(Rscm.lookup("item.scroll_box_elite"));
    }

    public static void build(CollectionLog log) {
        buildBosses(log.getBosses());
        buildClues(log.getClues());
        buildOthers(log.getOthers());
        buildMinigames(log.getMinigames());
        buildSlayer(log.getSlayers());
    }

    private static void buildBosses(LogCategory cat) {
        cat.init("Tormented Demon", getNPCDrops("npc.tormented_demon_lv450"));
        cat.init("Kalphite Queen", getNPCDrops("npc.kalphite_queen_lv333"));
        cat.init("King Black Dragon", getNPCDrops("npc.king_black_dragon_lv276"));
        cat.init("Kree'arra", getNPCDrops("npc.kree_arra_lv580"));
        cat.init("Commander Zilyana", getNPCDrops("npc.commander_zilyana_lv596"));
        cat.init("General Graardor", getNPCDrops("npc.general_graardor_lv624"));
        cat.init("K'ril Tsutsaroth", getNPCDrops("npc.k_ril_tsutsaroth_lv650"));
        cat.init("Nex", getNPCDrops("npc.nex_lv1001"));
        cat.init("Dagannoth Rex", getNPCDrops("npc.dagannoth_rex_lv303"));
        cat.init("Dagannoth Prime", getNPCDrops("npc.dagannoth_prime_lv303"));
        cat.init("Dagannoth Supreme", getNPCDrops("npc.dagannoth_supreme_lv303"));
        cat.init("Chaos Elemental", getNPCDrops("npc.chaos_elemental_lv305"));
    }


    private static void buildSlayer(LogCategory cat) {
        cat.init("Abyssal demon", getNPCDrops("npc.abyssal_demon_lv124"));
        cat.init("Dark beast", getNPCDrops("npc.dark_beast_lv182"));
        cat.init("Nechryael", getNPCDrops("npc.nechryael_lv115"));
        cat.init("Gargoyle", getNPCDrops("npc.gargoyle_lv111"));
        cat.init("Aberrant Spectre", getNPCDrops("npc.aberrant_spectre_lv96"));
        cat.init("Infernal mage", getNPCDrops("npc.infernal_mage_lv66"));
        cat.init("Crawling hand", getNPCDrops("npc.crawling_hand_lv7"));
    }

    private static void buildOthers(LogCategory cat) {
        cat.init("Cyclops", getItems("item.bronze_defender", "item.iron_defender", "item.steel_defender", "item.black_defender", "item.mithril_defender", "item.adamant_defender", "item.rune_defender", "item.dragon_defender"));
        cat.init("Dragons", getItems("item.dragon_plateskirt", "item.dragon_platelegs", "item.draconic_visage"));
    }

    private static Integer[] getNPCDrops(int npcId) {
        return filterExcluded(DropTableUtils.getNpcItems(npcId));
    }
    private static Integer[] getNPCDrops(String npcName) {
        return filterExcluded(DropTableUtils.getNpcItems(Rscm.lookup(npcName)));
    }

    private static int[] getItemIds(String... itemNames) {
        int[] ids = new int[itemNames.length];
        for (int i = 0; i < itemNames.length; i++) {
            ids[i] = Rscm.lookup(itemNames[i]);
        }
        return ids;
    }

    private static Integer[] getItems(int... itemIds) {
        Integer[] items = new Integer[itemIds.length];
        for (int i = 0; i < itemIds.length; i++) {
            items[i] = itemIds[i];
        }
        return items;
    }

    private static Integer[] getItems(String... itemNames) {
        return filterExcluded(getItems(getItemIds(itemNames)));
    }

    private static Integer[] filterExcluded(Integer[] items) {
        ArrayList<Integer> filtered = new ArrayList<>();

        for (Integer item : items) {
            if (!EXCLUDED_ITEMS.contains(item)) {
                filtered.add(item);
            }
        }

        return filtered.toArray(new Integer[0]);
    }


    private static void buildMinigames(LogCategory cat) {
        cat.init("Barrows", getItems(
                BarrowsChestTable.getAllBarrowsItems()
        ));
    }

    private static void buildClues(LogCategory cat) {
    }
}
