package me.adamix.mercury.server.task;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.GamePlayer;
import me.adamix.mercury.server.player.data.PlayerData;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

public class PlayTimeTask {
	private Task task;

	public void start() {
		task = MinecraftServer.getSchedulerManager().scheduleTask(() -> {

			for (GamePlayer onlinePlayer : Server.getOnlinePlayers()) {
				PlayerData data = onlinePlayer.getPlayerData();
				if (data != null) {
					onlinePlayer.getPlayerData().increasePlayTime(1);
				}
			}

		},  TaskSchedule.tick(1), TaskSchedule.seconds(1));
	}

	public void stop() {
		task.cancel();
		task = null;
	}
}
