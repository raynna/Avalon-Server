package com.rs.core.cache;

import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

public final class SpriteLoader {

    // decoded fields
    public static int frames;
    public static int width;
    public static int height;
    public static int[] xOffsets;
    public static int[] yOffsets;
    public static int[] innerWidths;
    public static int[] innerHeights;
    public static int[] palette;
    public static byte[][] pixels;
    public static byte[][] alphas;
    public static boolean[] hasAlpha;

    private SpriteLoader() {}

    /**
     * Decode sprite container bytes into static arrays.
     */
    public static void decode(byte[] data) {
        Buffer buf = new Buffer(data);

        buf.position = data.length - 2;
        frames = buf.readUnsignedShort();

        hasAlpha = new boolean[frames];
        alphas = new byte[frames][];
        xOffsets = new int[frames];
        yOffsets = new int[frames];
        innerWidths = new int[frames];
        innerHeights = new int[frames];
        pixels = new byte[frames][];

        buf.position = data.length - frames * 8 - 7;
        width = buf.readUnsignedShort();
        height = buf.readUnsignedShort();
        int paletteSize = buf.readUnsignedByte() + 1;

        for (int i = 0; i < frames; i++) xOffsets[i] = buf.readUnsignedShort();
        for (int i = 0; i < frames; i++) yOffsets[i] = buf.readUnsignedShort();
        for (int i = 0; i < frames; i++) innerWidths[i] = buf.readUnsignedShort();
        for (int i = 0; i < frames; i++) innerHeights[i] = buf.readUnsignedShort();

        buf.position = data.length - frames * 8 - (paletteSize - 1) * 3 - 7;
        palette = new int[paletteSize];
        for (int i = 1; i < paletteSize; i++) {
            palette[i] = buf.read24BitInt();
            if (palette[i] == 0) palette[i] = 1; // avoid transparency bug
        }

        buf.position = 0;
        for (int frame = 0; frame < frames; frame++) {
            int w = innerWidths[frame];
            int h = innerHeights[frame];
            int len = w * h;

            byte[] framePixels = new byte[len];
            pixels[frame] = framePixels;
            byte[] frameAlpha = new byte[len];
            alphas[frame] = frameAlpha;

            boolean anyAlpha = false;

            int flags = buf.readUnsignedByte();
            if ((flags & 0x1) == 0) {
                // row-major
                for (int j = 0; j < len; j++) {
                    framePixels[j] = buf.readByte();
                }
                if ((flags & 0x2) != 0) {
                    for (int j = 0; j < len; j++) {
                        byte a = frameAlpha[j] = buf.readByte();
                        if (a != -1) anyAlpha = true;
                    }
                }
            } else {
                // column-major
                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        framePixels[x + y * w] = buf.readByte();
                    }
                }
                if ((flags & 0x2) != 0) {
                    for (int x = 0; x < w; x++) {
                        for (int y = 0; y < h; y++) {
                            byte a = frameAlpha[x + y * w] = buf.readByte();
                            if (a != -1) anyAlpha = true;
                        }
                    }
                }
            }
            hasAlpha[frame] = anyAlpha;
        }
    }

    /**
     * Convert decoded frames into Java images.
     */
    public static List<BufferedImage> toBufferedImages() {
        List<BufferedImage> list = new ArrayList<>();
        for (int i = 0; i < frames; i++) {
            int w = innerWidths[i];
            int h = innerHeights[i];

            if (w <= 0 || h <= 0) {
                System.out.println("    Skipping frame " + i + " with invalid size " + w + "x" + h);
                continue;
            }

            int[] argb = new int[w * h];

            byte[] srcPixels = pixels[i];
            byte[] srcAlpha = alphas[i];
            boolean useAlpha = hasAlpha[i];

            for (int j = 0; j < w * h; j++) {
                int color = palette[srcPixels[j] & 0xFF];
                int a = useAlpha ? (srcAlpha[j] & 0xFF) : 0xFF;
                argb[j] = (a << 24) | color;
            }

            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            img.setRGB(0, 0, w, h, argb, 0, w);
            list.add(img);
        }
        return list;
    }


    public static void clear() {
        xOffsets = null;
        yOffsets = null;
        innerWidths = null;
        innerHeights = null;
        palette = null;
        pixels = null;
        alphas = null;
        hasAlpha = null;
    }
}
