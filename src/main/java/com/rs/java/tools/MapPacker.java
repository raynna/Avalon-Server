package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.rs.java.utils.MapArchiveKeys;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class MapPacker {

    private static final int MAPS_INDEX = 5;

    private static final String CACHE_PATH  = "data/cache/";
    private static final String PACK_FOLDER = "data/map/toPack/";

    private static final int REGION_X = 48;
    private static final int REGION_Y = 54;

    public static void main(String[] args) throws IOException {
        MapArchiveKeys.init();
        String mapArchiveName  = "m" + REGION_X + "_" + REGION_Y; // terrain
        String landArchiveName = "l" + REGION_X + "_" + REGION_Y; // objects/locs

        Path mapPath  = Path.of(PACK_FOLDER + mapArchiveName + ".dat");
        Path landPath = Path.of(PACK_FOLDER + landArchiveName + ".dat");

        byte[] tileData = Files.readAllBytes(mapPath);
        byte[] objData  = Files.readAllBytes(landPath);

        System.out.println("Packing region " + REGION_X + "," + REGION_Y);
        System.out.println(mapArchiveName + " bytes: " + tileData.length);
        System.out.println(landArchiveName + " bytes: " + objData.length);
        int [] keys = MapArchiveKeys.getMapKeys((REGION_X * 256) + REGION_Y);
        if (keys == null) {
            System.out.println("Keys were null");
            keys = new int[] { 0,0,0,0};
        }
        System.out.println("Keys: " + Arrays.toString(Arrays.stream(keys).toArray()));
        CacheLibrary cache = new CacheLibrary(CACHE_PATH, false, null);
        try {
            cache.put(MAPS_INDEX, mapArchiveName, tileData);

            if (keys != null) {
                cache.put(MAPS_INDEX, landArchiveName, objData, keys);
            } else {
                cache.put(MAPS_INDEX, landArchiveName, objData);
            }

            cache.update();
            System.out.println("Finished packing " + mapArchiveName + " / " + landArchiveName);
        } finally {
            cache.close();
        }
    }
}