package com.rs.java.game.npc.combat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;

public class CombatScriptsHandler {

	private static final HashMap<Object, CombatScript> cachedCombatScripts = new HashMap<Object, CombatScript>();
	private static final CombatScript DEFAULT_SCRIPT = new Default();
	protected transient Player player;

	public static void init() {
		try {
			Class<?>[] classes = Utils.getClasses("com.rs.java.game.npc.combat.impl");
			for (Class<?> c : classes) {
				if (c.isAnonymousClass())
					continue;
				Object o = c.getDeclaredConstructor().newInstance();
				if (!(o instanceof CombatScript script))
					continue;
                for (Object key : script.getKeys())
					cachedCombatScripts.put(key, script);
			}
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 ClassNotFoundException | IOException e) {
			Logger.handle(e);
		}
	}


	public static int specialAttack(final NPC npc, final Entity target) {
		CombatScript script = cachedCombatScripts.get(npc.getId());
		if (script == null) {
			script = cachedCombatScripts.get(npc.getDefinitions().name);
			if (script == null)
				script = DEFAULT_SCRIPT;
		}
		return script.attack(npc, target);
	}
}
