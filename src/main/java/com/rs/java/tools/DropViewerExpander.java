package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import com.rs.core.cache.Cache;
import com.rs.core.cache.defintions.IComponentDefinitions;
import com.rs.core.packets.InputStream;
import com.rs.core.packets.OutputStream;

import java.util.Arrays;

public class DropViewerExpander {

    private static final String CACHE_PATH = "data/cache/";
    private static final int INDEX = 3;
    private static final int INTERFACE_ID = 3005;

    // Template row
    private static final int TEMPLATE_PARENT = 345;
    private static final int TEMPLATE_CHILD_START = 346;
    private static final int CHILD_COUNT = 7;

    // How many extra rows
    private static final int BLOCKS_TO_ADD = 1;

    public static void main(String[] args) {
        try {
            Cache.init();

            CacheLibrary cache = new CacheLibrary(CACHE_PATH, false, null);
            cache.index(INDEX).update();

            Archive archive = cache.index(INDEX).archive(INTERFACE_ID);
            if (archive == null)
                throw new RuntimeException("Interface not found: " + INTERFACE_ID);

            int maxId = 0;
            for (File f : archive.files()) {
                if (f != null && f.getId() > maxId)
                    maxId = f.getId();
            }
            System.out.println("Highest before: " + maxId);

            File parentTemplateFile = archive.file(TEMPLATE_PARENT);
            if (parentTemplateFile == null)
                throw new RuntimeException("Missing parent template: " + TEMPLATE_PARENT);

            File[] childTemplateFiles = new File[CHILD_COUNT];
            for (int i = 0; i < CHILD_COUNT; i++) {
                int id = TEMPLATE_CHILD_START + i;
                childTemplateFiles[i] = archive.file(id);
                if (childTemplateFiles[i] == null)
                    throw new RuntimeException("Missing child template: " + id);
            }

            for (int block = 0; block < BLOCKS_TO_ADD; block++) {

                int newParentId = ++maxId;

                // Clone parent from template bytes
                IComponentDefinitions parent = decode(parentTemplateFile.getData());

                // IMPORTANT: internal id + hash must match new file id
                parent.anInt4814 = newParentId;
                parent.ihash = (INTERFACE_ID << 16) | newParentId;

                // Parent's own parentId should remain whatever template had (usually some container)
                // If you need to force it, do it here (example):
                // parent.parentId = (INTERFACE_ID << 16) | SOME_CONTAINER_ID;

                // Add children
                for (int i = 0; i < CHILD_COUNT; i++) {

                    int newChildId = ++maxId;

                    IComponentDefinitions child = decode(childTemplateFiles[i].getData());

                    // IMPORTANT: point child to new parent
                    child.parentId = (INTERFACE_ID << 16) | newParentId;

                    // IMPORTANT: internal id + hash must match new file id
                    child.anInt4814 = newChildId;
                    child.ihash = (INTERFACE_ID << 16) | newChildId;

                    archive.add(new File(newChildId, encode(child)));
                }

                archive.add(new File(newParentId, encode(parent)));
            }

            cache.index(INDEX).update();

            System.out.println("Done!");
            System.out.println("Rows added: " + BLOCKS_TO_ADD);
            System.out.println("New last component id: " + maxId);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static IComponentDefinitions decode(byte[] data) {
        IComponentDefinitions def = new IComponentDefinitions();
        def.decode(new InputStream(data));
        return def;
    }

    /**
     * CRITICAL: Only write bytes actually used (0..offset),
     * not the entire backing buffer.
     */
    private static byte[] encode(IComponentDefinitions def) {
        OutputStream out = new OutputStream();
        def.encode(out);
        return out.getBuffer();
    }

}
