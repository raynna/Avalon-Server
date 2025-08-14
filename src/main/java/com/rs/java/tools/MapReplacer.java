package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.io.IOException;

public class MapReplacer {

	// 6525, 6526, 6527 specbar green
	// 6531, 6532, 6533 specbar gray
	// 5600, 5601, 5602 specbar background

	// 4134 login screen

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		CacheLibrary cache718 = new CacheLibrary("data/cache/", false, null);
		CacheLibrary cache667 = new CacheLibrary("data/cache639/", false, null);
		cache718.index(5).update();
		System.out.println("Updated index 5");
		int regionX = 49;
		int regionY = 54;
		int archiveId = cache667.index(5).archiveId("m" + regionX + "_" + regionY);
		Archive fromArchive = cache667.index(5).archive(archiveId);
		Archive toArchive = cache718.index(5).archive(archiveId);
		for (File a : fromArchive.files()) {
			if (a == null) {
				System.out.println("failed file: " + archiveId);
				continue;
			}
			System.out.println("Archive: " + archiveId);
			System.out.println(a);
			toArchive.remove(a.getId());
			toArchive.add(a);
		}
		cache718.index(5).update();
		System.out.println("Updated index 5");
		System.out.println("Finished packing map: 639 cache: " + fromArchive.getId()
				+ " to 718 cache:" + toArchive.getId());
	}

}
