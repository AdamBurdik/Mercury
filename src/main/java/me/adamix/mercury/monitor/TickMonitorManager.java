package me.adamix.mercury.monitor;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.entity.Player;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.MathUtils;

import java.util.Collection;

// Taken from https://github.com/AtlasEngineCa/WorldSeedEntityEngine/blob/master/src/test/java/Main.java#L132
/**
 * Manager for displaying tps and ram usage in tab
 */
@SuppressWarnings("UnstableApiUsage")
public class TickMonitorManager {

	private TickMonitor lastTick;
	private Task task;
	@Getter private long ramUsage = 0L;
	@Getter private double tickTime = 0L;


	public void start() {

		this.task = MinecraftServer.getSchedulerManager().scheduleTask(
				this::run,
				TaskSchedule.tick(10),
				TaskSchedule.tick(10)
		);
		MinecraftServer.getGlobalEventHandler().addListener(ServerTickMonitorEvent.class, event -> {
			lastTick = event.getTickMonitor();
		});
	}

	public void stop() {
		task.cancel();
	}

	private void run() {
		Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
		if (players.isEmpty()) return;

		final Runtime runtime = Runtime.getRuntime();
		final TickMonitor tickMonitor = lastTick;
		this.ramUsage = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
		this.tickTime = MathUtils.round(tickMonitor.getTickTime(), 2);

		final Component header = Component.newline()
				.append(Component.text("RAM USAGE: " + ramUsage + " MB", NamedTextColor.GRAY).append(Component.newline())
						.append(Component.text("TICK TIME: " + tickTime + "ms", NamedTextColor.GRAY))).append(Component.newline());

		final Component footer = Component.newline()
				.append(Component.text("          Mercury          ")
						.color(TextColor.color(57, 200, 73))
						.append(Component.newline()));

		Audiences.players().sendPlayerListHeaderAndFooter(header, footer);
	}
}
