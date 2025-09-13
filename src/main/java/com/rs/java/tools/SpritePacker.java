package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.io.IOException;

public class SpritePacker {

	// 6525, 6526, 6527 specbar green
	// 6531, 6532, 6533 specbar gray
	// 5600, 5601, 5602 specbar background

	// 4134 login screen

	private static final int INDEX = 8;

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		int archive = 3028;// spriteId
		int secondArchive = 3028;
		boolean LOOP = false;
		CacheLibrary toCache = new CacheLibrary("data/cache/", false, null);
		CacheLibrary fromCache = new CacheLibrary("data/cache639/", false, null);
		if (LOOP) {
			for (int i = archive; i <= secondArchive; i++) {
				toCache.index(INDEX).update();
				System.out.println("Updated index " + INDEX);
				Archive fromArchive = fromCache.index(INDEX).archive(i);
				Archive toArchive = toCache.index(INDEX).archive(i);
				for (File a : fromArchive.files()) {
					System.out.println(a);
					toArchive.add(a);
				}
				toCache.index(INDEX).update();
				System.out.println("Finished packing sprite: " + i + " from cache: " + fromArchive.getId()
						+ " to cache:" + toArchive.getId());
			}
		} else {
			toCache.index(INDEX).update();
			System.out.println("Updated index " + INDEX);
			Archive fromArchive = fromCache.index(INDEX).archive(archive);
			Archive toArchive = toCache.index(INDEX).archive(secondArchive);
			for (File a : fromArchive.files()) {
				System.out.println(a);
				toArchive.add(a);
			}
			toCache.index(INDEX).update();
			System.out.println("Finished packing sprite: " + archive + " to " + fromArchive.getId()
					+ " to 718 cache:" + toArchive.getId());
		}
	}
}
