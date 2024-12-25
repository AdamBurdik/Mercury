package me.adamix.mercury.server.monitor;

import lombok.Getter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.dungeon.Dungeon;
import me.adamix.mercury.server.dungeon.DungeonManager;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
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
		MinecraftServer.getGlobalEventHandler().addListener(ServerTickMonitorEvent.class, event -> lastTick = event.getTickMonitor());
	}

	public void stop() {
		task.cancel();
	}

	private void run() {
		Collection<MercuryPlayer> players = Server.getOnlinePlayers();
		if (players.isEmpty()) return;

		final Runtime runtime = Runtime.getRuntime();
		final TickMonitor tickMonitor = lastTick;
		this.ramUsage = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
		this.tickTime = MathUtils.round(tickMonitor.getTickTime(), 2);

		final Component header = Component.newline()
				.append(Component.text("RAM USAGE: " + ramUsage + " MB", NamedTextColor.GRAY)).appendNewline()
				.append(Component.text("TICK TIME: " + tickTime + "ms", NamedTextColor.GRAY)).appendNewline()
				.append(Component.text("TPS: " + Math.min(20, (int) (1000 / tickTime)), NamedTextColor.GRAY)).appendNewline();

		final Component footer = Component.newline()
				.append(Component.text("          Mercury          ")
						.color(TextColor.color(57, 200, 73))
						.append(Component.newline())
				);

		DungeonManager dungeonManager = Server.getDungeonManager();
		for (MercuryPlayer player : players) {
			Dungeon dungeon = dungeonManager.getDungeon(player.getDungeonUniqueId());

			Component playerFooter = footer;
			if (dungeon != null) {
				playerFooter = footer.append(
						Component.text("Dungeon: " + dungeon.getDungeonID() + "-" + player.getDungeonUniqueId())
				).append(Component.newline());
			}

			player.sendPlayerListHeaderAndFooter(header, playerFooter);

		}
	}
}
