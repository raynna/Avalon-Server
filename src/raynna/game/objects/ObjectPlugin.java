package raynna.game.objects;

import raynna.game.WorldObject;
import raynna.game.item.Item;
import raynna.game.player.Player;
import raynna.util.Logger;
import raynna.data.rscm.Rscm;

public abstract class ObjectPlugin {

	public abstract Object[] getKeys();

	public boolean processObject(Player player, WorldObject object) {
		return false;
	}

	public boolean processObject2(Player player, WorldObject object) {
		return false;
	}

	public boolean processObject3(Player player, WorldObject object) {
		return false;
	}

	public boolean processObject4(Player player, WorldObject object) {
		return false;
	}

	public boolean processObject5(Player player, WorldObject object) {
		return false;
	}

	public boolean processItemOnObject(Player player, WorldObject object, Item item) {
		return false;
	}

	public boolean processObject(Player player, WorldObject object, String option) {
		return false;
	}

	public int getDistance() {
		return 0;
	}

	/*
	 * =============================
	 * Helper Methods
	 * =============================
	 */

	public boolean objectContains(WorldObject object, String name) {
		return object.getName().toLowerCase().contains(name.toLowerCase());
	}

	public boolean objectMatches(WorldObject object, Object ref) {
		return object.getId() == resolveObject(ref);
	}

	private int resolveObject(Object ref) {

		if (ref instanceof Integer id)
			return id;

		if (ref instanceof String key)
			return Rscm.lookup(key);

		throw new IllegalArgumentException(
				"Object reference must be Integer or String, got: " + ref.getClass()
		);
	}

	/*
	 * =============================
	 * Logging
	 * =============================
	 */

	public void sendPluginLog(int option, WorldObject object, String optionName, boolean executed) {

		StringBuilder builder = new StringBuilder();

		builder.append("Option ").append(option)
				.append(" - Class: ")
				.append(this.getClass().getSimpleName())
				.append(".java, ");

		if (executed) {

			builder.append("Executed: '")
					.append(optionName)
					.append("' on ")
					.append(object.getName())
					.append("(")
					.append(object.getId())
					.append(")");

		} else {

			builder.append("Failed: '")
					.append(optionName)
					.append("' option is unhandled in plugin ")
					.append(object.getName())
					.append("(")
					.append(object.getId())
					.append(")");
		}

		Logger.log("ObjectPlugin", builder);
	}
}