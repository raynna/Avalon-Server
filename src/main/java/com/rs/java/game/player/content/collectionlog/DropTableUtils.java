package com.rs.java.game.player.content.collectionlog;

import com.rs.kotlin.game.npc.drops.DropTable;
import com.rs.kotlin.game.npc.drops.DropTableRegistry;

public class DropTableUtils {

    public static Integer[] getNpcItems(int npcId) {

        DropTable table = DropTableRegistry.getDropTableForNpc(npcId);

        if (table == null)
            return new Integer[0];

        return table.getAllItemIdsForCollectionLog()
                .toArray(new Integer[0]);
    }

    public static Integer[] getTableDrops(DropTable table) {

        if (table == null)
            return new Integer[0];

        return table.getAllItemIdsForCollectionLog()
                .toArray(new Integer[0]);
    }
}
