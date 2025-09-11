package com.rs.java.tools;

import com.rs.core.cache.Cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextureDumper {

    private static final int TEXTURE_INDEX = 9;

    public static void main(String[] args) throws IOException {
        Cache.init(); // make sure this sets Cache.STORE

        var cacheIndex = Cache.STORE.getIndexes()[TEXTURE_INDEX];
        int groupCount = cacheIndex.getValidArchivesCount();
        System.out.println("Dumping " + groupCount + " textures...");

        File outDir = new File("./data/cache_dump/textures");
        if (!outDir.exists()) outDir.mkdirs();

        for (int groupId = 0; groupId < groupCount; groupId++) {
            int fileCount = cacheIndex.getValidFilesCount(groupId);
            for (int fileId = 0; fileId < fileCount; fileId++) {
                byte[] data = cacheIndex.getFile(groupId, fileId);
                if (data == null || data.length == 0) continue;

                File outFile = new File(outDir, groupId + "_" + fileId + ".dat");
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    fos.write(data);
                }
                System.out.println("Dumped texture " + groupId + " (" + data.length + " bytes)");
            }
        }

        System.out.println("Texture dump complete!");
    }
}
