package me.adamix.mercury.server.task;

import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.state.PlayerState;
import me.adamix.mercury.server.task.core.MercuryTask;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

public class PlayerTickTask implements MercuryTask {
	private Task task;

	@Override
	public void start() {
		task = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
			for (@NotNull Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
				MercuryPlayer player = MercuryPlayer.of(onlinePlayer);
				if (player.getState() == PlayerState.INIT || player.getState() == PlayerState.LIMBO) {
					continue;
				}
				player.getSidebar().update(player);
			}
		}, TaskSchedule.tick(1), TaskSchedule.tick(20));
	}

	@Override
	public void cancel() {
		task.cancel();;
		task = null;
	}
}
