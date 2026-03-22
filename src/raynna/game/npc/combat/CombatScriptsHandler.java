package raynna.game.npc.combat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import raynna.game.Entity;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.util.Logger;
import raynna.platform.PackageDiscovery;
import raynna.platform.PackageLayout;

public class CombatScriptsHandler {

	private static final HashMap<Object, CombatScript> cachedCombatScripts = new HashMap<Object, CombatScript>();
	private static final CombatScript DEFAULT_SCRIPT = new Default();
	protected transient Player player;

	public static void init() {
		try {
			Class<?>[] classes = PackageDiscovery.getClasses(PackageLayout.combatScriptPackages());
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
