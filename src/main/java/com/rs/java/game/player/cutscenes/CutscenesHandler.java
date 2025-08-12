package com.rs.java.game.player.cutscenes;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.rs.java.utils.Logger;

public class CutscenesHandler {

	private static final HashMap<Object, Class<Cutscene>> handledCutscenes = new HashMap<Object, Class<Cutscene>>();

	@SuppressWarnings("unchecked")
	public static void init() {
		try {
			Class<Cutscene> value1 = (Class<Cutscene>) Class.forName(EdgeWilderness.class.getCanonicalName());
			handledCutscenes.put("EdgeWilderness", value1);
			Class<Cutscene> value2 = (Class<Cutscene>) Class.forName(DTPreview.class.getCanonicalName());
			handledCutscenes.put("DTPreview", value2);
			Class<Cutscene> value3 = (Class<Cutscene>) Class.forName(NexCutScene.class.getCanonicalName());
			handledCutscenes.put("NexCutScene", value3);
			Class<Cutscene> value4 = (Class<Cutscene>) Class.forName(TowersPkCutscene.class.getCanonicalName());
			handledCutscenes.put("TowersPkCutscene", value4);
			Class<Cutscene> value5 = (Class<Cutscene>) Class.forName(HomeCutScene.class.getCanonicalName());
			handledCutscenes.put("HomeCutScene", value5);
			Class<Cutscene> value6 = (Class<Cutscene>) Class.forName(MasterOfFear.class.getCanonicalName());
			handledCutscenes.put("MasterOfFear", value6);
			Class<Cutscene> value7 = (Class<Cutscene>) Class.forName(CorporealBeastScene.class.getCanonicalName());
			handledCutscenes.put("CorporealBeastScene", value7);
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static void reload() {
		handledCutscenes.clear();
		init();
	}

	public static Cutscene getCutscene(Object key) {
		Class<Cutscene> classC = handledCutscenes.get(key);
		if (classC == null)
			return null;
		try {
			return classC.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			Logger.handle(e);
		}
		return null;
	}

}
