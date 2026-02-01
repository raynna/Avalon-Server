package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

public class InterfaceCopyTool {

	private static final String CACHE_PATH = "data/cache/";
	private static final String CACHE_PATH2 = "data/cache/";

	public static void main(String[] args) {

		int sourceInterface = 3051;       //interface from other cache
		int destinationInterface = 3050;  //interface in your cache

		try {
			CacheLibrary cache = new CacheLibrary(CACHE_PATH, false, null);
			CacheLibrary fromCache = new CacheLibrary(CACHE_PATH2, false, null);

			cache.index(3).update();
			fromCache.index(3).update();

			System.out.println("Index 3 updated in both caches.");

			Archive destArchive = cache.index(3).archive(destinationInterface);
			if (destArchive != null) {
				System.out.println("Clearing interface: " + destinationInterface);
				for (File f : destArchive.files()) {
					if (f != null) {
						destArchive.remove(f.getId());
					}
				}
			}

			Archive sourceArchive = fromCache.index(3).archive(sourceInterface);

			if (sourceArchive == null) {
				System.out.println("Source interface does not exist in external cache!");
				return;
			}

			Archive newArchive = cache.index(3).add(destinationInterface);

			for (File f : sourceArchive.files()) {
				if (f == null)
					continue;

				newArchive.add(new File(
						f.getId(),
						f.getData()
				));
			}

			cache.index(3).update();

			System.out.println("Successfully copied interface "
					+ sourceInterface + " from external cache → "
					+ destinationInterface + " in your cache.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
