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
import java.util.*;

public class TextureDumper {

    private static final int INDEX = 9; // textures
    private static final Path OUTPUT_DIR = Path.of("dump/textures/");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws IOException {
        CacheLibrary cache = new CacheLibrary("data/cache/", false, null);

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

                // Decode full Texture (like client does)
                TextureDefinition tex = new TextureDefinition(buf);

                Map<String, Object> json = new LinkedHashMap<>();
                json.put("archiveId", archiveId);
                json.put("spriteIds", tex.spriteIds);
                json.put("textureIds", tex.textureIds);

                try (FileWriter writer = new FileWriter(OUTPUT_DIR.resolve(archiveId + ".json").toFile())) {
                    GSON.toJson(json, writer);
                }

                // Extra: find specific sprite
                for (int spriteId : tex.spriteIds) {
                    if (spriteId == 485) {
                        System.out.println("✅ Texture " + archiveId + " uses spriteId 318");
                    }
                }

            } catch (Exception e) {
                System.err.println("⚠️ Failed to parse texture " + archiveId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Finished dumping.");
        System.out.println("  Archives processed: " + archiveCount);
        System.out.println("  Files processed:    " + fileCount);
    }

    // Stub: you’d port the client’s Buffer here
    static class Buffer {
        private final byte[] data;
        int pos = 0;
        Buffer(byte[] data) { this.data = data; }
        int readUnsignedByte() { return data[pos++] & 0xFF; }
        int readUnsignedShort() {
            return ((data[pos++] & 0xFF) << 8) | (data[pos++] & 0xFF);
        }
    }

    // Minimal TextureDefinition wrapper
    static class TextureDefinition {
        public final int[] spriteIds;
        public final int[] textureIds;

        TextureDefinition(Buffer buf) {
            // Here you should port the constructor logic from the client’s Texture class
            // For now, just demonstrate sprite collection:
            int opCount = buf.readUnsignedByte();
            List<Integer> sprites = new ArrayList<>();
            List<Integer> textures = new ArrayList<>();
            for (int i = 0; i < opCount; i++) {
                TextureOp op = TextureOp.decode(buf);
                if (op.getSpriteId() >= 0) sprites.add(op.getSpriteId());
                if (op.getTextureId() >= 0) textures.add(op.getTextureId());
                // skip child ops linking for now
            }
            this.spriteIds = sprites.stream().mapToInt(Integer::intValue).toArray();
            this.textureIds = textures.stream().mapToInt(Integer::intValue).toArray();
        }
    }

    // Minimal TextureOp stub
    static abstract class TextureOp {
        int getSpriteId() { return -1; }
        int getTextureId() { return -1; }

        static TextureOp decode(Buffer buf) {
            int opcode = buf.readUnsignedByte();
            if (opcode == 0) {
                int spriteId = buf.readUnsignedShort();
                return new TextureOpSprite(spriteId);
            } else if (opcode == 2) {
                int texId = buf.readUnsignedShort();
                return new TextureOpRef(texId);
            } else {
                // TODO: port all other opcodes from client
                return new TextureOp() {};
            }
        }
    }

    static class TextureOpSprite extends TextureOp {
        private final int spriteId;
        TextureOpSprite(int spriteId) { this.spriteId = spriteId; }
        @Override int getSpriteId() { return spriteId; }
    }

    static class TextureOpRef extends TextureOp {
        private final int texId;
        TextureOpRef(int texId) { this.texId = texId; }
        @Override int getTextureId() { return texId; }
    }
}
