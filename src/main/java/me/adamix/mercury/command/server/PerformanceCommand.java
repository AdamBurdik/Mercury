package me.adamix.mercury.command.server;

import me.adamix.mercury.Server;
import me.adamix.mercury.common.ColorPallet;
import me.adamix.mercury.monitor.TickMonitorManager;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;

public class PerformanceCommand extends Command {
	public PerformanceCommand() {
		super("performance", "tps", "mspt");

		setDefaultExecutor((sender, ctx) -> {
			TickMonitorManager monitorManager = Server.getTickMonitorManager();

			Component component = Component.text()
					.content("Tick Time: ")
					.color(ColorPallet.DARK_GREEN.getColor())
					.append(
							Component.text(monitorManager.getTickTime() + "ms")
									.color(ColorPallet.GREEN.getColor())
					)
					.appendNewline()
					.append(
							Component.text("Ram Usage: ")
									.color(ColorPallet.DARK_GREEN.getColor())
									.append(
											Component.text(monitorManager.getRamUsage() + "mb")
													.color(ColorPallet.GREEN.getColor())
									)
					)
					.build();
			sender.sendMessage(component);
		});
	}
}
