package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;

public class TextureTrimmer {

	private static final int INDEX = 9;
	private static final int MAX_TEXTURE_ID = 2276;

	public static void main(String[] args) throws Exception {
		CacheLibrary cache = new CacheLibrary("data/cache/", false, null);
		Index index = cache.index(INDEX);

		int removed = 0;

		for (Archive archive : index.archives()) {
			if (archive != null && archive.getId() > MAX_TEXTURE_ID) {
				index.remove(archive.getId());
				removed++;
				System.out.println("Removed texture archive: " + archive.getId());
			}
		}

		index.update();
		System.out.println("Done. Removed " + removed + " texture archives.");
	}
}
