package com.rs.java.game.player.content.collectionlog;

import java.util.ArrayList;


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

    static {
    }

    public static void build(CollectionLog log) {
        buildBosses(log.getBosses());
        buildClues(log.getClues());
        buildOthers(log.getOthers());
        buildMinigames(log.getMinigames());
        buildSlayer(log.getSlayers());
    }

    private static void buildBosses(LogCategory cat) {
        cat.init("Kree'arra", getNPCDrops(6222));
        cat.init("Commander Zilyana", getNPCDrops(6247));
        cat.init("General Graardor", getNPCDrops(6260));
        cat.init("K'ril Tsutsaroth", getNPCDrops(6203));
        cat.init("Nex", getNPCDrops(13447));
        cat.init("Dagannoth Rex", getNPCDrops(2883));
        cat.init("Dagannoth Prime", getNPCDrops(2882));
        cat.init("Dagannoth Supreme", getNPCDrops(2881));
    }

    private static Integer[] getNPCDrops(int id) {
        return DropTableUtils.getNpcItems(id);
    }

    private static void buildMinigames(LogCategory cat) {
    }

    private static void buildOthers(LogCategory cat) {
        cat.init("Pets", pets.toArray(new Integer[pets.size()]));
    }

    private static void buildClues(LogCategory cat) {
    }

    private static void buildSlayer(LogCategory cat) {
        cat.init("Abyssal demon", getNPCDrops(1615));
    }
}
