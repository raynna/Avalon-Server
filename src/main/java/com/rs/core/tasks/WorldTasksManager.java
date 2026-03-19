package com.rs.core.tasks;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WorldTasksManager {

	private static final List<WorldTaskInformation> tasks = Collections
			.synchronizedList(new LinkedList<WorldTaskInformation>());

	public static void processTasks() {
		if (tasks.size() > 100) {
			System.out.println("[WorldTasks WARNING] task count=" + tasks.size());
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
