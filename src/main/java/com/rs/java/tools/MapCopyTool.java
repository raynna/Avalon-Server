package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.rs.java.utils.MapArchiveKeys;

import java.io.IOException;
import java.util.Arrays;

public class MapCopyTool {

    private static final int MAPS_INDEX = 5;

    private static final String SOURCE_CACHE = "data/634/cache/";
    private static final String DEST_CACHE   = "data/742/";

    private static final int REGION_ID = 12598;

    public static void main(String[] args) throws IOException {

        MapArchiveKeys.init();

        int regionX = REGION_ID >> 8;
        int regionY = REGION_ID & 0xFF;

        String mapName  = "m" + regionX + "_" + regionY;
        String landName = "l" + regionX + "_" + regionY;

        int[] keys = new int[] {//ge xteas from 634
                6023912,
                -1398996940,
                -1850857481,
                -1428087612
        };

        System.out.println("Copying region: " + regionX + "," + regionY);

        CacheLibrary source = new CacheLibrary(SOURCE_CACHE, false, null);
        CacheLibrary dest   = new CacheLibrary(DEST_CACHE, false, null);
        var idx = dest.index(5);

        var m = idx.archive("m49_54");
        System.out.println("DEST m49_54 containsData=" + (m != null && m.containsData()));

        var l = idx.archive("l49_54", keys);
        System.out.println("DEST l49_54 containsData=" + (l != null && l.containsData()));
        try {

            var srcIndex = source.index(MAPS_INDEX);

            int mapArchiveId = srcIndex.archiveId(mapName);
            int landArchiveId = srcIndex.archiveId(landName);

            System.out.println("Map archive name: " + mapName);
            System.out.println("Land archive name: " + landName);
            System.out.println("Map archive id: " + mapArchiveId);
            System.out.println("Land archive id: " + landArchiveId);

            if (mapArchiveId == -1 || landArchiveId == -1) {
                System.out.println("Archive not found in source.");
                return;
            }

            byte[] mapData = source.data(MAPS_INDEX, mapArchiveId);
            byte[] landData = source.data(MAPS_INDEX, landArchiveId, 0, keys);

            System.out.println("Map data length: " + (mapData == null ? "NULL" : mapData.length));
            System.out.println("Land data length: " + (landData == null ? "NULL" : landData.length));

            if (mapData == null || landData == null) {
                System.out.println("Sector data is null.");
                return;
            }

            System.out.println("Packing with keys: " + Arrays.toString(keys));

            dest.put(MAPS_INDEX, mapName, mapData);
            dest.put(MAPS_INDEX, landName, landData, keys);

            dest.update();

            System.out.println("Region copied successfully.");
            var a = idx.archive("l49_54", keys);
            System.out.println("dest land containsData=" + (a != null && a.containsData()));
        } finally {
            source.close();
            dest.close();
        }
    }
}