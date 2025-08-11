package com.rs.java.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rs.core.cache.defintions.ClientScriptMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MusicScriptDumper {

    public static void dumpAllMusic(int maxId) throws IOException {
        Map<Integer, Map<String, Object>> allMaps = new LinkedHashMap<>();

        for (int scriptId = 0; scriptId <= maxId; scriptId++) {
            ClientScriptMap map = ClientScriptMap.getMap(scriptId);
            if (map == null || map.getSize() == 0)
                continue;  // Skip if map not found or empty

            Map<String, Object> mapData = new LinkedHashMap<>();
            mapData.put("keyType", map.keyType);
            mapData.put("valueType", map.valueType);
            mapData.put("defaultStringValue", map.getDefaultStringValue());
            mapData.put("defaultIntValue", map.getDefaultIntValue());
            mapData.put("values", map.getValues());

            allMaps.put(scriptId, mapData);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File dir = new File("./data/clientscripts/maps");
        if (!dir.exists()) dir.mkdirs();

        File outFile = new File(dir, "all_interface_script_map.json");

        try (FileWriter writer = new FileWriter(outFile)) {
            gson.toJson(allMaps, writer);
        }

        System.out.println("Dumped " + allMaps.size() + " client script maps.");
        System.out.println("Output file: " + outFile.getAbsolutePath());
    }
}
