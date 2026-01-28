package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.io.IOException;

public class InterfaceReplacer {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		int interfaceId = 320;
		CacheLibrary cache1 = new CacheLibrary("data/cache/", false, null);
		CacheLibrary cache2 = new CacheLibrary("data/cache/", false, null);
		cache1.index(3).update();
		System.out.println("Updated index 3");

		Archive toRemove = cache1.index(3).archive(interfaceId);
		System.out.println("Removed archive: " + interfaceId + " containing...");
		for (File files : toRemove.files()) {
			if (files == null) {
				cache1.index(3).remove(interfaceId);
				System.out.println("Files were null, removing archive!");
				continue;
			}
			System.out.println(files);
			toRemove.remove(files.getId());
		}
		cache1.index(3).update();



		Archive fromArchive = cache2.index(3).archive(interfaceId);
		Archive toArchive = cache1.index(3).archive(interfaceId);
		for (File a : fromArchive.files()) {
			System.out.println(a);
			toArchive.add(a);
		}
		cache1.index(3).update();
		System.out.println("Finished packing all component.rscm from:" + fromArchive.getId() + " to:" + toArchive.getId());
	}
}
