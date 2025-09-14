package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TextureDumper {

    private static final int INDEX = 9; // textures
    private static final Path OUTPUT_DIR = Path.of("dump/textures/");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws IOException {
        CacheLibrary cache = new CacheLibrary("data/onyxcache/cache/", false, null);

        Files.createDirectories(OUTPUT_DIR);

        int archiveCount = 0, fileCount = 0;

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

            try {
                Buffer buf = new Buffer(data);

                if (buf.remaining() < 1) {
                    System.err.println("Archive " + archiveId + " has no opCount byte");
                    continue;
                }

                int opCount = buf.readUnsignedByte();

                List<Map<String, Object>> ops = new ArrayList<>();
                List<Integer> spriteIds = new ArrayList<>();
                List<Integer> textureRefs = new ArrayList<>();

                for (int i = 0; i < opCount; i++) {
                    if (buf.remaining() < 2) {
                        System.err.println("Archive " + archiveId + " ran out of bytes at op#" + i);
                        break;
                    }

                    int type = buf.readUnsignedByte();
                    int len = buf.readUnsignedByte();

                    Map<String, Object> op = new LinkedHashMap<>();
                    op.put("opIndex", i);
                    op.put("type", type);
                    op.put("len", len);

                    if (type == 0 && len >= 2 && buf.remaining() >= 2) {
                        int spriteId = buf.readUnsignedShort();
                        spriteIds.add(spriteId);
                        op.put("spriteId", spriteId);
                        len -= 2;
                    } else if (type == 2 && len >= 2 && buf.remaining() >= 2) {
                        int textureId = buf.readUnsignedShort();
                        textureRefs.add(textureId);
                        op.put("textureRef", textureId);
                        len -= 2;
                    }

                    // Bail if len > remaining
                    if (len > buf.remaining()) {
                        System.err.println("Archive " + archiveId +
                                " op#" + i + " claims len=" + len +
                                " but only " + buf.remaining() + " bytes left. Stopping parse.");
                        break;
                    }

                    // Read remaining bytes (raw)
                    List<Integer> skipped = new ArrayList<>();
                    for (int j = 0; j < len; j++) {
                        skipped.add(buf.readUnsignedByte());
                    }
                    if (!skipped.isEmpty()) {
                        op.put("skipped", skipped);
                    }

                    ops.add(op);
                }

                Map<String, Object> json = new LinkedHashMap<>();
                json.put("archiveId", archiveId);
                json.put("fileLength", data.length);
                json.put("opCount", opCount);
                json.put("ops", ops);
                json.put("spritesUsed", spriteIds);
                json.put("texturesReferenced", textureRefs);

                try (FileWriter writer = new FileWriter(OUTPUT_DIR.resolve(archiveId + ".json").toFile())) {
                    GSON.toJson(json, writer);
                }

            } catch (Exception e) {
                System.err.println("Failed to parse texture " + archiveId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Finished dumping.");
        System.out.println("  Archives processed: " + archiveCount);
        System.out.println("  Files processed:    " + fileCount);
    }

    // Minimal Buffer for reading
    static class Buffer {
        private final byte[] data;
        int pos = 0;

        Buffer(byte[] data) {
            this.data = data;
        }

        int readUnsignedByte() {
            if (pos >= data.length) {
                throw new IndexOutOfBoundsException("readUnsignedByte: pos=" + pos + " length=" + data.length);
            }
            return data[pos++] & 0xFF;
        }

        int readUnsignedShort() {
            if (pos + 1 >= data.length) {
                throw new IndexOutOfBoundsException("readUnsignedShort: pos=" + pos + " length=" + data.length);
            }
            return ((data[pos++] & 0xFF) << 8) | (data[pos++] & 0xFF);
        }

        int remaining() {
            return data.length - pos;
        }
    }
}
