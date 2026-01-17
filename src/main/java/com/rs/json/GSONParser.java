package com.rs.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.lang.reflect.Type;
import java.io.StringReader;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

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
		GSON = new GsonBuilder().setPrettyPrinting()
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

			// DEBUG: Simple JSON logging
			Logger.log("GSONDebug", "Generated JSON length: " + json.length());
			if (json.length() > 200) {
				Logger.log("GSONDebug", "First 200 chars: " + json.substring(0, 200));
			} else {
				Logger.log("GSONDebug", "Full JSON: " + json);
			}

			try (Writer writer = Files.newBufferedWriter(Paths.get(tmpFile))) {
				writer.write(json);
			}

			// DEBUG: Try to parse with more detailed error tracking
			try (Reader reader = Files.newBufferedReader(Paths.get(tmpFile))) {
				// Read the file content
				StringBuilder content = new StringBuilder();
				int character;
				while ((character = reader.read()) != -1) {
					content.append((char) character);
				}
				String jsonContent = content.toString();

				JsonReader jsonReader = new JsonReader(new StringReader(jsonContent));
				jsonReader.setLenient(true); // Allow reading to continue

				try {
					GSON.fromJson(jsonReader, type);
				} catch (JsonSyntaxException jse) {
					// Get the position where parsing failed
					Logger.log("GSONDebug", "JSON Parse Error: " + jse.getMessage());

					// Log the problematic section
					int errorPos = Math.max(0, jsonContent.length() - 500);
					Logger.log("GSONDebug", "Last 500 chars of JSON: " +
							jsonContent.substring(errorPos));

					throw jse;
				}
			}

			if (Files.exists(Paths.get(dir))) {
				Files.copy(Paths.get(dir), Paths.get(backupFile), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			}

			Files.move(Paths.get(tmpFile), Paths.get(dir), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

		} catch (Exception e) {
			e.printStackTrace();
			Logger.log("Save", e);

			// DEBUG: Add more specific error info
			Logger.log("GSONDebug", "Error saving to: " + dir);
			Logger.log("GSONDebug", "Object type: " + (src != null ? src.getClass().getName() : "null"));
			Logger.log("GSONDebug", "Target type: " + type.getTypeName());

			// Check if it's the Entity class issue
			if (e.getMessage() != null && e.getMessage().contains("Entity")) {
				Logger.log("GSONDebug", "ENTITY CLASS DETECTED IN ERROR");
				Logger.log("GSONDebug", "PROBLEM: Player extends Entity and Entity doesn't have no-args constructor");
				Logger.log("GSONDebug", "SOLUTION: Add no-args constructor to Entity class");
			}

			//cleanup temp file
			try { Files.deleteIfExists(Paths.get(tmpFile)); } catch (IOException ignored) {}
		}
	}

	// Add a simple debug method to test serialization
	public static void debugSave(Object src, Type type) {
		try {
			String json = GSON.toJson(src, type);
			Logger.log("GSONDebug", "✓ Serialization successful for: " + src.getClass().getName());
			Logger.log("GSONDebug", "JSON length: " + json.length());
		} catch (Exception e) {
			Logger.log("GSONDebug", "✗ Serialization FAILED for: " + src.getClass().getName());
			Logger.log("GSONDebug", "Error: " + e.getMessage());

			// Check specific Gson error
			if (e.getMessage() != null) {
				if (e.getMessage().contains("no-args constructor")) {
					Logger.log("GSONDebug", "PROBLEM: Class needs no-args constructor");
					Logger.log("GSONDebug", "This is the Entity class in Player hierarchy");
				}
				if (e.getMessage().contains("Entity")) {
					Logger.log("GSONDebug", "PROBLEM IS IN Entity CLASS");
				}
			}
		}
	}

}