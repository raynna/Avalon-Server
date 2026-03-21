package com.rs.core.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WorldTasksManager {

	private static final int TASK_WARNING_THRESHOLD = 100;
	private static final int TASK_WARNING_COOLDOWN_TICKS = 25;
	private static int tickCounter;
	private static int lastWarningTick = -TASK_WARNING_COOLDOWN_TICKS;

	private static final List<WorldTaskInformation> tasks = Collections
			.synchronizedList(new LinkedList<WorldTaskInformation>());

	public static void processTasks() {
		tickCounter++;
		if (tasks.size() > TASK_WARNING_THRESHOLD && tickCounter - lastWarningTick >= TASK_WARNING_COOLDOWN_TICKS) {
			lastWarningTick = tickCounter;
			System.out.println("[WorldTasks WARNING] task count=" + tasks.size() + " top=" + summarizeTopTasks());
		}
		for (WorldTaskInformation taskInformation :
				tasks.toArray(new WorldTaskInformation[tasks.size()])) {

			if (taskInformation.continueCount > 0) {
				taskInformation.continueCount--;
				continue;
			}

			long start = System.currentTimeMillis();

			try {
				taskInformation.task.run();
			} catch (Throwable e) {
				System.err.println("WorldTask crashed: " + taskInformation.task.getClass().getName());
				e.printStackTrace();
				tasks.remove(taskInformation);
				continue;
			}

			long took = System.currentTimeMillis() - start;

			if (took > 200) {
				System.out.println("[SlowWorldTask] " +
						taskInformation.task.getClass().getName() +
						" took " + took + " ms");
			}

			if (taskInformation.task.needRemove) {
				tasks.remove(taskInformation);
			} else {
				taskInformation.continueCount = taskInformation.continueMaxCount;
			}
		}
	}

	public static void main(String[] args) {
		processTasks();
		processTasks();
		processTasks();

	}

	public static void runOnGameThread(Runnable r) {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				r.run();
			}
		});
	}

	public static void schedule(int ticks, int count, Runnable runnable) {
		if (runnable == null)
			return;

		schedule(new WorldTask() {
			@Override
			public void run() {
				runnable.run();
			}
		}, ticks, count);
	}

	public static void schedule(int ticks, Runnable runnable) {
		if (runnable == null)
			return;

		schedule(new WorldTask() {
			@Override
			public void run() {
				runnable.run();
			}
		}, ticks - 1);
	}

	public static void schedule(Runnable runnable) {
		if (runnable == null)
			return;

		schedule(new WorldTask() {
			@Override
			public void run() {
				runnable.run();
			}
		});
	}



	public static void schedule(WorldTask task, int ticks, int count) {
		if (task == null || ticks < 0 || count < 0)
			return;
		tasks.add(new WorldTaskInformation(task, ticks, count));
	}

	public static void schedule(WorldTask task, int ticks) {
		if (task == null || ticks < 0)
			return;
		tasks.add(new WorldTaskInformation(task, ticks, -1));
	}

	public static void schedule(WorldTask task) {
		if (task == null)
			return;
		tasks.add(new WorldTaskInformation(task, 0, -1));
	}

	public static int getTasksCount() {
		return tasks.size();
	}

	public WorldTasksManager() {

	}

	private static String summarizeTopTasks() {
		Map<String, Integer> counts = new HashMap<>();
		for (WorldTaskInformation info : tasks.toArray(new WorldTaskInformation[tasks.size()])) {
			String className = info.task.getClass().getName();
			if (info.continueMaxCount >= 0) {
				className += "[repeat:" + info.continueMaxCount + "]";
			}
			counts.merge(className, 1, Integer::sum);
		}
		List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
		entries.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
				.thenComparing(Map.Entry::getKey));
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < entries.size() && i < 5; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			Map.Entry<String, Integer> entry = entries.get(i);
			builder.append(entry.getKey()).append("=").append(entry.getValue());
		}
		return builder.toString();
	}

	private static final class WorldTaskInformation {

		private WorldTask task;
		private int continueMaxCount;
		private int continueCount;

		public WorldTaskInformation(WorldTask task, int continueCount, int continueMaxCount) {
			this.task = task;
			this.continueCount = continueCount;
			this.continueMaxCount = continueMaxCount;
			if (continueMaxCount == -1)
				task.needRemove = true;
		}
	}
}
