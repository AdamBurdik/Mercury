package me.adamix.mercury.server.task.core;

import java.util.HashSet;
import java.util.Set;

public class TaskManager {
	private final Set<MercuryTask> mercuryTaskSet = new HashSet<>();

	public void startTask(MercuryTask mercuryTask) {
		mercuryTaskSet.add(mercuryTask);
		mercuryTask.start();
	}

	public void stopAllTasks() {
		for (MercuryTask mercuryTask : mercuryTaskSet) {
			mercuryTask.cancel();
		}
		mercuryTaskSet.clear();
	}
}
