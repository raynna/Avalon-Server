package com.rs.java.game.objects;

import com.rs.java.game.WorldObject;
import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectPluginLoader {

	public static final HashMap<Object, ObjectPlugin> cachedObjectPlugins = new HashMap<Object, ObjectPlugin>();

	public static ObjectPlugin getPlugin(WorldObject object) {
		ObjectPlugin plugin = cachedObjectPlugins.getOrDefault(object.getId(), cachedObjectPlugins.get(object.getName()));
		if (plugin != null) {
			System.out.println("[ObjectPluginLoader] "+object.getName()+"("+object.getId()+"): plugin was found by Id.");
			return plugin;
		}
        for (Map.Entry<Object, ObjectPlugin> entry : cachedObjectPlugins.entrySet()) {
            Object[] keys = entry.getValue().getKeys();
            for (Object key : keys) {
                if (key instanceof String && object.getName().toLowerCase().contains(((String) key).toLowerCase())) {
                    plugin = entry.getValue();
                    System.out.println("[ObjectPluginLoader] " + object.getName() + "(" + object.getId() + "): Found plugin by name");
                    return plugin;
                }
            }
        }
        System.out.println("[ObjectPluginLoader] "+object.getName()+"("+object.getId()+"): Found no plugin for this object.");
		return null;
	}

	public static void init() {
		try {
			String[] pluginFolders = {"com.rs.java.game.objects.plugins"};
			Set<Class<?>> processedClasses = new HashSet<>();
			for (String pluginFolder : pluginFolders) {
				Class<?>[] classes = Utils.getClasses(pluginFolder);
				for (Class<?> c : classes) {
					if (c.isAnonymousClass() || processedClasses.contains(c))
						continue;
					Object o = c.getDeclaredConstructor().newInstance();
					if (!(o instanceof ObjectPlugin plugin))
						continue;
					for (Object key : plugin.getKeys())
						cachedObjectPlugins.put(key, plugin);
					processedClasses.add(c);
				}
			}
			System.out.println("[ObjectPluginLoader]: " + processedClasses.size() + " plugins were loaded.");
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 ClassNotFoundException | IOException e) {
			Logger.handle(e);
		}
	}

}
