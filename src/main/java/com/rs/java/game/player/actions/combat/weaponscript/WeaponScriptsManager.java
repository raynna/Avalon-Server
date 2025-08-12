package com.rs.java.game.player.actions.combat.weaponscript;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;

/**
 * @author -Andreas 11 feb. 2020 12:23:00
 * @project 1. Avalon
 * 
 */

public class WeaponScriptsManager {

	public static final HashMap<Object, WeaponScript> weaponScripts = new HashMap<Object, WeaponScript>();

	public static void init() {
		try {
			Class<?>[] classes = Utils.getClasses("com.rs.java.game.player.actions.combat.weaponscript.impl");
			for (Class<?> c : classes) {
				if (c.isAnonymousClass())
					continue;
				Object o = c.getDeclaredConstructor().newInstance();
				if (!(o instanceof WeaponScript))
					continue;
				WeaponScript script = (WeaponScript) o;
				for (Object key : script.getKeys())
					weaponScripts.put(key, script);
			}
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 ClassNotFoundException | IOException e) {
			Logger.handle(e);
		}
	}

}
