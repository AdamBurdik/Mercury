package me.adamix.mercury.server.task;

import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.state.PlayerState;
import me.adamix.mercury.server.task.core.MercuryTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
				if (player.getState() == PlayerState.PRE_INIT) {
					player.sendActionBar(
							Component.text("Data is loading... please wait")
									.color(TextColor.color(ColorPallet.AQUA.getColor()))
					);
				}

				if (!player.getState().isPlayable()) {
					continue;
				}
			}
		}, TaskSchedule.tick(1), TaskSchedule.tick(5));
	}

	@Override
	public void cancel() {
		task.cancel();;
		task = null;
	}
}
