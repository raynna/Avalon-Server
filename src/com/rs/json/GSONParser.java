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
		} catch (IOException e) {
			e.printStackTrace();
			Logger.log("Load", e);
		}
		return null;
	}

	public static void save(Object src, String dir, Type type) {
		try (Writer writer = Files.newBufferedWriter(Paths.get(dir))) {
			writer.write(GSON.toJson(src, type));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}