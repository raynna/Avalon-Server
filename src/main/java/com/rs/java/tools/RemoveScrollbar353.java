package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

public class RemoveScrollbar353 {

    private static final String CACHE_PATH = "data/cache/";
    private static final int INDEX = 3;
    private static final int INTERFACE_ID = 3005;
    private static final int SCROLLBAR_ID = 649;

    public static void main(String[] args) {

        try {

            CacheLibrary cache = new CacheLibrary(CACHE_PATH, false, null);

            // Load index
            cache.index(INDEX).update();

            Archive archive = cache.index(INDEX).archive(INTERFACE_ID);

            if (archive == null)
                throw new RuntimeException("Interface " + INTERFACE_ID + " not found.");

            File removed = archive.remove(SCROLLBAR_ID);

            if (removed == null)
                throw new RuntimeException("Component " + SCROLLBAR_ID + " not found.");

            cache.index(INDEX).update();

            System.out.println("Successfully removed component " + SCROLLBAR_ID +
                    " from interface " + INTERFACE_ID);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
