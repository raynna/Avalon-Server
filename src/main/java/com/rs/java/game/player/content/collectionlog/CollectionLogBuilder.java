package com.rs.java.game.player.content.collectionlog;

import com.rs.kotlin.game.npc.TableCategory;
import com.rs.kotlin.game.npc.drops.DropTable;
import com.rs.kotlin.game.npc.drops.DropTableRegistry;

import java.util.Set;

public class CollectionLogBuilder {

    public static void build(CollectionLog log) {

        int totalTables = 0;
        int bossTables = 0;
        int slayerTables = 0;
        int minigameTables = 0;
        int otherTables = 0;
        int clueTables = 0;

        for (DropTable table : DropTableRegistry.getAllTables()) {

            Set<Integer> items = table.getAllItemIdsForCollectionLog();

            if (items == null || items.isEmpty()) {
                continue;
            }
            totalTables++;

            CategoryType type = mapCategory(table.getCategory());

            String tabName = table.getCollectionGroup() != null
                    ? table.getCollectionGroup()
                    : table.getName();

            if (type == CategoryType.BOSSES) {
                bossTables++;
            }
            if (type == CategoryType.MINIGAMES) {
                minigameTables++;
            }
            if (type == CategoryType.OTHERS) {
                otherTables++;
            }
            if (type == CategoryType.SLAYER) {
                slayerTables++;
            }
            if (type == CategoryType.CLUES) {
                clueTables++;
            }

            log.getCategory(type)
                    .init(tabName, items.stream().mapToInt(i -> i).toArray());
        }

        System.out.println("[CollectionLog] Total tables loaded: " + totalTables);
        System.out.println("[CollectionLog] Boss tables loaded: " + bossTables);
        System.out.println("[CollectionLog] Slayer tables loaded: " + slayerTables);
        System.out.println("[CollectionLog] Clue tables loaded: " + clueTables);
        System.out.println("[CollectionLog] Minigame tables loaded: " + minigameTables);
        System.out.println("[CollectionLog] Other tables loaded: " + otherTables);
    }

    private static CategoryType mapCategory(TableCategory category) {

        switch (category) {
            case BOSS:
                return CategoryType.BOSSES;

            case SLAYER:
                return CategoryType.SLAYER;

            case MINIGAME:
                return CategoryType.MINIGAMES;

            case CLUE:
                return CategoryType.CLUES;

            default:
                return CategoryType.OTHERS;
        }
    }
}