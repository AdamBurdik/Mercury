package me.adamix.mercury.command;

import me.adamix.mercury.Server;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.command.builder.Command;

public class InventoryCommand extends Command {
	public InventoryCommand() {
		super("inventory");

		setDefaultExecutor((sender, ctx) -> {
			if (!(sender instanceof GamePlayer player)) {
				return;
			}

			Server.getInventoryManager().open("profile_selection", player);

		});
	}
}
