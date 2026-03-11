package com.rs.java.game.item;

import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;
import com.rs.kotlin.rscm.Rscm;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemPluginLoader {

    public static final Map<Object, ItemPlugin> cachedItemPlugins = new HashMap<Object, ItemPlugin>();

    public static ItemPlugin getPlugin(Item item) {

        ItemPlugin plugin = cachedItemPlugins.get(item.getId());
        if (plugin != null)
            return plugin;

        String name = item.getName().toLowerCase();

        for (Map.Entry<Object, ItemPlugin> entry : cachedItemPlugins.entrySet()) {
            Object key = entry.getKey();

            if (key instanceof String str && name.contains(str)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public static void init() {
        try {
            String[] pluginFolders = {
                    "com.rs.java.game.item.plugins",
                    "com.rs.java.game.item.plugins.weapons",
                    "com.rs.java.game.item.plugins.misc",
                    "com.rs.java.game.item.plugins.tools",
                    "com.rs.java.game.item.plugins.summoning",
                    "com.rs.java.game.item.plugins.skilling",
                    "com.rs.java.game.item.plugins.minigames"
            };
            Set<Class<?>> processedClasses = new HashSet<>();
            for (String pluginFolder : pluginFolders) {
                Class<?>[] classes = Utils.getClasses(pluginFolder);
                for (Class<?> c : classes) {
                    if (c.isAnonymousClass() || processedClasses.contains(c)) {
                        continue;
                    }
                    Object o = c.getDeclaredConstructor().newInstance();
                    if (!(o instanceof ItemPlugin plugin)) {
                        continue;
                    }
                    for (Object key : plugin.getKeys()) {

                        if (key instanceof Integer id) {
                            cachedItemPlugins.put(id, plugin);
                        }

                        else if (key instanceof String keyStr) {

                            if (keyStr.startsWith("item.")) {
                                cachedItemPlugins.put(Rscm.lookup(keyStr), plugin);
                            }

                            else if (keyStr.startsWith("item_group.")) {
                                for (int id : Rscm.lookupList(keyStr)) {
                                    cachedItemPlugins.put(id, plugin);
                                }
                            }

                            else {
                                // fallback for name contains later
                                cachedItemPlugins.put(keyStr.toLowerCase(), plugin);
                            }

                        } else {
                            System.out.println("Invalid key for " + plugin.getClass().getName());
                        }
                    }
                    processedClasses.add(c);
                }
            }
            System.out.println("[ItemPluginManager]: " + processedClasses.size() + " plugins were loaded.");
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Logger.handle(e);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
