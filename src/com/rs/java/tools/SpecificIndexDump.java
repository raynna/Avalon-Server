package com.rs.java.tools;

import com.google.gson.GsonBuilder;
import com.rs.core.cache.Cache;

import java.io.*;

public class SpecificIndexDump {

    public static void dumpIndex(int index) throws IOException {
        if (Cache.STORE == null) {
            System.err.println("Cache not initialized! Call Cache.init() first.");
            return;
        }

        var indexes = Cache.STORE.getIndexes();
            var cacheIndex = indexes[index];
            int groupCount = cacheIndex.getValidArchivesCount();
            System.out.println("Dumping cache index " + index + " with " + groupCount + " groups");

            File indexDir = new File("./data/cache_dump/index_" + index);
            if (!indexDir.exists()) indexDir.mkdirs();

            for (int groupId = 0; groupId < groupCount; groupId++) {
                int fileCount = cacheIndex.getValidFilesCount(groupId);

                File groupDir = new File(indexDir, "group_" + groupId);
                if (!groupDir.exists()) groupDir.mkdirs();

                for (int fileId = 0; fileId < fileCount; fileId++) {
                    byte[] data = cacheIndex.getFile(groupId, fileId);
                    if (data == null) continue;

                    try {
                        // Try to decode depending on index
                        Object decoded = tryDecode(index, data);

                        if (decoded != null) {
                            // Serialize decoded object to JSON or text file
                            File outFile = new File(groupDir, fileId + ".json");
                            try (FileWriter writer = new FileWriter(outFile)) {
                                new GsonBuilder().setPrettyPrinting().create().toJson(decoded, writer);
                            }
                        } else {
                            // Dump raw data as base64 if no decoder
                            File outFile = new File(groupDir, fileId + ".bin");
                            try (var fos = new FileOutputStream(outFile)) {
                                fos.write(data);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to process index " + index + " group " + groupId + " file " + fileId + ": " + e);
                    }
                }
            }
    }

    private static Object tryDecode(int index, byte[] data) {
        try {
        InputStream stream = new ByteArrayInputStream(data);
            switch (index) {
                default:
                    // no decoder, return raw hex string or null
                    return bytesToHex(data);
            }
        } catch (Exception e) {
            System.err.println("Decode failed at index " + index + ": " + e.getMessage());
            return null;
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }


}
