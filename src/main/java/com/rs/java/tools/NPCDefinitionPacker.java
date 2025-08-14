package com.rs.java.tools;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.io.IOException;

public class NPCDefinitionPacker {

	private static int NPC_TO_PACK = 0;

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		CacheLibrary library = new CacheLibrary("data/cache/", false, null);
		library.index(18).update();
		Archive archive = library.index(18).archive(NPC_TO_PACK >>> 134238215);
		File file = archive.file(NPC_TO_PACK & 0x7f);
		for (byte files : file.getData())
			System.out.println(files);
		System.out.println(file);
		System.out.println(file.getData());
		library.index(18).update();
		System.out.println("Finished");
	}
}
