package com.rs.java.game.objects;

import com.rs.java.game.WorldObject;
import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;
import com.rs.kotlin.rscm.Rscm;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectPluginLoader {

	public static final Map<Object, ObjectPlugin> cachedObjectPlugins = new HashMap<>();

	public static ObjectPlugin getPlugin(WorldObject object) {

		// Fast lookup by ID
		ObjectPlugin plugin = cachedObjectPlugins.get(object.getId());
		if (plugin != null)
			return plugin;

		// Name fallback
		String name = object.getName().toLowerCase();

		for (Map.Entry<Object, ObjectPlugin> entry : cachedObjectPlugins.entrySet()) {
			Object key = entry.getKey();

			if (key instanceof String str && name.contains(str)) {
				return entry.getValue();
			}
		}

		return null;
	}

	public static void init() {
		try {

			String pluginFolder = "com.rs.java.game.objects.plugins";

			Set<Class<?>> processedClasses = new HashSet<>();

			Class<?>[] classes = Utils.getClasses(pluginFolder);

			for (Class<?> c : classes) {

				if (c.isAnonymousClass() || processedClasses.contains(c))
					continue;

				Object o = c.getDeclaredConstructor().newInstance();

				if (!(o instanceof ObjectPlugin plugin))
					continue;

				for (Object key : plugin.getKeys()) {

					if (key instanceof Integer id) {

						cachedObjectPlugins.put(id, plugin);

					}
					else if (key instanceof String keyStr) {

						if (keyStr.startsWith("object.")) {

							cachedObjectPlugins.put(Rscm.lookup(keyStr), plugin);

						}
						else if (keyStr.startsWith("object_group.")) {

							for (int id : Rscm.lookupList(keyStr)) {
								cachedObjectPlugins.put(id, plugin);
							}

						}
						else {

							// fallback for name contains
							cachedObjectPlugins.put(keyStr.toLowerCase(), plugin);
						}
					}
					else {

						System.out.println("Invalid key for " + plugin.getClass().getName());
					}
				}

				processedClasses.add(c);
			}

			System.out.println("[ObjectPluginLoader]: " + processedClasses.size() + " plugins were loaded.");

		} catch (InstantiationException | IllegalAccessException |
				 NoSuchMethodException | InvocationTargetException e) {

			Logger.handle(e);

		} catch (IOException | ClassNotFoundException e) {

			throw new RuntimeException(e);
		}
	}
}