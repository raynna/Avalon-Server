package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.io.IOException;

public class SpritePacker {

	private static final int INDEX = 8; // sprites index

	public static void main(String[] args) throws IOException {
		int fromArchive = 20318; // spriteId in donor cache
		int toArchive = 318;     // new spriteId in destination cache

		CacheLibrary toCache = new CacheLibrary("data/cache/", false, null);
		CacheLibrary fromCache = new CacheLibrary("data/onyxcache/cache/", false, null);

		copySprite(fromCache, toCache, fromArchive, toArchive);

		// Save changes
		toCache.index(INDEX).update();
		System.out.println("✅ Finished packing sprite " + fromArchive + " → " + toArchive);
	}

	private static void copySprite(CacheLibrary fromCache, CacheLibrary toCache, int fromId, int toId) {
		Archive fromArchive = fromCache.index(INDEX).archive(fromId);
		if (fromArchive == null) {
			System.err.println("❌ Missing source sprite archive " + fromId);
			return;
		}

		// Remove existing destination and re-add
		toCache.index(INDEX).remove(toId);
		Archive destArchive = toCache.index(INDEX).add(toId);

		for (File file : fromArchive.files()) {
			if (file == null) continue;
			byte[] data = file.getData();
			if (data == null) continue;
			destArchive.add(file.getId(), data);
		}

		System.out.println("✅ Copied sprite " + fromId + " into destination archive " + toId);
	}
}
