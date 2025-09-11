package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.io.IOException;

public class TexturePacker {

	private static int TEXTURE_INDEX = 9;

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		int textureId = 318; // spriteId
		int loop = -1;
		CacheLibrary toCache = new CacheLibrary("data/cache/", false, null);
		CacheLibrary fromCache = new CacheLibrary("data/onyxcache/cache/", false, null);

		Index toIndex = toCache.index(TEXTURE_INDEX);
		Index fromIndex = fromCache.index(TEXTURE_INDEX);

		// Show archive count in both caches
		int toCount = toIndex.archives().length;
		int fromCount = fromIndex.archives().length;
		System.out.println("Texture index " + TEXTURE_INDEX + " contains:");
		System.out.println(" - To cache:   " + toCount + " archives");
		System.out.println(" - From cache: " + fromCount + " archives");

		if (loop != -1) {
			for (int i = textureId; i <= loop; i++) {
				copyArchive(fromIndex, toIndex, i, toCache);
			}
		} else {
			copyArchive(fromIndex, toIndex, textureId, toCache);
		}
	}

	private static void copyArchive(Index fromIndex, Index toIndex, int archiveId, CacheLibrary toCache) throws IOException {
		toIndex.update();
		System.out.println("Updated index " + TEXTURE_INDEX);

		Archive fromArchive = fromIndex.archive(archiveId);
		Archive toArchive = toIndex.archive(archiveId);

		if (fromArchive == null) {
			System.out.println("Archive " + archiveId + " not found in fromCache.");
			return;
		}

		for (File a : fromArchive.files()) {
			System.out.println("Copying file: " + a + " (size=" + a.getData().length + ")");
			toArchive.add(a);
		}

		toIndex.update();

		System.out.println("Finished packing texture: " + archiveId
				+ " from cache: " + fromArchive.getId()
				+ " to cache: " + toArchive.getId());

		// Show progress info
		int current = archiveId;
		int total = fromIndex.archives().length;
		System.out.println("Progress: " + (current + 1) + " / " + total + " archives processed.");
	}
}
