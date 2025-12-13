package com.rs.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.lang.reflect.Type;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Logger;

/**
* @author Melvin 27 jan. 2020
* 
*/

public class GSONParser {

	private static Gson GSON;

	static {
		GSON = new GsonBuilder()
				.setPrettyPrinting()
				.disableInnerClassSerialization()
				.enableComplexMapKeySerialization()
				.setDateFormat(DateFormat.LONG)
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
				.registerTypeAdapter(ItemMetadata.class, new ItemMetadataDeserializer())
				.registerTypeAdapter(ItemMetadata.class, new ItemMetadataSerializer())
				.create();
	}

	public static Player load(String dir, Type type) {
		try (Reader reader = Files.newBufferedReader(Paths.get(dir))) {
			return GSON.fromJson(reader, type);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.log("Load", e);

			String backupFile = dir + ".bak";
			if (Files.exists(Paths.get(backupFile))) {
				try (Reader reader = Files.newBufferedReader(Paths.get(backupFile))) {
					Logger.log("Load", "Recovered from backup for " + dir);
					return GSON.fromJson(reader, type);
				} catch (Exception ex) {
					ex.printStackTrace();
					Logger.log("Load", "Backup load failed for " + dir);
				}
			}
		}
		return null;
	}

	public static void save(Object src, String dir, Type type) {
		String tmpFile = dir + ".tmp";
		String backupFile = dir + ".bak";
		try {
			String json = GSON.toJson(src, type);

			try (Writer writer = Files.newBufferedWriter(Paths.get(tmpFile))) {
				writer.write(json);
			}

			try (Reader reader = Files.newBufferedReader(Paths.get(tmpFile))) {
				GSON.fromJson(reader, type);
			}

			if (Files.exists(Paths.get(dir))) {
				Files.copy(Paths.get(dir), Paths.get(backupFile), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			}

			Files.move(Paths.get(tmpFile), Paths.get(dir), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

		} catch (Exception e) {
			e.printStackTrace();
			Logger.log("Save", e);

			//cleanup temp file
			try { Files.deleteIfExists(Paths.get(tmpFile)); } catch (IOException ignored) {}
		}
	}

}