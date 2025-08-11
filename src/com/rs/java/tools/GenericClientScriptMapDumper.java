package com.rs.java.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rs.core.cache.Cache;
import com.rs.core.cache.defintions.ClientScriptMap;
import com.rs.core.packets.InputStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenericClientScriptMapDumper {

    /**
     * Dumps all ClientScriptMaps found in the specified cache index.
     *
     * @param index the cache index to dump from (e.g. 17 for client script maps)
     * @throws IOException if file write fails
     */
    public static void dumpAllMapsFromCacheIndex(int index) throws IOException {
        Map<Integer, Map<String, Object>> allMaps = new LinkedHashMap<>();

        if (Cache.STORE == null) {
            System.err.println("Cache not initialized! Call Cache.init() before running this.");
            return;
        }

        if (index < 0 || index >= Cache.STORE.getIndexes().length) {
            System.err.println("Invalid cache index: " + index);
            return;
        }

        var cacheIndex = Cache.STORE.getIndexes()[index];
        int groupCount = cacheIndex.getValidArchivesCount();

        System.out.println("Dumping all maps from cache index: " + index);
        System.out.println("Total groups: " + groupCount);

        for (int groupId = 0; groupId < groupCount; groupId++) {
            int fileCount = cacheIndex.getValidFilesCount(groupId);

            for (int fileId = 0; fileId < fileCount; fileId++) {
                byte[] data = cacheIndex.getFile(groupId, fileId);
                if (data == null)
                    continue;

                int scriptId = (groupId << 8) | fileId;
                ClientScriptMap map = new ClientScriptMap();
                try {
                    map.readValueLoop(new InputStream(data));
                } catch (Exception e) {
                    System.err.println("Failed to parse script at group " + groupId + " file " + fileId + ": " + e.getMessage());
                    continue;
                }

                if (map.getSize() == 0)
                    continue; // skip empty maps

                Map<String, Object> mapData = new LinkedHashMap<>();
                mapData.put("keyType", map.keyType);
                mapData.put("valueType", map.valueType);
                mapData.put("defaultStringValue", map.getDefaultStringValue());
                mapData.put("defaultIntValue", map.getDefaultIntValue());
                mapData.put("values", map.getValues());

                allMaps.put(scriptId, mapData);
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File dir = new File("./data/clientscripts/maps");
        if (!dir.exists()) dir.mkdirs();

        File outFile = new File(dir, "all_maps_index_" + index + ".json");

        try (FileWriter writer = new FileWriter(outFile)) {
            gson.toJson(allMaps, writer);
        }

        System.out.println("Dumped " + allMaps.size() + " client script maps.");
        System.out.println("Output file: " + outFile.getAbsolutePath());
    }
}
