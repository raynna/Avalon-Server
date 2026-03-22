package raynna.game;

import raynna.core.cache.defintions.ObjectDefinitions;
import raynna.data.rscm.Rscm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class WorldObject extends WorldTile {

	private int id;
	private int type;
	private int rotation;
	private int life;
	private int lastX;
	private int lastY;
	private Map<String, Object> metadata;
	
	public WorldObject(int id, int type, int rotation, WorldTile tile) {
		super(tile.getX(), tile.getY(), tile.getPlane());
		this.id = id;
		this.type = type;
		this.rotation = rotation;
		this.life = 1;
	}

	public WorldObject(int id, int type, int rotation, int x, int y, int plane) {
		super(x, y, plane);
		this.id = id;
		this.type = type;
		this.rotation = rotation;
		this.life = 1;
	}

	public WorldObject(int id, int type, int rotation, int x, int y, int plane, int life) {
		super(x, y, plane);
		this.id = id;
		this.type = type;
		this.rotation = rotation;
		this.life = life;
	}
	
	public WorldObject(int id, int type, int rotation, int x, int y, int plane, int lastX, int lastY) {
		super(x, y, plane);
		this.id = id;
		this.type = type;
		this.rotation = rotation;
		this.setLastX(lastX);
		this.setLastY(lastY);
	}

	public WorldObject(WorldObject object) {
		super(object.getX(), object.getY(), object.getPlane());
		this.id = object.id;
		this.type = object.type;
		this.rotation = object.rotation;
		this.life = object.life;
	}
	
	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public void decrementObjectLife() {
		this.life--;
	}

	public ObjectDefinitions getDefinitions() {
		return ObjectDefinitions.getObjectDefinitions(id);
	}
	
	public String getName() {
		return getDefinitions().name;
	}
	
	public int getConfigByFile() {
		return getDefinitions().configFileId;
	}
	
	public int getConfig() {
		return getDefinitions().configId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLastX() {
		return lastX;
	}

	public void setLastX(int lastX) {
		this.lastX = lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public void setLastY(int lastY) {
		this.lastY = lastY;
	}

	public void setMeta(String key, Object value) {
		if (metadata == null)
			metadata = new HashMap<>();
		metadata.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getMeta(String key) {
		if (metadata == null)
			return null;
		return (T) metadata.get(key);
	}

	public boolean hasMeta(String key) {
		return metadata != null && metadata.containsKey(key);
	}

	public void removeMeta(String key) {
		if (metadata != null)
			metadata.remove(key);
	}

	public void clearMeta() {
		if (metadata != null)
			metadata.clear();
	}

	public boolean hasOption(String option) {
		ObjectDefinitions definition = getDefinitions();

		if (definition.options == null)
			return false;

		for (int i = 0; i < definition.options.length; i++) {
			if (option.equalsIgnoreCase(definition.options[i]))
				return true;
		}

		return false;
	}

	public boolean hasAnyOption(String... options) {
		for (String option : options) {
			if (hasOption(option))
				return true;
		}
		return false;
	}

	private static String normalizeObjectKey(String name) {
		return name.startsWith("object.") ? name : "object." + name;
	}

	private static int resolveId(Object obj) {

		if (obj instanceof Integer id)
			return id;

		if (obj instanceof String name)
			return Rscm.lookup(normalizeObjectKey(name));

		throw new IllegalArgumentException(
				"Object must be Integer or String, got: " + obj.getClass()
		);
	}

	public static int getId(String name) {
		return Rscm.lookup(normalizeObjectKey(name));
	}

	public static List<Integer> getIds(Object... ids) {
		return Arrays.stream(ids)
				.map(WorldObject::resolveId)
				.toList();
	}

	public static int[] getIdsArray(String... names) {
		return Arrays.stream(names)
				.map(WorldObject::normalizeObjectKey)
				.mapToInt(Rscm::lookup)
				.toArray();
	}

	public boolean isObject(Object... objects) {

		int id = getId();
		String name = getName().toLowerCase();

		for (Object obj : objects) {

			if (obj instanceof Integer objectId) {
				if (objectId == id)
					return true;
			}

			else if (obj instanceof String str) {

				str = str.toLowerCase();

				if (str.startsWith("object_group.")) {

					if (Rscm.lookupList(str).contains(id))
						return true;

				}

				else if (str.startsWith("object.")) {

					if (Rscm.lookup(str) == id)
						return true;

				}

				else if (name.contains(str)) {

					return true;

				}

			}

			else {
				throw new IllegalArgumentException(
						"Object key must be Integer or String, got: " + obj.getClass()
				);
			}
		}

		return false;
	}
}
