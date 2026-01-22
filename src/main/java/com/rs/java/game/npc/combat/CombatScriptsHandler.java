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

				if (c.isEnum() || c.isAnonymousClass() || c.isMemberClass() || c.isInterface())
					continue;

				if (!CombatScript.class.isAssignableFrom(c))
					continue;

				CombatScript script = (CombatScript) c.getDeclaredConstructor().newInstance();

				for (Object key : script.getKeys())
					cachedCombatScripts.put(key, script);
			}
		} catch (Exception e) {
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
