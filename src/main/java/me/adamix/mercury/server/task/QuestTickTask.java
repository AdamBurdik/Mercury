package me.adamix.mercury.server.task;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.task.core.MercuryTask;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

public class QuestTickTask implements MercuryTask {
	private Task task;

	@Override
	public void start() {
		task = MinecraftServer.getSchedulerManager().scheduleTask(() ->{
			Server.getQuestManager().tickQuests();
		}, TaskSchedule.tick(1), TaskSchedule.tick(10));
	}

	@Override
	public void cancel() {
		task.cancel();
		task = null;
	}
}
