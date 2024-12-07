package me.adamix.mercury.server.command.server;

import me.adamix.mercury.server.Server;
import net.minestom.server.command.builder.Command;

public class StopCommand extends Command {
	public StopCommand() {
		super("stop");

		setDefaultExecutor((sender, ctx) -> {
			Server.stop();
		});
	}
}
