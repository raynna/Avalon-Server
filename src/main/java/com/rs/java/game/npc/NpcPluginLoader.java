package com.rs.java.game.npc;

import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NpcPluginLoader {

	public static final HashMap<Object, NpcPlugin> cachedNpcPlugins = new HashMap<Object, NpcPlugin>();

	public static NpcPlugin getPlugin(NPC npc) {
		NpcPlugin plugin = cachedNpcPlugins.getOrDefault(npc.getId(), cachedNpcPlugins.get(npc.getName()));
		if (plugin != null) {
			System.out.println("[NpcPluginManager] "+npc.getName()+"("+npc.getId()+"): plugin was found by Id.");
			return plugin;
		}
        for (Map.Entry<Object, NpcPlugin> entry : cachedNpcPlugins.entrySet()) {
            Object[] keys = entry.getValue().getKeys();
            for (Object key : keys) {
                if (key instanceof String && npc.getName().toLowerCase().contains(((String) key).toLowerCase())) {
                    plugin = entry.getValue();
                    System.out.println("[NpcPluginManager] " + npc.getName() + "(" + npc.getId() + "): Found plugin by name");
                    return plugin;
                }
            }
        }
        System.out.println("[NpcPluginManager] "+npc.getName()+"("+npc.getId()+"): Found no plugin for this npc.");
		return null;
	}

	public static void init() {
		try {
			String[] pluginFolders = {"com.rs.java.game.npc.plugins"};
			Set<Class<?>> processedClasses = new HashSet<>();
			for (String pluginFolder : pluginFolders) {
				Class<?>[] classes = Utils.getClasses(pluginFolder);
				for (Class<?> c : classes) {
					if (c.isAnonymousClass() || processedClasses.contains(c))
						continue;
					Object o = c.getDeclaredConstructor().newInstance();
					if (!(o instanceof NpcPlugin plugin))
						continue;
					for (Object key : plugin.getKeys())
						cachedNpcPlugins.put(key, plugin);
					processedClasses.add(c);
				}
			}
			System.out.println("[NpcPluginManager]: " + processedClasses.size() + " plugins were loaded.");
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 ClassNotFoundException | IOException e) {
			Logger.handle(e);
		}
	}

}
