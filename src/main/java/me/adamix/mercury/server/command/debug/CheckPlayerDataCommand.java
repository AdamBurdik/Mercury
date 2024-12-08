package me.adamix.mercury.server.command.debug;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.GamePlayer;
import net.minestom.server.command.builder.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckPlayerDataCommand extends Command {
	private static Logger LOGGER = LoggerFactory.getLogger(CheckPlayerDataCommand.class);

	public CheckPlayerDataCommand() {
		super("check_data");

		setDefaultExecutor((sender, ctx) -> {
			for (GamePlayer onlinePlayer : Server.getOnlinePlayers()) {
				LOGGER.info("{} data = {}", onlinePlayer.getUsername(), onlinePlayer.getPlayerData());
			}
		});
	}
}
