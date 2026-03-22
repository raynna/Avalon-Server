package raynna.game.player.content.customtab;

import java.text.DecimalFormat;

import raynna.game.player.Player;
import raynna.util.Utils;
import raynna.game.player.tasksystem.Task;

public class AchievementsTab extends CustomTab {

	private static final AchievementsStore[] ACHIEVEMENT_STORES = AchievementsStore.values();

	public enum AchievementsStore {

		TITLE(25, "Achievements"),

		EASY_TASKS(3, "Easy Achievements"),

		MEDIUM_TASKS(4, "Medium Achievements"),

		HARD_TASKS(5, "Hard Achievements"),

		ELITE_TASKS(6, "Elite Achievements");

		private final int compId;
		private final String text;

		AchievementsStore(int compId, String text) {
			this.compId = compId;
			this.text = text;
		}
	}

	private final static int MARKED_1 = 3893, UNMARKED_1 = 3894, MARKED_2 = 3895, UNMARKED_2 = 3896,
			MARKED_3 = 3897, UNMARKED_3 = 3898, MARKED_4 = 3899, UNMARKED_4 = 3900;

	private static void sendComponentButtons(Player player) {
		player.getPackets().sendHideIComponent(3002, BACK_BUTTON, true);
		player.getPackets().sendHideIComponent(3002, FORWARD_BUTTON, true);
		player.getPackets().sendHideIComponent(3002, 24, false);

		player.getPackets().sendHideIComponent(3002, BLUE_STAR_COMP, false);
		player.getPackets().sendSpriteOnIComponent(3002, BLUE_STAR_COMP, 1820);

		player.getPackets().sendHideIComponent(3002, GREEN_STAR_COMP, false);
		player.getPackets().sendSpriteOnIComponent(3002, GREEN_STAR_COMP, UNMARKED_1);

		player.getPackets().sendHideIComponent(3002, RED_STAR_COMP, false);
		player.getPackets().sendSpriteOnIComponent(3002, RED_STAR_COMP, UNMARKED_2);

		player.getPackets().sendHideIComponent(3002, PURPLE_STAR_COMP, false);
		player.getPackets().sendSpriteOnIComponent(3002, PURPLE_STAR_COMP, UNMARKED_3);

		player.getPackets().sendHideIComponent(3002, YELLOW_STAR_COMP, false);
		player.getPackets().sendSpriteOnIComponent(3002, YELLOW_STAR_COMP, UNMARKED_4);
	}

	public static void open(Player player) {
		sendComponentButtons(player);

		for (int i = 3; i <= 22; i++)
			player.getPackets().sendHideIComponent(3002, i, true);

		for (int i = 28; i <= 56; i++)
			player.getPackets().sendHideIComponent(3002, i, true);

		player.getTemporaryAttributtes().put("ACHIEVEMENTTAB", 0);
		player.getTemporaryAttributtes().remove("ACHIEVEMENTCATEGORY");
		player.getTemporaryAttributtes().remove("GEARTAB");

		completedTasks = 0;
		totalTasks = 0;
		totalActions = 0;
		currentActions = 0;

		for (Task task : Task.getEntries()) {

			currentActions += task.getAmount() > 0 ? player.getTaskManager().stage(task)
					: player.getTaskManager().isCompleted(task) ? 1 : 0;

			totalActions += Math.max(task.getAmount(), 1);

			if (player.getTaskManager().isCompleted(task))
				completedTasks++;

			totalTasks++;
		}

		for (AchievementsStore store : ACHIEVEMENT_STORES) {
			player.getPackets().sendHideIComponent(3002, store.compId, false);
			player.getPackets().sendTextOnComponent(3002, store.compId, store.text);
		}

		double percentage = getPercentage(currentActions, totalActions);

		player.getPackets().sendTextOnComponent(3002, 24,
				completedTasks + "/" + totalTasks
						+ (percentage == 100 ? "" : " ")
						+ (percentage == 100 ? "<col=04BB3B>" : percentage == 0 ? "<col=BB0404>" : "<col=FFF300>")
						+ (percentage == 100 ? "100%" : new DecimalFormat("##.##").format(percentage) + "%"));

		if (percentage == 100) {
			player.getPackets().sendHideIComponent(3002, 10, false);
			player.getPackets().sendTextOnComponent(3002, 10,
					"<col=04BB3B>You completed all " + totalTasks + " tasks!");
		}

		refreshScrollbar(player, ACHIEVEMENT_STORES.length);
	}

