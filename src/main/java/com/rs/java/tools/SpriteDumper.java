package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import com.rs.core.cache.SpriteLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class SpriteDumper {

    private static final int INDEX = 8;

    public static void main(String[] args) throws IOException {
        CacheLibrary cache = new CacheLibrary("data/cache/", false, null);

        java.io.File outDir = new java.io.File("dump/sprites/");
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        int archiveCount = 0, fileCount = 0, imageCount = 0;

        for (Archive archive : cache.index(INDEX).archives()) {
            if (archive == null) continue;
            archiveCount++;
            System.out.println("-------------------------------------------------");
            System.out.println("Archive " + archive.getId());

            // Always fetch file 0 directly, don’t trust archive.files() stubs
            File file = cache.index(INDEX).archive(archive.getId()).file(0);
            if (file == null) {
                System.out.println("  No file 0 in archive " + archive.getId());
                continue;
            }

            byte[] data = file.getData();
            if (data == null) {
                System.out.println("  File 0 has no data in archive " + archive.getId());
                continue;
            }

            fileCount++;
            System.out.println("  File 0 length=" + data.length);

            try {
                SpriteLoader.decode(data);

                System.out.println("    -> Frames=" + SpriteLoader.frames
                        + " width=" + SpriteLoader.width
                        + " height=" + SpriteLoader.height
                        + " paletteSize=" + (SpriteLoader.palette != null ? SpriteLoader.palette.length : -1));

                List<BufferedImage> images = SpriteLoader.toBufferedImages();
                System.out.println("    -> Decoded " + images.size() + " images.");

                int idx = 0;
                for (BufferedImage image : images) {
                    java.io.File out = new java.io.File(outDir,
                            archive.getId() + "_" + idx++ + ".png");
                    ImageIO.write(image, "png", out);
                    System.out.println("       Saved -> " + out.getAbsolutePath());
                    imageCount++;
                }

                SpriteLoader.clear();
            } catch (Exception e) {
                System.err.println("⚠️ Failed to decode sprite archive "
                        + archive.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("-------------------------------------------------");
        System.out.println("Finished dumping.");
        System.out.println("  Archives processed: " + archiveCount);
        System.out.println("  Files processed:    " + fileCount);
        System.out.println("  Images dumped:      " + imageCount);
    }
}
