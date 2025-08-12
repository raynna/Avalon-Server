package com.rs.java.game.player.dialogues;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;

public final class DialogueSkillingHandler {

	private static final HashMap<Object, Class<? extends Dialogue>> handledDialogues = new HashMap<Object, Class<? extends Dialogue>>();

	@SuppressWarnings(
	{ "unchecked" })
	public static void init() {
		try {
			Class<Dialogue>[] classes = Utils.getClasses("com.rs.java.game.player.dialogues.skilling");
			for (Class<Dialogue> c : classes) {
				if (c.isAnonymousClass()) // next
					continue;
				handledDialogues.put(c.getSimpleName(), c);
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static void reload() {
		handledDialogues.clear();
		init();
	}

	public static Dialogue getDialogue(Object key) {
		if (key instanceof Dialogue)
			return (Dialogue) key;
		Class<? extends Dialogue> classD = handledDialogues.get(key);
		if (classD == null)
			return null;
		try {
			return classD.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			Logger.handle(e);
		}
		return null;
	}


	private DialogueSkillingHandler() {

	}
}
