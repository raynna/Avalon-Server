package com.rs.java.game.player.content.collectionlog;

import com.rs.kotlin.game.npc.TableCategory;
import com.rs.kotlin.game.npc.drops.DropTable;
import com.rs.kotlin.game.npc.drops.DropTableRegistry;

import java.util.Set;

/**
 * Builds MASTER collection log using DropTable metadata.
 */
public class CollectionLogBuilder {

    public static void build(CollectionLog log) {

        for (DropTable table : DropTableRegistry.getAllTables()) {

            Set<Integer> items = table.getAllItemIdsForCollectionLog();

            if (items == null || items.isEmpty())
                continue;

            CategoryType type = mapCategory(table.getCategory());

            String tabName = table.getCollectionGroup() != null
                    ? table.getCollectionGroup()
                    : table.getName();

            log.getCategory(type)
                    .init(tabName, items.stream().mapToInt(i -> i).toArray());
        }
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