package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.io.IOException;

public class SpritePacker {

	private static final int INDEX = 8; // sprite index

	public static void main(String[] args) throws IOException {

		int fromSprite = 675;
		int toSprite = 10247;
		//fixed equipment tab icon = 1822
		//old ugly equipment tab icon = 8669
		CacheLibrary currentCache = new CacheLibrary("data/cache/", false, null);
		CacheLibrary fromCache = new CacheLibrary("data/onyxcache/cache/", false, null);

		currentCache.index(INDEX).update();
		fromCache.index(INDEX).update();

		if (currentCache.index(INDEX).archive(toSprite) != null) {
			currentCache.index(INDEX).remove(toSprite);
			System.out.println("Removed sprite " + toSprite + " from cache100");
		}

		Archive fromArchive = fromCache.index(INDEX).archive(fromSprite);
		if (fromArchive == null) {
			throw new RuntimeException("Sprite " + fromSprite + " not found in cache200");
		}

		Archive toArchive = currentCache.index(INDEX).add(toSprite);

		// Copy files
		for (File f : fromArchive.files()) {
			if (f == null) continue;

			byte[] data = f.getData().clone(); // clone for safety
			toArchive.add(new File(f.getId(), data));
		}

		currentCache.index(INDEX).update();

		System.out.println("✅ Copied sprite " + fromSprite + " (onyxCache) → " + toSprite + " (currentCache)");
	}

}
