package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

public class ComponentMover {

    private static final String CACHE_PATH = "data/cache/";
    private static final int INDEX = 3;
    private static final int INTERFACE_ID = 3005;

    private static final int OLD_SCROLLBAR_ID = 353;
    private static final int NEW_SCROLLBAR_ID = 800;

    public static void main(String[] args) {

        try {

            CacheLibrary cache = new CacheLibrary(CACHE_PATH, false, null);
            cache.index(INDEX).update();

            Archive archive = cache.index(INDEX).archive(INTERFACE_ID);

            if (archive == null)
                throw new RuntimeException("Interface not found.");

            File scrollbar = archive.file(OLD_SCROLLBAR_ID);

            if (scrollbar == null)
                throw new RuntimeException("Scrollbar not found: " + OLD_SCROLLBAR_ID);

            // Remove old
            archive.remove(OLD_SCROLLBAR_ID);

            // Add at new ID
            archive.add(new File(NEW_SCROLLBAR_ID, scrollbar.getData()));

            // Save
            cache.index(INDEX).update();

            System.out.println("Scrollbar moved from "
                    + OLD_SCROLLBAR_ID + " to " + NEW_SCROLLBAR_ID);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