	public static void handleButtons(Player player, int compId) {

		String category = (String) player.temporaryAttribute().get("ACHIEVEMENTCATEGORY");

		if (compId == BACK_BUTTON) {
			if (category != null) {
                switch (category) {
                    case "elite" -> openTasks(player, "hard");
                    case "hard" -> openTasks(player, "medium");
                    case "medium" -> openTasks(player, "easy");
                    case "easy" -> open(player);
                }
				return;
			}
		}

		if (compId == FORWARD_BUTTON) {
			if (category != null) {
                switch (category) {
                    case "easy" -> openTasks(player, "medium");
                    case "medium" -> openTasks(player, "hard");
                    case "hard" -> openTasks(player, "elite");
                }
				return;
			}
		}

		if (category != null) {

			int i = 3;

			for (Task task : Task.getEntries()) {

				if (!task.getDifficulty().name().equalsIgnoreCase(category))
					continue;

				if (compId == i) {

					player.getPackets().sendGameMessage(
							Utils.formatString(task.getDifficulty().name()) + " Achievement: "
									+ (player.getTaskManager().isCompleted(task) ? "<str>" : "")
									+ Utils.formatString(task.name()).replace("$", "'")
									+ (player.getTaskManager().isCompleted(task)
									? " - Completed!"
									: (task.getAmount() > 1
									? " (" + player.getTaskManager().stage(task) + "/"
									+ task.getAmount() + ")"
									: ".")));
				}
				i++;
			}

		} else {

			player.temporaryAttribute().remove("ACHIEVEMENTCATEGORY");

			for (AchievementsStore store : ACHIEVEMENT_STORES) {

				if (compId == store.compId) {

					String c = store.text.toLowerCase().replace(" achievements", "").trim();

					openTasks(player, c);
				}
			}
		}

		switch (compId) {

			case BLUE_STAR_COMP:
				open(player);
				break;

			case GREEN_STAR_COMP:
				openTasks(player, "easy");
				break;

			case RED_STAR_COMP:
				openTasks(player, "medium");
				break;

			case PURPLE_STAR_COMP:
				openTasks(player, "hard");
				break;

			case YELLOW_STAR_COMP:
				openTasks(player, "elite");
				break;

			default:
				break;
		}
	}

	static int completedTasks;
	static int totalTasks;
	static int totalActions;
	static int currentActions;

	public static void openTasks(Player player, String category) {

		for (int i = 3; i <= 56; i++) {
			if (i >= 23 && i <= 27)
				continue;
			player.getPackets().sendHideIComponent(3002, i, true);
		}

		player.getTemporaryAttributtes().put("ACHIEVEMENTCATEGORY", category);

		sendComponentButtons(player);

		if (!category.equals("elite"))
			player.getPackets().sendHideIComponent(3002, FORWARD_BUTTON, false);

		player.getPackets().sendHideIComponent(3002, BACK_BUTTON, false);

        switch (category) {
            case "easy" -> player.getPackets().sendSpriteOnIComponent(3002, GREEN_STAR_COMP, MARKED_1);
            case "medium" -> player.getPackets().sendSpriteOnIComponent(3002, RED_STAR_COMP, MARKED_2);
            case "hard" -> player.getPackets().sendSpriteOnIComponent(3002, PURPLE_STAR_COMP, MARKED_3);
            case "elite" -> player.getPackets().sendSpriteOnIComponent(3002, YELLOW_STAR_COMP, MARKED_4);
        }

		int i = 3;

		completedTasks = 0;
		totalTasks = 0;
		totalActions = 0;
		currentActions = 0;

		for (Task store : Task.getEntries()) {

			if (!store.getDifficulty().name().equalsIgnoreCase(category))
				continue;

			currentActions += store.getAmount() > 0
					? player.getTaskManager().stage(store)
					: player.getTaskManager().isCompleted(store) ? 1 : 0;

			totalActions += Math.max(store.getAmount(), 1);

			totalTasks++;

			if (player.getTaskManager().isCompleted(store))
				completedTasks++;

			player.getPackets().sendHideIComponent(3002, i, false);

			player.getPackets().sendTextOnComponent(3002, i,
					(player.getTaskManager().isCompleted(store)
							? "<col=04BB3B>"
							: player.getTaskManager().stage(store) > 0 ? "<col=FFF300>" : "<col=BB0404>")
							+ Utils.formatString(store.name()).replace("$", "'")
							+ (store.getAmount() > 1 && !player.getTaskManager().isCompleted(store)
							? " (" + player.getTaskManager().stage(store) + "/" + store.getAmount() + ")"
							: ""));

			player.getPackets().sendTextOnComponent(3002, 25, Utils.formatString(category));

			i++;
		}

		refreshScrollbar(player, totalTasks);

		double percentage = getPercentage(currentActions, totalActions);

		player.getPackets().sendTextOnComponent(3002, 24,
				completedTasks + "/" + totalTasks
						+ (percentage == 100 ? "" : " ")
						+ (percentage == 100 ? "<col=04BB3B>" : percentage == 0 ? "<col=BB0404>" : "<col=FFF300>")
						+ (percentage == 100 ? "100%" : new DecimalFormat("##.#").format(percentage) + "%"));
	}

	public static double getPercentage(double completed, double totaltasks) {
		return (completed / totaltasks) * 100;
	}
}