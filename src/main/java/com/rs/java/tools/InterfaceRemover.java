package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.io.IOException;

public class InterfaceRemover {

	private static int INTERFACE_TO_REMOVE = 3005;

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		CacheLibrary library = new CacheLibrary("data/cache/", false, null);
		Archive toRemove = library.index(3).archive(INTERFACE_TO_REMOVE);
		System.out.println("Removed archive: " + INTERFACE_TO_REMOVE + " containing...");
		for (File files : toRemove.files()) {
			if (files == null) {
				library.index(3).remove(INTERFACE_TO_REMOVE);
				System.out.println("Files were null, removing archive!");
				continue;
			}
			System.out.println(files);
			toRemove.remove(files.getId());
		}
		library.index(3).update();
		System.out.println("Finished");
	}
}
