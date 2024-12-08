package me.adamix.mercury.server.task;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.data.PlayerData;
import me.adamix.mercury.server.player.state.PlayerState;
import me.adamix.mercury.server.task.core.MercuryTask;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

public class PlayTimeTask implements MercuryTask {
	private Task task;

	@Override
	public void start() {
		task = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
			for (MercuryPlayer onlinePlayer : Server.getOnlinePlayers()) {
				if (onlinePlayer.getState() == PlayerState.PLAY) {
					PlayerData data = onlinePlayer.getPlayerData();
					data.getStatistics().increase("play_time", 1);
				}
			}
		},  TaskSchedule.tick(1), TaskSchedule.seconds(1));
	}

	@Override
	public void cancel() {
		task.cancel();
		task = null;
	}
}
