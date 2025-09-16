package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.io.IOException;

public class IndexReplacer {

    public static void main(String[] args) throws IOException {
        // Source and destination cache folders
        String destPath = "data/cache/";     // your main cache
        String sourcePath = "data/onyxcache/cache/"; // donor cache

        int indexId = 9; // the index you want to replace (textures = 9)

        CacheLibrary destCache = new CacheLibrary(destPath, false, null);
        CacheLibrary sourceCache = new CacheLibrary(sourcePath, false, null);

        Index destIndex = destCache.index(indexId);
        Index sourceIndex = sourceCache.index(indexId);

        if (destIndex == null || sourceIndex == null) {
            System.err.println("❌ Index " + indexId + " not found in one of the caches!");
            return;
        }

        // Remove all archives from destination index
        System.out.println("Clearing index " + indexId + " in destination cache...");
        for (Archive archive : destIndex.archives()) {
            if (archive != null) {
                destIndex.remove(archive.getId());
            }
        }
        destIndex.update();

        // Copy all archives from source index
        System.out.println("Copying index " + indexId + " from source to destination...");
        for (Archive sourceArchiveStub : sourceIndex.archives()) {
            if (sourceArchiveStub == null) continue;
            int archiveId = sourceArchiveStub.getId();

            // Load full archive (with data)
            Archive sourceArchive = sourceCache.index(indexId).archive(archiveId);
            if (sourceArchive == null) continue;

            Archive destArchive = destIndex.add(archiveId);
            for (File file : sourceArchive.files()) {
                if (file == null) continue;
                byte[] data = file.getData();
                if (data == null) continue; // skip empty files
                destArchive.add(file.getId(), data);
            }
        }


        destIndex.update();

        System.out.println("✅ Finished replacing index " + indexId +
                " from " + sourcePath + " into " + destPath);
    }
}
