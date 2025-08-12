package com.rs.java.game.player.tasks;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.rs.java.game.player.Player;
import com.rs.java.utils.Logger;

public class TaskManager {

	private ArrayList<Tasks> tasks = new ArrayList<Tasks>(
			Tasks.tasks.size());

	public TaskManager(Player player) {

	}

	public void startTask(Object task, Object[] parameters) {
		if (task == null) {
			return;
		}
		Task M = Tasks.getTask(task);
		tasks.set(M.taskID, (Tasks) task);
	}

	public void init() {
		refreshTasks();
	}

	public void refreshTasks() {
		// TODO Auto-generated method stub
	}

	public ArrayList<Tasks> getTasks() {
		return tasks;
	}

	public void setTask(ArrayList<Tasks> tasks) {
		this.tasks = tasks;
	}

	public static enum Progress {

		STARTED,

		PROGRESS,

		COMPLETED;

	}

	public static class Tasks {

		private static final Map<Object, Class<? extends Task>> tasks = new HashMap<>();

		public static Task getTask(Object key) {
			if (key instanceof Task) {
				return (Task) key;
			}
			Class<? extends Task> taskClass = tasks.get(key);
			if (taskClass == null) {
				Logger.log("TaskManager", "No task class found for key: " + key);
				return null;
			}
			try {
				return taskClass.getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
					 InvocationTargetException e) {
				Logger.handle(e);
			}
			return null;
		}
    }
}