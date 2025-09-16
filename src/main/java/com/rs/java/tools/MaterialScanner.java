package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MaterialScanner {

    private static final int INDEX = 23; // materials
    private static final Path OUTPUT_DIR = Path.of("dump/materials/");

    // sprite we’re searching for
    private static final int TARGET_SPRITE_ID = 318;

    public static void main(String[] args) throws IOException {
        CacheLibrary cache = new CacheLibrary("data/onyxcache/cache/", false, null);

        Files.createDirectories(OUTPUT_DIR);

        int archiveCount = 0, fileCount = 0, hitCount = 0;

        for (Archive archiveStub : cache.index(INDEX).archives()) {
            if (archiveStub == null) continue;
            archiveCount++;
            int archiveId = archiveStub.getId();

            Archive archive = cache.index(INDEX).archive(archiveId);
            if (archive == null) continue;

            File file = archive.file(0);
            if (file == null) continue;

            byte[] data = file.getData();
            if (data == null || data.length == 0) continue;

            fileCount++;

            boolean found = false;

            for (int i = 0; i < data.length - 1; i++) {
                int value = ((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF);
                if (value == TARGET_SPRITE_ID) {
                    System.out.println("✅ Material archive " + archiveId +
                            " contains spriteId " + TARGET_SPRITE_ID +
                            " at offset " + i);
                    found = true;
                    hitCount++;
                }
            }

            if (!found) {
                // Uncomment if you want to see which archives didn’t match
                System.out.println("Material " + archiveId + " has no match.");
            }
        }

        System.out.println("Finished scanning.");
        System.out.println("  Archives processed: " + archiveCount);
        System.out.println("  Files processed:    " + fileCount);
        System.out.println("  Hits:               " + hitCount);
    }
}
