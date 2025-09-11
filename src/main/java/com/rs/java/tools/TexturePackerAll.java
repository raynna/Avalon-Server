package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

public class TexturePackerAll {

    private static final int TEXTURE_INDEX = 11;

    public static void main(String[] args) {
        try {
            CacheLibrary toCache = new CacheLibrary("data/cache/", false, null);
            CacheLibrary fromCache = new CacheLibrary("data/onyxcache/cache/", false, null);

            Index fromIndex = fromCache.index(TEXTURE_INDEX);
            Index toIndex = toCache.index(TEXTURE_INDEX);

            System.out.println("Clearing destination index " + TEXTURE_INDEX + "...");
            toIndex.clear(); // wipes all old textures

            int packed = 0;

            for (Archive fromArchive : fromIndex.archives()) {
                if (fromArchive == null) continue;

                // Create new archive in destination
                Archive toArchive = toIndex.add(fromArchive.getId());

                // Copy all files
                for (File f : fromArchive.files()) {
                    byte[] data = f.getData();
                    if (data == null || data.length == 0) continue;

                    toArchive.add(f, true);
                    packed++;
                }
            }

            // Save changes
            toIndex.update();

            System.out.println("Replaced index " + TEXTURE_INDEX + " with " + packed + " textures.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
