package raynna.game.player.tasks;

import raynna.game.player.Player;
import raynna.game.player.tasks.TaskManager.Progress;

public abstract class Task {

	protected transient Player player;
	protected int taskID;
	protected Progress progress;

	public abstract void start();

	public abstract void finish();

}
