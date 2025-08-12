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

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		int archive = 3028;// spriteId
		int secondArchive = 3028;
		boolean LOOP = false;
		CacheLibrary cache718 = new CacheLibrary("data/cache/", false, null);
		CacheLibrary cache667 = new CacheLibrary("data/cache639/", false, null);
		if (LOOP) {
			for (int i = archive; i <= secondArchive; i++) {
				cache718.index(8).update();
				System.out.println("Updated index 8");
				Archive fromArchive = cache667.index(8).archive(i);
				Archive toArchive = cache718.index(8).archive(i);
				for (File a : fromArchive.files()) {
					System.out.println(a);
					toArchive.add(a);
				}
				cache718.index(8).update();
				System.out.println("Finished packing sprite: " + i + " from 667 cache: " + fromArchive.getId()
						+ " to 718 cache:" + toArchive.getId());
			}
		} else {
			cache718.index(8).update();
			System.out.println("Updated index 8");
			Archive fromArchive = cache667.index(8).archive(archive);
			Archive toArchive = cache718.index(8).archive(secondArchive);
			for (File a : fromArchive.files()) {
				System.out.println(a);
				toArchive.add(a);
			}
			cache718.index(8).update();
			System.out.println("Finished packing sprite: " + archive + " from 667 cache: " + fromArchive.getId()
					+ " to 718 cache:" + toArchive.getId());
		}
	}
}
