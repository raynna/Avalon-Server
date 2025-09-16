package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.io.IOException;

public class IndexSpriteScanner {

    // Change this to the index you want to scan
    private static final int INDEX = 8; // e.g. 9=textures, 23=materials, 35=??
    private static final int[] TARGET_VALUES = { 318, 20318, 200318 };

    public static void main(String[] args) throws IOException {
        CacheLibrary cache = new CacheLibrary("data/onyxcache/cache/", false, null);

        int archiveCount = 0, fileCount = 0, hitCount = 0;

        for (Archive archiveStub : cache.index(INDEX).archives()) {
            if (archiveStub == null) continue;
            archiveCount++;
            int archiveId = archiveStub.getId();

            Archive archive = cache.index(INDEX).archive(archiveId);
            if (archive == null) continue;

            for (File file : archive.files()) {
                if (file == null) continue;
                byte[] data = file.getData();
                if (data == null || data.length == 0) continue;

                fileCount++;

                // Scan shorts
                for (int i = 0; i < data.length - 1; i++) {
                    int shortVal = ((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF);

                    for (int target : TARGET_VALUES) {
                        if (shortVal == target) {
                            System.out.println("✅ Found short " + target +
                                    " in archive " + archiveId +
                                    ", file " + file.getId() +
                                    " (offset " + i + ")");
                            hitCount++;
                        }
                    }
                }

                // Scan ints
                for (int i = 0; i < data.length - 3; i++) {
                    int intVal = ((data[i] & 0xFF) << 24)
                               | ((data[i + 1] & 0xFF) << 16)
                               | ((data[i + 2] & 0xFF) << 8)
                               | (data[i + 3] & 0xFF);

                    for (int target : TARGET_VALUES) {
                        if (intVal == target) {
                            System.out.println("✅ Found int " + target +
                                    " in archive " + archiveId +
                                    ", file " + file.getId() +
                                    " (offset " + i + ")");
                            hitCount++;
                        }
                    }
                }
            }
        }

        System.out.println("Finished scanning index " + INDEX);
        System.out.println("  Archives processed: " + archiveCount);
        System.out.println("  Files processed:    " + fileCount);
        System.out.println("  Hits:               " + hitCount);
    }
}
