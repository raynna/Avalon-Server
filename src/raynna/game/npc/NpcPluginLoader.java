package raynna.game.npc;

import raynna.util.Logger;
import raynna.data.rscm.Rscm;
import raynna.platform.PackageDiscovery;
import raynna.platform.PackageLayout;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NpcPluginLoader {

	public static final Map<Object, NpcPlugin> cachedNpcPlugins = new HashMap<>();

	public static NpcPlugin getPlugin(NPC npc) {
		NpcPlugin plugin = cachedNpcPlugins.get(npc.getId());
		if (plugin != null) {
			System.out.println("[NpcPluginLoader] getPlugin(" + npc.getName() + "/" + npc.getId() + ") → found by id → " + plugin.getClass().getSimpleName());
			return plugin;
		}

		String name = npc.getName().toLowerCase();
		for (Map.Entry<Object, NpcPlugin> entry : cachedNpcPlugins.entrySet()) {
			Object key = entry.getKey();
			if (key instanceof String str && name.contains(str.toLowerCase())) {
				System.out.println("[NpcPluginLoader] getPlugin(" + npc.getName() + "/" + npc.getId() + ") → found by name key='" + str + "' → " + entry.getValue().getClass().getSimpleName());
				return entry.getValue();
			}
		}
		return null;
	}

	public static void init() {
        try {
            Set<Class<?>> processedClasses = new HashSet<>();

            for (String pluginFolder : PackageLayout.npcPluginPackages()) {
                Class<?>[] classes = PackageDiscovery.getClasses(java.util.List.of(pluginFolder));

                for (Class<?> c : classes) {
					if (c.isAnonymousClass() || processedClasses.contains(c))
						continue;

					Object o = c.getDeclaredConstructor().newInstance();
					if (!(o instanceof NpcPlugin plugin))
						continue;


					for (Object key : plugin.getKeys()) {
						if (key instanceof Integer id) {
							cachedNpcPlugins.put(id, plugin);

						} else if (key instanceof String keyStr) {
							if (keyStr.startsWith("npc.")) {
								int resolved = Rscm.lookup(keyStr);
								cachedNpcPlugins.put(resolved, plugin);

							} else if (keyStr.startsWith("npc_group.")) {
								java.util.List<Integer> ids = Rscm.lookupList(keyStr);
								for (int id : ids) {
									cachedNpcPlugins.put(id, plugin);
								}

							} else {
								cachedNpcPlugins.put(keyStr.toLowerCase(), plugin);
							}

						} else {
							System.out.println("[NpcPluginLoader]   INVALID key type " + (key == null ? "null" : key.getClass().getName()) + " for " + plugin.getClass().getName());
						}
					}

					processedClasses.add(c);
				}
			}

			System.out.println("[NpcPluginLoader]: " + processedClasses.size() + " plugins loaded. Total registered keys: " + cachedNpcPlugins.size());

		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			Logger.handle(e);
		}
	}
}
