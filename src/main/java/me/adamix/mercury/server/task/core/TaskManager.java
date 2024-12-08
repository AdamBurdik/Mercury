package me.adamix.mercury.server.task.core;

import java.util.HashSet;
import java.util.Set;

public class TaskManager {
	private final Set<GameTask> gameTaskSet = new HashSet<>();

	public void startTask(GameTask gameTask) {
		gameTaskSet.add(gameTask);
		gameTask.start();
	}

	public void stopAllTasks() {
		for (GameTask gameTask : gameTaskSet) {
			gameTask.cancel();
		}
		gameTaskSet.clear();
	}
}
